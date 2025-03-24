package model

import model.Auto.COCHE

enum class Riesgo(val interesAplicado: Double) {
    BAJO(2.0),
    MEDIO(5.0),
    ALTO(10.0);

    companion object {
        fun getRiesgo(valor: String): Riesgo {
            try {
                val riesgo = Riesgo.valueOf(valor.uppercase().replace(" ",""))
                return riesgo
            } catch (e: IllegalArgumentException) {
                return MEDIO
            }
        }
    }
}