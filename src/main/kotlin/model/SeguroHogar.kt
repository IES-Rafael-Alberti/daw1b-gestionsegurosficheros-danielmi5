package model

import java.time.LocalDate

class SeguroHogar : Seguro {
    private val metrosCuadrados: Double
    private val valorContenido: Double
    private val direccion: String
    private val anioConstruccion: Int

    constructor(dniTitular: String, importe: Double, metrosCuadrados: Double, valorContenido: Double, direccion: String, anioConstruccion: Int) : super(++numPolizasHogar, dniTitular, importe) {
        this.metrosCuadrados = metrosCuadrados
        this.valorContenido = valorContenido
        this.direccion = direccion
        this.anioConstruccion = anioConstruccion
    }

    constructor(numPolizas: Int, dniTitular: String, importe: Double, metrosCuadrados: Double, valorContenido: Double, direccion: String, anioConstruccion: Int) : super(numPolizas, dniTitular, importe) {
        this.metrosCuadrados = metrosCuadrados
        this.valorContenido = valorContenido
        this.direccion = direccion
        this.anioConstruccion = anioConstruccion
    }


    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val aniosAntiguedad = LocalDate.now().year - anioConstruccion
        val interesResidual = (aniosAntiguedad / CICLO_ANIOS_INCREMENTO).toInt() * PORCENTAJE_INCREMENTO_ANIOS
        return importe * (((interes + interesResidual) / 100) + 1)
    }

    override fun serializar(separador: String): String {
        return "${super.serializar(";")}$separador$metrosCuadrados$separador$valorContenido$separador$direccion$separador$anioConstruccion"
    }

    override fun toString(): String {
        val nombreClase = tipoSeguro()
        val cadena = super.toString().substring(0, super.toString().length - 1).replace("Seguro", nombreClase)
        return "$cadena, metrosCuadrados=$metrosCuadrados, valorContenido=$valorContenido, direccion=$direccion, anioConstruccion=$anioConstruccion)"
    }

    companion object {
        var numPolizasHogar: Int = 100000

        const val PORCENTAJE_INCREMENTO_ANIOS = 0.02
        const val CICLO_ANIOS_INCREMENTO = 5

        fun crearSeguro(datos: List<String>): SeguroHogar {
            return SeguroHogar(datos[0].toInt(), datos[1], datos[2].toDouble(), datos[3].toDouble(), datos[4].toDouble(), datos[5], datos[6].toInt(),)
        }
    }
}