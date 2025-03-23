package model

import java.time.LocalDate

class SeguroVida : Seguro {
    private val fechaNac: LocalDate
    private val nivelRiesgo: Riesgo
    private val indemnizacion: Double


    constructor(dniTitular: String, importe: Double, fechaNac: LocalDate, nivelRiesgo: Riesgo, indemnizacion: Double) : super(++numPolizasAuto, dniTitular, importe) {
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
        val interesResidual = edad * 0.05
        return importe * (((interes + interesResidual + nivelRiesgo.interesAplicado) / 100) + 1)
    }

    override fun tipoSeguro(): String {
        return this::class.simpleName ?: "Desconocido"
    }

    override fun serializar(): String {
        return "${super.serializar()};$fechaNac;$nivelRiesgo;$indemnizacion"
    }

    override fun toString(): String {
        val nombreClase = tipoSeguro()
        val cadenaBase = super.toString().substring(0, super.toString().length - 1).replace("Seguro", nombreClase)
        return "$cadenaBase, fechaNac=$fechaNac, nivelRiesgo=$nivelRiesgo, indemnizacion=$indemnizacion)"
    }

    companion object {
        private var numPolizasAuto: Int = 800000

        fun crearSeguro(datos: List<String>): SeguroVida {
            return when (datos.size) {
                5 -> SeguroVida(datos[0], datos[1].toDouble(), LocalDate.parse(datos[2]), Riesgo.getRiesgo(datos[3]), datos[4].toDouble())
                6 -> SeguroVida(datos[0].toInt(), datos[1], datos[2].toDouble(), LocalDate.parse(datos[3]), Riesgo.getRiesgo(datos[4]), datos[5].toDouble())
                else -> crearSeguro(datos) //TODO controlar las excepciones en consola
            }
        }
    }
}