package data

import model.Seguro

open class RepoSegurosMem : IRepoSeguros {
    protected val listaSeguros = mutableListOf<Seguro>()

    override fun agregar(seguro: Seguro): Boolean {
        return listaSeguros.add(seguro)
    }

    override fun buscar(numPoliza: Int): Seguro? {
        return listaSeguros.find { it.numPoliza == numPoliza }
    }

    override fun eliminar(seguro: Seguro): Boolean {
        return listaSeguros.remove(seguro)
    }

    override fun eliminar(numPoliza: Int): Boolean {
        val seguro = buscar(numPoliza)
        return if (seguro != null) listaSeguros.remove(seguro) else false
    }

    override fun obtenerTodos(): List<Seguro> {
        return listaSeguros
    }

    override fun obtener(tipoSeguro: String): List<Seguro> {
        return listaSeguros.filter { it.tipoSeguro() == tipoSeguro }
    }
}