package model

class SeguroAuto : Seguro {
    private val descripcion: String
    private val combustible: Double
    private val tipoAuto: Auto
    private val cobertura: Cobertura
    private val asistenciaCarretera: Boolean
    private val numPartes: Int

    constructor(dniTitular: String, importe: Double, descripcion: String, combustible: Double, tipoAuto: Auto, cobertura: Cobertura, asistenciaCarretera: Boolean, numPartes: Int) : super(++numPolizasAuto, dniTitular, importe) {
        this.descripcion = descripcion
        this.combustible = combustible
        this.tipoAuto = tipoAuto
        this.cobertura = cobertura
        this.asistenciaCarretera = asistenciaCarretera
        this.numPartes = numPartes
    }

    private constructor(numPolizas: Int, dniTitular: String, importe: Double, descripcion: String, combustible: Double, tipoAuto: Auto, cobertura: Cobertura, asistenciaCarretera: Boolean, numPartes: Int) : super(numPolizas, dniTitular, importe) {
        this.descripcion = descripcion
        this.combustible = combustible
        this.tipoAuto = tipoAuto
        this.cobertura = cobertura
        this.asistenciaCarretera = asistenciaCarretera
        this.numPartes = numPartes
    }

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val interesResidual = numPartes * 2
        return importe * (((interes + interesResidual) / 100) + 1)
    }

    override fun tipoSeguro(): String {
        return this::class.simpleName ?: "Desconocido"
    }

    override fun serializar(): String {
        return "${super.serializar()};$descripcion;$combustible;$tipoAuto;$cobertura;$asistenciaCarretera;$numPartes"
    }

    override fun toString(): String {
        val nombreClase = tipoSeguro()
        val cadenaBase = super.toString().substring(0, super.toString().length - 1).replace("Seguro", nombreClase)
        return "$cadenaBase, descripción=$descripcion, combustible=$combustible, tipoAuto=$tipoAuto, cobertura=$cobertura, asistenciaCarretera=$asistenciaCarretera, numPartes=$numPartes)"
    }

    companion object {
        private var numPolizasAuto: Int = 400000

        fun crearSeguro(datos: List<String>): SeguroAuto {
            return when (datos.size) {
                8 -> SeguroAuto(datos[0], datos[1].toDouble(), datos[2], datos[3].toDouble(), Auto.getAuto(datos[4]), Cobertura.getCobertura(datos[5]), datos[6].toBoolean(), datos[7].toInt())
                9 -> SeguroAuto(datos[0].toInt(), datos[1], datos[2].toDouble(), datos[3], datos[4].toDouble(), Auto.getAuto(datos[5]), Cobertura.getCobertura(datos[6]), datos[7].toBoolean(), datos[8].toInt())
                else -> crearSeguro(datos) //TODO controlar las excepciones en consola
            }
        }
    }
}