package model


enum class Perfil {
      ADMIN,
    GESTION,
    CONSULTA;

    companion object {
        fun getPerfil(valor: String = ""): Perfil {
            try {
                val perfil = Perfil.valueOf(valor.uppercase().replace(" ",""))
                return perfil
            } catch (e: IllegalArgumentException) {
                return CONSULTA
            }
        }
    }
}