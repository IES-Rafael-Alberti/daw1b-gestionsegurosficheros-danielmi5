package app

import model.Perfil
import model.Usuario
import service.IServUsuarios
import ui.IEntradaSalida
import utils.IUtilFicheros

/**
 * Clase responsable del control de acceso de usuarios: alta inicial, inicio de sesión
 * y recuperación del perfil. Su objetivo es asegurar que al menos exista un usuario
 * en el sistema antes de acceder a la aplicación.
 *
 * Esta clase encapsula toda la lógica relacionada con la autenticación de usuarios,
 * separando así la responsabilidad del acceso del resto de la lógica de negocio.
 *
 * Utiliza inyección de dependencias (DIP) para recibir los servicios necesarios:
 * - La ruta del archivo de usuarios
 * - El gestor de usuarios para registrar o validar credenciales
 * - La interfaz de entrada/salida para interactuar con el usuario
 * - La utilidad de ficheros para comprobar la existencia y contenido del fichero
 *
 * @property rutaArchivo Ruta del archivo donde se encuentran los usuarios registrados.
 * @property gestorUsuarios Servicio encargado de la gestión de usuarios (login, alta...).
 * @property ui Interfaz para mostrar mensajes y recoger entradas del usuario.
 * @property ficheros Utilidad para operar con ficheros (leer, comprobar existencia...).
 */
class ControlAcceso(
    private val rutaArchivo: String,
    private val ui: IEntradaSalida,
    private val gestorUsuarios: IServUsuarios,
    private val ficheros: IUtilFicheros
) {

    /**
     * Inicia el proceso de autenticación del sistema.
     *
     * Primero verifica si hay usuarios registrados. Si el archivo está vacío o no existe,
     * ofrece al usuario la posibilidad de crear un usuario ADMIN inicial.
     *
     * A continuación, solicita credenciales de acceso en un bucle hasta que sean válidas
     * o el usuario decida cancelar el proceso.
     *
     * @return Un par (nombreUsuario, perfil) si el acceso fue exitoso, o `null` si el usuario cancela el acceso.
     */
    fun autenticar(): Pair<String, Perfil>? {
        var par: Pair<String, Perfil>? = null
        if (verificarFicheroUsuarios()) {
            par = iniciarSesion()
        } else {
            par = crearAdminInicial()
        }

        return par
    }

    /**
     * Verifica si el archivo de usuarios existe y contiene al menos un usuario registrado.
     *
     * Si el fichero no existe o está vacío, se informa al usuario y se le pregunta si desea
     * registrar un nuevo usuario con perfil ADMIN.
     *
     * Este método se asegura de que siempre haya al menos un usuario en el sistema.
     *
     * @return `true` si el proceso puede continuar (hay al menos un usuario),
     *         `false` si el usuario cancela la creación inicial o ocurre un error.
     */
    private fun verificarFicheroUsuarios(): Boolean {
        return if (ficheros.existeFichero(rutaArchivo) && ficheros.leerArchivo(rutaArchivo).isEmpty()) {
            ui.preguntar("¿Quieres crear un usuario ADMIN inicial?")
        } else true
    }

    /**
     * Solicita al usuario sus credenciales (usuario y contraseña) en un bucle hasta
     * que sean válidas o el usuario decida cancelar.
     *
     * Si la autenticación es exitosa, se retorna el nombre del usuario y su perfil.
     *
     * @return Un par (nombreUsuario, perfil) si las credenciales son correctas,
     *         o `null` si el usuario decide no continuar.
     */
    private fun iniciarSesion() : Pair<String, Perfil>?{
        var sesion: Pair<String, Perfil>? = null
        var todoCorrecto = false
        while (!todoCorrecto){
            try {
                var nombreUsuario = ui.pedirInfo("Introduce el nombre del usuario", "Nombre introducido no existente", { nombre -> gestorUsuarios.buscarUsuario(nombre) != null })
                val perfil = verificarSesion(nombreUsuario)
                if (perfil == null){
                    return null
                } else {
                    sesion = Pair(nombreUsuario, perfil)
                    todoCorrecto = true
                }
            } catch (e: Exception) {
                ui.mostrarError(e.message.toString())
                if (ui.preguntar("¿Quieres cancelar el proceso?")) return null
            }
        }
        return sesion
    }


    private fun crearAdminInicial() : Pair<String, Perfil>?{
        val patronClave = "^[A-Za-z0-9]{4,}$".toRegex()
        var admin: Pair<String, Perfil>? = null
        var todoCorrecto = false
        while (!todoCorrecto){
            try {
                var nombreUsuario = ui.pedirInfo("Introduce un nombre para el usuario", "El nombre no puede estar vacío", {nombre: String -> nombre.isNotBlank()})

                val clave = ui.pedirInfo("Introduce una clave", "La contraseña debe tener mínimo 4 caracteres alfanuméricos", {clave -> patronClave.matches(clave)})

                if (gestorUsuarios.agregarUsuario(nombreUsuario, clave, Perfil.ADMIN)) {
                    todoCorrecto = true
                    admin = Pair(nombreUsuario, Perfil.ADMIN)
                }
                else {
                    throw IllegalArgumentException("El usuario no se ha podido crear")
                }
            } catch (e: Exception) {
                ui.mostrarError(e.message.toString())
                if (ui.preguntar("¿Quieres cancelar el proceso?")) return null;
            }

        }

        return admin
    }

    private fun verificarSesion(nombreUsuario: String) :Perfil? {
        var perfil: Perfil? = null
        do {
            try {
                perfil = gestorUsuarios.iniciarSesion(nombreUsuario, ui.pedirInfoOculta("Introduce la clave del usuario"))
                if (perfil == null) {
                    throw IllegalArgumentException("Clave incorrecta")
                }
            } catch (e: Exception) {
                ui.mostrarError(e.message.toString())
                if (ui.preguntar("¿Quieres cancelar el proceso?")) return null;
            }

        }while (perfil == null)

        return perfil
    }
}

