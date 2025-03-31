package data

import model.Seguro
import model.SeguroAuto
import model.SeguroHogar
import model.SeguroVida
import utils.IUtilFicheros

class RepoSegurosFich(private val rutaArchivo: String, private val fich: IUtilFicheros) : RepoSegurosMem(), ICargarSegurosIniciales {


    override fun agregar(seguro: Seguro): Boolean {
        if (buscar(seguro.numPoliza) == null) {
            if (fich.agregarLinea(rutaArchivo, seguro.serializar(";"))){
                return super.agregar(seguro)
            }
        }
        return false
    }

    override fun eliminar(seguro: Seguro): Boolean {
        if (fich.escribirArchivo(rutaArchivo, listaSeguros.filter { it != seguro })) {
            return super.eliminar(seguro)
        }
        return false
    }

    override fun eliminar(numPoliza: Int): Boolean {
        if (fich.escribirArchivo(rutaArchivo, listaSeguros.filter { it.numPoliza != numPoliza })) {
            if (buscar(numPoliza) != null) return super.eliminar(buscar(numPoliza)!!)
        }
        return false
    }

    override fun cargarSeguros(mapa: Map<String, (List<String>) -> Seguro>): Boolean {
        val segurosCargados = fich.leerArchivo(rutaArchivo)
        segurosCargados.forEach {
            val split = it.split(";")
            val funcionCrearSeguro = mapa[split.last()]
            if (funcionCrearSeguro != null) {
                listaSeguros.add(funcionCrearSeguro(split))
            }
        }
        actualizarContadores(listaSeguros)
        return listaSeguros.isNotEmpty()
    }



    private fun actualizarContadores(seguros: List<Seguro>) {
        // Actualizar los contadores de polizas del companion object según el tipo de seguro
        val maxHogar = seguros.filter { it.tipoSeguro() == "SeguroHogar" }.maxOfOrNull { it.numPoliza }
        val maxAuto = seguros.filter { it.tipoSeguro() == "SeguroAuto" }.maxOfOrNull { it.numPoliza }
        val maxVida = seguros.filter { it.tipoSeguro() == "SeguroVida" }.maxOfOrNull { it.numPoliza }

        if (maxHogar != null) SeguroHogar.numPolizasHogar = maxHogar
        if (maxAuto != null) SeguroAuto.numPolizasAuto = maxAuto
        if (maxVida != null) SeguroVida.numPolizasVida = maxVida
    }
}