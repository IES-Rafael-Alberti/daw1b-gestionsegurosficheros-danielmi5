package app

import data.ICargarSegurosIniciales
import data.ICargarUsuariosIniciales
import model.Seguro
import model.SeguroAuto
import model.SeguroHogar
import model.SeguroVida
import ui.IEntradaSalida

/**
 * Clase encargada de cargar los datos iniciales de usuarios y seguros desde ficheros,
 * necesarios para el funcionamiento del sistema en modo persistente.
 *
 * @param ui Interfaz de entrada/salida para mostrar mensajes de error.
 * @param repoUsuarios Repositorio que permite cargar usuarios desde un fichero.
 * @param repoSeguros Repositorio que permite cargar seguros desde un fichero.
 */
class CargadorInicial (private val ui: IEntradaSalida, private val repoUsuarios: ICargarUsuariosIniciales, private val repoSeguros: ICargarSegurosIniciales) {

    companion object{
        val mapaSeguros: Map<String, (List<String>) -> Seguro> = mapOf("vida" to SeguroVida::crearSeguro,
            "auto" to SeguroAuto::crearSeguro,
            "hogar" to SeguroHogar::crearSeguro)
    }

    /**
     * Carga los usuarios desde el archivo configurado en el repositorio.
     * Muestra errores si ocurre un problema en la lectura o conversión de datos.
     */
    fun cargarUsuarios() {
        try {
            repoUsuarios.cargarUsuarios()
        } catch (e: Exception) {
            ui.mostrarError(e.message.toString())
        }
    }

    /**
     * Carga los seguros desde el archivo configurado en el repositorio.
     * Utiliza el mapa de funciones de creación definido en la configuración de la aplicación
     * (ConfiguracionesApp.mapaCrearSeguros).
     * Muestra errores si ocurre un problema en la lectura o conversión de datos.
     */
    fun cargarSeguros() {
        try {
            repoSeguros.cargarSeguros(mapaSeguros)
        } catch (e: Exception) {
            ui.mostrarError(e.message.toString())
        }
    }
}