package model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SeguroVida : Seguro {
    private val fechaNac: LocalDate
    private val nivelRiesgo: Riesgo
    private val indemnizacion: Double


    constructor(dniTitular: String, importe: Double, fechaNac: LocalDate, nivelRiesgo: Riesgo, indemnizacion: Double) : super(++numPolizasVida, dniTitular, importe) {
        this.fechaNac = fechaNac
        this.nivelRiesgo = nivelRiesgo
        this.indemnizacion = indemnizacion
    }

    private constructor(numPolizas: Int, dniTitular: String, importe: Double, fechaNac: LocalDate, nivelRiesgo: Riesgo, indemnizacion: Double) : super(numPolizas, dniTitular, importe) {
        this.fechaNac = fechaNac
        this.nivelRiesgo = nivelRiesgo
        this.indemnizacion = indemnizacion
    }

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val edad = LocalDate.now().year - fechaNac.year
        val interesResidual = edad * PORCENTAJE_INCREMENTO_POR_ANIO
        return importe * (((interes + interesResidual + nivelRiesgo.interesAplicado) / 100) + 1)
    }

    override fun serializar(separador: String): String {
        return "${super.serializar(";")}$separador$fechaNac$separador$nivelRiesgo$separador$indemnizacion"
    }

    override fun toString(): String {
        val nombreClase = tipoSeguro()
        val cadenaBase = super.toString().substring(0, super.toString().length - 1).replace("Seguro", nombreClase)
        return "$cadenaBase, fechaNac=$fechaNac, nivelRiesgo=$nivelRiesgo, indemnizacion=$indemnizacion)"
    }

    companion object {
        var numPolizasVida: Int = 800000

        const val PORCENTAJE_INCREMENTO_POR_ANIO = 0.05

        fun crearSeguro(datos: List<String>): SeguroVida {
            return SeguroVida(datos[0].toInt(), datos[1], datos[2].toDouble(), LocalDate.parse(datos[3], DateTimeFormatter.ofPattern("dd/MM/YYYY")), Riesgo.getRiesgo(datos[4]), datos[5].toDouble()) //TODO controlar las excepciones en consola
        }
    }
}