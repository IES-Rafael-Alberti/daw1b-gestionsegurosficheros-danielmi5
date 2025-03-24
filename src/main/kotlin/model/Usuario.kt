package model

class Usuario(val nombre: String, clave: String, val perfil: Perfil) : IExportable {
    var clave = clave
        private set

    fun verificarClave(claveEncriptada: String): Boolean {
        return clave == claveEncriptada
    }

    fun cambiarClave(nuevaClaveEncriptada: String) {
        clave = nuevaClaveEncriptada
    }

    override fun serializar(separador: String): String = "$nombre$separador$clave$separador$perfil"

    companion object {
        val nombreUsados = mutableListOf<String>()

        fun crearUsuario(datos: List<String>): Usuario {
            val datosM = datos.toMutableList()
            var usuarioCorrecto = false
            var usuario: Usuario? = null

            while (!usuarioCorrecto) {
                try {
                    if (datos[0] in nombreUsados) throw IllegalArgumentException("Nombre '${datos[0]}' ya utilizado") else usuario = Usuario(datos[0], datos[1], Perfil.getPerfil(datos[2]))
                    usuarioCorrecto = true
                } catch (e: IllegalArgumentException) {
                    println("**ERROR* -> ${e.message}")
                    print("Introduce un usuario no utilizado >>")
                    datosM[0] = readln().replace(" ", "")
                }
            }
            nombreUsados.add(datos[0])
            return usuario!!
        }
    }
}