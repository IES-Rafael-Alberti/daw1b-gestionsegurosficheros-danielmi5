package app

import service.IServUsuarios
import ui.IEntradaSalida
import utils.IUtilFicheros

class ControlAcceso(
    private val rutaArchivoUsuarios: String,
    private val ui: IEntradaSalida,
    private val servicioUsuarios: IServUsuarios,
    private val fich: IUtilFicheros
) {


}