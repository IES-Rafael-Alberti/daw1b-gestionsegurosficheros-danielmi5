package app

import data.ICargarSegurosIniciales
import data.ICargarUsuariosIniciales
import model.Seguro
import model.SeguroAuto
import model.SeguroHogar
import model.SeguroVida
import ui.IEntradaSalida

class CargadorInicial (private val ui: IEntradaSalida) {

    companion object{
        val mapaSeguros: Map<String, (List<String>) -> Seguro> = mapOf("vida" to SeguroVida::crearSeguro,
            "auto" to SeguroAuto::crearSeguro,
            "hogar" to SeguroHogar::crearSeguro)
    }

    fun cargarInfo(repoUsuarios: ICargarUsuariosIniciales, repoSeguros: ICargarSegurosIniciales) {
        cargarUsuarios(repoUsuarios)
        cargarSeguros(repoSeguros)
    }

    private fun cargarUsuarios(repoUsuarios: ICargarUsuariosIniciales) {
        try {
            repoUsuarios.cargarUsuarios()
        } catch (e: Exception) {
            ui.mostrarError(e.message.toString())
        }
    }


    private fun cargarSeguros(repoSeguros: ICargarSegurosIniciales) {
        try {
            repoSeguros.cargarSeguros(mapaSeguros)
        } catch (e: Exception) {
            ui.mostrarError(e.message.toString())
        }
    }
}