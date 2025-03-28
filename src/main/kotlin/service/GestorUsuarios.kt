package service

import data.IRepoUsuarios
import model.Perfil
import model.Usuario
import utils.IUtilSeguridad

class GestorUsuarios(private val repoUsuarios: IRepoUsuarios, private val utilSeguridad: IUtilSeguridad) : IServUsuarios {

    override fun iniciarSesion(nombre: String, clave: String): Perfil? {
        val usuario = repoUsuarios.buscar(nombre)
        return if (usuario == null) {
            null
        } else if (utilSeguridad.verificarClave(clave, usuario.clave)) usuario.perfil else null
    }

    override fun agregarUsuario(nombre: String, clave: String, perfil: Perfil): Boolean {
        val claveEncriptada = utilSeguridad.encriptarClave(clave)
        val usuario = Usuario(nombre, claveEncriptada, perfil)
        return repoUsuarios.agregar(usuario)
    }

    override fun eliminarUsuario(nombre: String): Boolean {
        return repoUsuarios.eliminar(nombre)
    }

    override fun cambiarClave(usuario: Usuario, nuevaClave: String): Boolean {
        val claveEncriptada = utilSeguridad.encriptarClave(nuevaClave)
        return repoUsuarios.cambiarClave(usuario, claveEncriptada)
    }

    override fun buscarUsuario(nombre: String): Usuario? {
        return repoUsuarios.buscar(nombre)
    }

    override fun consultarTodos(): List<Usuario> {
        return repoUsuarios.obtenerTodos()
    }

    override fun consultarPorPerfil(perfil: Perfil): List<Usuario> {
        return repoUsuarios.obtener(perfil)
    }
}
