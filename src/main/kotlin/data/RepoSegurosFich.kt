package data

import model.Seguro
import model.SeguroAuto
import model.SeguroHogar
import model.SeguroVida
import utils.IUtilFicheros

class RepoSegurosFich(private val rutaArchivo: String, private val fich: IUtilFicheros) : RepoSegurosMem(), ICargarSegurosIniciales {

    init {
        if (fich.existeFichero(rutaArchivo)){
            cargarSeguros(mapa)
        }
    }

    companion object{
        val mapa: Map<String, (List<String>) -> Seguro> = mapOf("vida" to SeguroVida::crearSeguro,
            "auto" to SeguroAuto::crearSeguro,
            "hogar" to SeguroHogar::crearSeguro)
    }

    override fun agregar(seguro: Seguro): Boolean {
        if (buscar(seguro.numPoliza) != null) {
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

    override fun cargarSeguros(mapa: Map<String, (List<String>) -> Seguro>): Boolean {
        val segurosCargados = fich.leerArchivo(rutaArchivo)
        segurosCargados.forEach {
            val split = it.split(";")
            val seguro = mapa[split.last()]?.invoke(split) ?: return false
            listaSeguros.add(seguro)
        }
        actualizarContadores(listaSeguros)
        return true
        
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