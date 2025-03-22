package model

enum class Auto {
    COCHE, MOTO, CAMION;

    companion object {
        fun getAuto(valor: String): Auto {
            return when (valor.uppercase().trim()) {
                "MOTO" -> MOTO
                "CAMION" -> CAMION
                else -> COCHE
            }
        }
    }
}