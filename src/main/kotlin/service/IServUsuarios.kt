package service

import model.Perfil
import model.Usuario

interface IServUsuarios {
    fun iniciarSesion(nombre: String, clave: String): Perfil?
    fun agregarUsuario(nombre: String, clave: String, perfil: Perfil): Boolean
    fun eliminarUsuario(nombre: String): Boolean
    fun cambiarClave(usuario: Usuario, nuevaClave: String): Boolean
    fun buscarUsuario(nombre: String): Usuario?
    fun consultarTodos(): List<Usuario>
    fun consultarPorPerfil(perfil: Perfil): List<Usuario>
}