package data

import model.Usuario

interface IRepoUsuarios {
    fun agregar(usuario: Usuario): Boolean
    fun buscar(nombre: String): Usuario?
    fun eliminar(usuario: Usuario): Boolean
    fun eliminar(nombre: String): Boolean
    fun obtenerTodos(): List<Usuario>
    fun obtener(tipoUsuario: String): List<Usuario>
}