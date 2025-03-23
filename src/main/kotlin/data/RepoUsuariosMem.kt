package data

import model.Perfil
import model.Usuario

class RepoUsuariosMem : IRepoUsuarios {
    private val listaUsuarios = mutableListOf<Usuario>()

    override fun agregar(usuario: Usuario): Boolean {
        return listaUsuarios.add(usuario)
    }

    override fun buscar(nombre: String): Usuario? {
        listaUsuarios.forEach {
            if (it.nombre == nombre) return it
        }
        return null
    }

    override fun eliminar(usuario: Usuario): Boolean {
        return listaUsuarios.remove(usuario)
    }

    override fun eliminar(nombre: String): Boolean {
        listaUsuarios.forEach {
            if (it.nombre == nombre) {
                return listaUsuarios.remove(it)
            }
        }
        return false
    }

    override fun obtenerTodos(): List<Usuario> {
        return listaUsuarios.toList()
    }

    override fun obtener(tipoUsuario: String): List<Usuario> {
        return listaUsuarios.filter { it.perfil == Perfil.getPerfil(tipoUsuario) }
    }
}