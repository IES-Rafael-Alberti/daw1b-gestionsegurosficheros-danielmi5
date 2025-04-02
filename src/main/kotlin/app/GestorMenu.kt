package app

import model.Perfil
import service.IServSeguros
import service.IServUsuarios
import ui.*


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
        val (opciones, acciones) = ConfiguracionesApp.obtenerMenuYAcciones(perfilUsuario.toString(), indice)
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

                val perfil = Perfil.getPerfil(ui.pedirInfo("Introduce el perfil del usuario", "Debe ser un perfil válido -> ${Perfil.entries}", { cadena: String -> cadena.uppercase() in Perfil.entries.map {it.name}}))

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

    }

    /** Elimina un usuario si existe */
    fun eliminarUsuario() {
        if (gestorUsuarios.eliminarUsuario(ui.pedirInfo("Introduce el nombre del usuario a eliminar"))) ui.mostrar("Usuario eliminado correctamente") else ui.mostrarError("No se ha podido eliminar el usuario")
    }

    /** Cambia la contraseña del usuario actual */
    fun cambiarClaveUsuario() {
        val usuario = gestorUsuarios.buscarUsuario(nombreUsuario) ?: return
        var clave: String = ""
        var claveCambiada = false
        do {
            try {
                clave = ui.pedirInfo("Introduce una clave", "La contraseña debe tener mínimo 4 caracteres alfanuméricos", {clave -> patronClave.matches(clave)})
                gestorUsuarios.cambiarClave(usuario, clave)
                claveCambiada = true
            } catch (e: Exception) {
                ui.mostrarError(e.message.toString())
                //if (ui.preguntar("¿Quieres cancelar el proceso?")) return null;
            }
        } while (!claveCambiada)

    }

    /**
     * Mostrar la lista de usuarios (Todos o filtrados por un perfil)
     */
    fun consultarUsuarios() {
        if (ui.preguntar("¿Quieres una consulta global en lugar de una filtrada por perfil?")) gestorUsuarios.consultarTodos() else {
            while (true){
                try {
                    val perfil = ui.pedirInfo("Introduce el perfil a filtrar", "Debe ser un perfil válido -> ${Perfil.entries}", { cadena: String -> cadena.uppercase() in Perfil.entries.map {it.name}})

                    gestorUsuarios.consultarPorPerfil(Perfil.getPerfil(perfil))
                } catch (e: Exception) {
                    ui.mostrarError(e.message.toString())
                    if (ui.preguntar("¿Quieres cancelar la consulta?")) break;
                }
            }
        }
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
        var importe = 0.0
        do {
            try {
                importe = ui.pedirDouble("Introduce un importe positivo", "El importe no puede ser negativo", "Debes introducir un número decimal positivo", {importe -> importe >= 0.0})
            } catch (e: Exception){
                ui.mostrarError(e.message.toString())
            }
        } while (importe < 0)
        return importe
    }





    /** Crea un nuevo seguro de hogar solicitando los datos al usuario */
    fun contratarSeguroHogar() {
        gestorSeguros.contratarSeguroHogar(pedirDni(), pedirImporte(), ui.pedirValorDouble("Introduce los m² del Hogar"), ui.pedirValorDouble("Introduce el valor contenido del Hogar"), ui.pedirCadena("Introduce la dirección del Hogar"), ui.pedirAnio("Introduce el año de construcción"))
    }

    /** Crea un nuevo seguro de auto solicitando los datos al usuario */
    fun contratarSeguroAuto() {
        TODO("Implementar este método")
    }

    /** Crea un nuevo seguro de vida solicitando los datos al usuario */
    fun contratarSeguroVida() {
        TODO("Implementar este método")
    }

    /** Elimina un seguro si existe por su número de póliza */
    fun eliminarSeguro() {
        TODO("Implementar este método")
    }

    /** Muestra todos los seguros existentes */
    fun consultarSeguros() {
        TODO("Implementar este método")
    }

    /** Muestra todos los seguros de tipo hogar */
    fun consultarSegurosHogar() {
        TODO("Implementar este método")
    }

    /** Muestra todos los seguros de tipo auto */
    fun consultarSegurosAuto() {
        TODO("Implementar este método")
    }

    /** Muestra todos los seguros de tipo vida */
    fun consultarSegurosVida() {
        TODO("Implementar este método")
    }

    companion object {
        val patronClave = "^[A-Za-z0-9]{4,}$".toRegex()
        val patronDNI = "^\\d{8}[A-Z]$".toRegex()
    }
}
