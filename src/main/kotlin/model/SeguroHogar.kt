package model

import java.time.LocalDate

class SeguroHogar : Seguro {
    private val metrosCuadrados: Double
    private val valorContenido: Double
    private val direccion: String
    private val anioConstruccion: Int

    constructor(dniTitular: String, importe: Double, metrosCuadrados: Double, valorContenido: Double, direccion: String, anioConstruccion: Int) : super(++numPolizasAuto, dniTitular, importe) {
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
        val interesResidual = (aniosAntiguedad / 5).toInt() * 0.02
        return importe * (((interes + interesResidual) / 100) + 1)
    }

    override fun tipoSeguro(): String {
        return this::class.simpleName ?: "Desconocido"
    }

    override fun serializar(): String {
        return "${super.serializar()};$metrosCuadrados;$valorContenido;$direccion;$anioConstruccion"
    }

    override fun toString(): String {
        val nombreClase = tipoSeguro()
        val cadena = super.toString().substring(0, super.toString().length - 1).replace("Seguro", nombreClase)
        return "$cadena, metrosCuadrados=$metrosCuadrados, valorContenido=$valorContenido, direccion=$direccion, anioConstruccion=$anioConstruccion)"
    }

    companion object {
        private var numPolizasAuto: Int = 100000

        fun crearSeguro(datos: List<String>): SeguroHogar {
            return when (datos.size){
                6 -> SeguroHogar(datos[0], datos[1].toDouble(), datos[2].toDouble(), datos[3].toDouble(), datos[4], datos[5].toInt())
                7 -> SeguroHogar(datos[0].toInt(), datos[1], datos[2].toDouble(), datos[3].toDouble(), datos[4].toDouble(), datos[5], datos[6].toInt(),)
                else -> crearSeguro(datos) //TODO controlar las excepciones en consola
            }
        }
    }
}