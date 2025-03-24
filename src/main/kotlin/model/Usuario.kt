package model

class Usuario(val nombre: String, clave: String, val perfil: Perfil) : IExportable {
    var clave = clave
        private set

    fun cambiarClave(nuevaClaveEncriptada: String) {
        clave = nuevaClaveEncriptada
    }

    override fun serializar(separador: String): String = "$nombre$separador$clave$separador$perfil"

    companion object {
        val nombreUsados = mutableListOf<String>()

        fun crearUsuario(datos: List<String>): Usuario {
            nombreUsados.add(datos[0])
            return Usuario(datos[0], datos[1], Perfil.getPerfil(datos[2]))
        }
    }
}