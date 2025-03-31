package app

import model.Perfil
import service.IServSeguros
import service.IServUsuarios
import ui.IEntradaSalida

class GestorMenu(
    private val nombreUsuario: String,
    private val perfil: Perfil,
    private val ui: IEntradaSalida,
    private val servicioUsuarios: IServUsuarios,
    private val servicioSeguros: IServSeguros
){
    fun nuevoUsuario(){

    }
    fun eliminarUsuario() {

    }
    fun cambiarClaveUsuario(){

    }
    fun  consultarUsuarios(){

    }
    fun contratarSeguro(){

    }
    fun eliminarSeguro(){

    }

    fun mostrarMenu(tipoMenu: String){
        ui.mostrar(mapaMenus[tipoMenu]!!)
    }

    companion object{
        private const val MENU_CONSULTA = ""
        private const val MENU_ADMIN = ""
        private const val MENU_GESTION = ""
        val mapaMenus: Map<String, String> = mapOf("admin" to MENU_ADMIN, "gestion" to MENU_GESTION, "consulta" to MENU_CONSULTA)
    }
}