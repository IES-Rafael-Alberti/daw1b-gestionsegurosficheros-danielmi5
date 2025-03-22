package model

enum class Perfil {
      ADMIN,
    GESTION,
    CONSULTA;

    companion object {
        fun getPerfil(valor: String = ""): Perfil {
            return when (valor.uppercase().trim()) {
                "ADMIN" -> ADMIN
                "GESTION" -> GESTION
                else -> CONSULTA
            }
        }
    }
}