package model

enum class Auto {
    COCHE, MOTO, CAMION;

    companion object {
        fun getAuto(valor: String): Auto {
            try {
                val auto = Auto.valueOf(valor.uppercase().replace(" ",""))
                return auto
            } catch (e: IllegalArgumentException) {
                return COCHE
            }

        }
    }
}