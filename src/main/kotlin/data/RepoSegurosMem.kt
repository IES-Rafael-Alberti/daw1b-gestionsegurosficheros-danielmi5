package data

import model.Seguro

class RepoSegurosMem : IRepoSeguros {
    private val listaSeguros = mutableListOf<Seguro>()

    override fun agregar(seguro: Seguro): Boolean {
        return listaSeguros.add(seguro)
    }

    override fun buscar(numPoliza: Int): Seguro? {
        listaSeguros.forEach {
            if (it.comprobarNumPoliza(numPoliza)) return it
        }
        return null
    }

    override fun eliminar(seguro: Seguro): Boolean {
        return listaSeguros.remove(seguro)
    }

    override fun eliminar(numPoliza: Int): Boolean {
        listaSeguros.forEach {
            if (it.comprobarNumPoliza(numPoliza)) {
                return listaSeguros.remove(it)
            }
        }
        return false
    }

    override fun obtenerTodos(): List<Seguro> {
        return listaSeguros.toList()
    }

    override fun obtener(tipoSeguro: String): List<Seguro> {
        return listaSeguros.filter { it.tipoSeguro() == tipoSeguro }
    }
}