package app

import model.*
import service.IServSeguros
import service.IServUsuarios
import ui.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Clase encargada de gestionar el flujo de menús y opciones de la aplicación,
 * mostrando las acciones disponibles según el perfil del usuario autenticado.
 *
 * @property nombreUsuario Nombre del usuario que ha iniciado sesión.
 * @property perfilUsuario Perfil del usuario: admin, gestion o consulta.
 * @property ui Interfaz de usuario.
 * @property gestorUsuarios Servicio de operaciones sobre usuarios.
 * @property gestorSeguros Servicio de operaciones sobre seguros.
 */
class GestorMenu(
    private val nombreUsuario: String,
    private val perfilUsuario: Perfil,
    private val ui: IEntradaSalida,
    private val gestorUsuarios: IServUsuarios,
    private val gestorSeguros: IServSeguros
) {

    /**
     * Inicia un menú según el índice correspondiente al perfil actual.
     *
     * @param indice Índice del menú que se desea mostrar (0 = principal).
     */
    fun iniciarMenu(indice: Int = 0) {
        val (opciones, acciones) = ConfiguracionesApp.obtenerMenuYAcciones(perfilUsuario.name.lowercase(), indice)
        ejecutarMenu(opciones, acciones)
    }

    /**
     * Formatea el menú en forma numerada.
     */
    private fun formatearMenu(opciones: List<String>): String {
        var cadena = ""
        opciones.forEachIndexed { index, opcion ->
            cadena += "${index + 1}. $opcion\n"
        }
        return cadena
    }

    /**
     * Muestra el menú limpiando pantalla y mostrando las opciones numeradas.
     */
    private fun mostrarMenu(opciones: List<String>) {
        ui.limpiarPantalla()
        ui.mostrar(formatearMenu(opciones), salto = false)
    }

    /**
     * Ejecuta el menú interactivo.
     *
     * @param opciones Lista de opciones que se mostrarán al usuario.
     * @param ejecutar Mapa de funciones por número de opción.
     */
    private fun ejecutarMenu(opciones: List<String>, ejecutar: Map<Int, (GestorMenu) -> Boolean>) {
        do {
            mostrarMenu(opciones)
            val opcion = ui.pedirInfo("Elige opción > ").toIntOrNull()
            if (opcion != null && opcion in 1..opciones.size) {
                // Buscar en el mapa las acciones a ejecutar en la opción de menú seleccionada
                val accion = ejecutar[opcion]
                // Si la accion ejecutada del menú retorna true, debe salir del menú
                if (accion != null && accion(this)) return
            } else {
                ui.mostrarError("Opción no válida!")
            }
        } while (true)
    }

    /** Crea un nuevo usuario solicitando los datos necesarios al usuario */
    fun nuevoUsuario() {
        var todoCorrecto = false
        while (!todoCorrecto){
            try {
                var nombreUsuario = ui.pedirInfo("Introduce un nombre para el usuario", "El nombre no puede estar vacío", {nombre: String -> nombre.isNotBlank()})

                val clave = ui.pedirInfo("Introduce una clave", "La contraseña debe tener mínimo 4 caracteres alfanuméricos", {clave -> patronClave.matches(clave)})

                val perfil = pedirPerfil()

                if (gestorUsuarios.agregarUsuario(nombreUsuario, clave, perfil)) {
                    todoCorrecto = true
                }
                else {
                    throw IllegalArgumentException("El usuario no se ha podido crear")
                }
            } catch (e: Exception) {
                ui.mostrarError(e.message.toString())
                //if (ui.preguntar("¿Quieres cancelar el proceso?")) return null;
            }
        }
        ui.mostrar("Se ha creado el usuario correctamente.",pausa = true)
    }

    /** Elimina un usuario si existe */
    fun eliminarUsuario() {
        val nombre = ui.pedirInfo("Introduce el nombre del usuario a eliminar")
        if (nombre == nombreUsuario) ui.mostrarError("No se puede eliminar el usuario actual") else {
            if (gestorUsuarios.eliminarUsuario(nombre)) ui.mostrar("Usuario eliminado correctamente", pausa = true) else ui.mostrarError("No se ha podido eliminar el usuario")
        }

    }

    /** Cambia la contraseña del usuario actual */
    fun cambiarClaveUsuario() {
        val usuario = gestorUsuarios.buscarUsuario(nombreUsuario) ?: return
        var clave: String = ""
        var claveCambiada = false
        do {
            try {
                clave = ui.pedirInfo("Introduce la nueva clave", "La contraseña debe tener mínimo 4 caracteres alfanuméricos", {patronClave.matches(it)})
                gestorUsuarios.cambiarClave(usuario, clave)
                claveCambiada = true
            } catch (e: Exception) {
                ui.mostrarError(e.message.toString())
                //if (ui.preguntar("¿Quieres cancelar el proceso?")) return null;
            }
        } while (!claveCambiada)
        ui.mostrar("Se ha cambiado la clave correctamente.",pausa = true)
    }

    /**
     * Mostrar la lista de usuarios (Todos o filtrados por un perfil)
     */
    fun consultarUsuarios() {
        val (lista, nombreLista) = if (ui.preguntar("¿Quieres una consulta global en lugar de una filtrada por perfil?")){
            Pair(gestorUsuarios.consultarTodos(), "Usuarios")
        } else {
            var perfil = ""
            while (true){
                try {
                    perfil = ui.pedirInfo("Introduce el perfil a filtrar ${Perfil.entries}", "Debe ser un perfil válido -> ${Perfil.entries}", { cadena: String -> cadena.uppercase() in Perfil.entries.map {it.name}})

                    break;
                } catch (e: Exception) {
                    ui.mostrarError(e.message.toString())
                    if (ui.preguntar("¿Quieres cancelar la consulta?")) break;
                }
            }
            Pair(gestorUsuarios.consultarPorPerfil(Perfil.getPerfil(perfil)), "Usuarios (${perfil.uppercase()})")
        }
        if (lista.isEmpty()) ui.mostrar("No hay $nombreLista existentes", pausa = true) else ui.mostrarListado("Lista de $nombreLista", lista)
    }

    private fun pedirNumPoliza(): Int{
        return ui.pedirEntero("Introduce el número de poliza a eliminar", "No hay ningún seguro con ese número de poliza","Debes introducir un número de póliza existente", {it >= 0})
    }

    /**
     * Solicita al usuario un DNI y verifica que tenga el formato correcto: 8 dígitos seguidos de una letra.
     *
     * @return El DNI introducido en mayúsculas.
     */
    private fun pedirDni(): String {
        var esValido = false
        var dni = ""
        while (!esValido) {
            try {
                dni = ui.pedirInfo("Introduce el DNI (letra en mayúsculas)", "DNI no válido (Formato incorrecto)", { patronDNI.matches(it) })
                esValido = true
            } catch (e: Exception) {
                ui.mostrarError(e.message.toString())
            }
        }
        return dni
    }

    /**
     * Solicita al usuario un importe positivo, usado para los seguros.
     *
     * @return El valor introducido como `Double` si es válido.
     */
    private fun pedirImporte(): Double{
        var importe: Double? = null
        do {
            try {
                importe = ui.pedirValorDouble("Introduce un importe positivo", "El importe no puede ser negativo", {it >= 0.0})
            } catch (e: Exception){
                ui.mostrarError(e.message.toString())
            }
        } while (importe == null)
        return importe
    }

    private fun pedirPerfil(): Perfil{
        var perfil: Perfil? = null

        do {
            try {
                perfil = Perfil.getPerfil(ui.pedirInfo("Introduce el perfil del usuario ${Perfil.entries}", "Debe ser un perfil válido -> ${Perfil.entries}", { cadena: String -> cadena.uppercase() in Perfil.entries.map {it.name}}))
            } catch (e: Exception){
                ui.mostrarError(e.message.toString())
            }
        } while (perfil == null)
        return perfil

    }

    private fun pedirTipoAuto(): Auto{
        var tipoAuto: Auto? = null

        do {
            try {
                tipoAuto = Auto.getAuto(ui.pedirInfo("Introduce el tipo de Auto ${Auto.entries}", "Debe ser un tipo de auto válido -> ${Auto.entries}", { cadena: String -> cadena.uppercase() in Auto.entries.map {it.name}}))
            } catch (e: Exception){
                ui.mostrarError(e.message.toString())
            }
        } while (tipoAuto == null)
        return tipoAuto

    }

    private fun pedirCobertura(): Cobertura{
        var cobertura: Cobertura? = null

        do {
            try {
                cobertura = Cobertura.getCobertura(ui.pedirInfo("Introduce la cobertura ${Cobertura.entries}", "Debe ser una cobertura existente -> ${Cobertura.entries}", { cadena: String -> cadena.uppercase() in Cobertura.entries.map {it.name}}))
            } catch (e: Exception){
                ui.mostrarError(e.message.toString())
            }
        } while (cobertura == null)
        return cobertura

    }

    private fun pedirNivelRiesgo(): Riesgo{
        var riesgo: Riesgo? = null

        do {
            try {
                riesgo = Riesgo.getRiesgo(ui.pedirInfo("Introduce el nivel de riesgo ${Riesgo.entries}", "Debe ser un nivel de riesgo existente -> ${Riesgo.entries}", { cadena: String -> cadena.uppercase() in Riesgo.entries.map {it.name}}))
            } catch (e: Exception){
                ui.mostrarError(e.message.toString())
            }
        } while (riesgo == null)
        return riesgo

    }

    private fun pedirFechaDeNacimiento(): LocalDate {
        var fecha = ""
        var fechaValida = false
        while (!fechaValida){
            try {
                fecha = ui.pedirInfo("Introduce la fecha de nacimiento (DD/MM/AAAA)", "Debes introducir una fecha de nacimiento válida (Hasta 125 años)", ::verificarFecha)

                fechaValida = true
            } catch (e: Exception){
                ui.mostrarError(e.message.toString())
            }
        }
        return LocalDate.parse(fecha, DateTimeFormatter.ofPattern(formatoFecha))
    }

    private fun verificarFecha(fecha: String):Boolean{
        val rangoAniosPermitidos = 125
        return if (patronFormatoFecha.matches(fecha)) {
            val fechaFormateada = LocalDate.parse(fecha, DateTimeFormatter.ofPattern(formatoFecha))
            if (fechaFormateada < LocalDate.now() && fechaFormateada.year > fechaFormateada.year - rangoAniosPermitidos) true else false
        } else false

    }

    /** Crea un nuevo seguro de hogar solicitando los datos al usuario */
    fun contratarSeguroHogar() {
        gestorSeguros.contratarSeguroHogar(pedirDni(), pedirImporte(), ui.pedirValorDouble("Introduce los m² del Hogar", "El valor no puede ser <= 0",{it > 0.0}), ui.pedirValorDouble("Introduce el valor contenido del Hogar", "El valor no puede ser negativo", {it >= 0.0}), ui.pedirCadena("Introduce la dirección del Hogar"), ui.pedirAnio("Introduce el año de construcción"))

        ui.mostrar("Se ha contratado el SeguroHogar correctamente.",pausa = true)
    }

    /** Crea un nuevo seguro de auto solicitando los datos al usuario */
    fun contratarSeguroAuto() {
        gestorSeguros.contratarSeguroAuto(pedirDni(), pedirImporte(), ui.pedirCadena("Introduce la descripción del Auto"),ui.pedirValorDouble("Introduce el combustible del Auto", "El valor no puede ser negativo", {it >= 0.0}), pedirTipoAuto(), pedirCobertura(), ui.preguntar("¿Tiene asistencia en carretera?"),ui.pedirValorInt("Introduce el número de partes del auto", "El valor no puede ser <= 0",{it > 0}))

        ui.mostrar("Se ha contratado el SeguroAuto correctamente.",pausa = true)
    }

    /** Crea un nuevo seguro de vida solicitando los datos al usuario */
    fun contratarSeguroVida() {
        gestorSeguros.contratarSeguroVida(pedirDni(), pedirImporte(), pedirFechaDeNacimiento(), pedirNivelRiesgo(), ui.pedirValorDouble("Introduce la indemnzación del seguro", "El valor no puede ser <= 0", {it > 0.0}))

        ui.mostrar("Se ha contratado el SeguroVida correctamente.",pausa = true)
    }

    /** Elimina un seguro si existe por su número de póliza */
    fun eliminarSeguro() {
        var numPoliza = 0
        var realizado = false
        do {
            try {
                numPoliza = pedirNumPoliza()
                if (gestorSeguros.eliminarSeguro(numPoliza)) ui.mostrar("Seguro eliminado correctamente", pausa = true) else ui.mostrarError("No se ha podido eliminar el seguro (No existe)")
                realizado = true
            } catch (e: Exception) {
                ui.mostrarError(e.message.toString())
                if (ui.preguntar("¿Quieres cancelar el proceso?")) return
            }
        } while (!realizado)


    }

    /** Muestra todos los seguros existentes */
    fun consultarSeguros() {
        val lista = gestorSeguros.consultarTodos()
        if (lista.isEmpty()) ui.mostrar("No hay seguros existentes", pausa = true) else ui.mostrarListado("Lista de Seguros", lista)
    }

    /** Muestra todos los seguros de tipo hogar */
    fun consultarSegurosHogar() {
        val lista = gestorSeguros.consultarPorTipo("SeguroHogar")
        if (lista.isEmpty()) ui.mostrar("No hay SegurosHogar existentes", pausa = true) else ui.mostrarListado("Lista de SegurosHogar", lista)

    }

    /** Muestra todos los seguros de tipo auto */
    fun consultarSegurosAuto() {
        val lista = gestorSeguros.consultarPorTipo("SeguroAuto")
        if (lista.isEmpty()) ui.mostrar("No hay SegurosAuto existentes", pausa = true) else ui.mostrarListado("Lista de SegurosAuto", lista)
    }

    /** Muestra todos los seguros de tipo vida */
    fun consultarSegurosVida() {
        val lista = gestorSeguros.consultarPorTipo("SeguroVida")
        if (lista.isEmpty()) ui.mostrar("No hay SegurosVida existentes", pausa = true) else ui.mostrarListado("Lista de SegurosVida",lista)

    }

    companion object {
        val patronClave = "^[A-Za-z0-9]{4,}$".toRegex()
        val patronDNI = "^\\d{8}[A-Z]$".toRegex()
        val patronFormatoFecha = "^\\d{2}/\\d{2}/\\d{4}$".toRegex()
        val formatoFecha = "dd/MM/yyyy"
    }
}
