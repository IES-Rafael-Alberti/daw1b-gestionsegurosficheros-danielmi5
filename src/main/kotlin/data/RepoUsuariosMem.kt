package data

import model.Perfil
import model.Usuario

open class RepoUsuariosMem : IRepoUsuarios {
    protected val listaUsuarios = mutableListOf<Usuario>()

    override fun agregar(usuario: Usuario): Boolean {
        return if (buscar(usuario.nombre) != null) listaUsuarios.add(usuario) else false
    }

    override fun buscar(nombreUsuario: String): Usuario? {
        return listaUsuarios.find { it.nombre == nombreUsuario }
    }

    override fun eliminar(usuario: Usuario): Boolean {
        return listaUsuarios.remove(usuario)
    }

    override fun eliminar(nombreUsuario: String): Boolean {
        val usuario = buscar(nombreUsuario)
        return if (usuario != null) listaUsuarios.remove(usuario) else false
    }

    override fun obtenerTodos(): List<Usuario> {
        return listaUsuarios.toList()
    }

    override fun obtener(perfil: Perfil): List<Usuario> {
        return listaUsuarios.filter { it.perfil == perfil }
    }

    override fun cambiarClave(usuario: Usuario, nuevaClave: String): Boolean {
        usuario.cambiarClave(nuevaClave)
        return true
    }
}