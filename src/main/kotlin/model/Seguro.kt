package model

abstract class Seguro(private val numPoliza: Int, private val dniTitular: String, protected val importe: Double) : IExportable {

    abstract fun calcularImporteAnioSiguiente(interes: Double): Double
    abstract fun tipoSeguro(): String

    fun comprobarNumPoliza(numPoliza: Int): Boolean {
        return this.numPoliza == numPoliza
    }

    override fun serializar(): String {
        return "$numPoliza;$dniTitular;$importe"
    }

    override fun toString(): String {
        return "Seguro(numPoliza=$numPoliza, dniTitular=$dniTitular, importe=${"%.2f".format(importe)})"
    }

    override fun hashCode(): Int {
        return numPoliza
    }

    override fun equals(other: Any?): Boolean {
        if (other is Seguro) {
            return if (this.numPoliza == other.numPoliza) true else false
        } else return false

    }
}