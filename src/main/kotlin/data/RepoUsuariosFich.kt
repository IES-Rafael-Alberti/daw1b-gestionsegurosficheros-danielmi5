package data

import model.Usuario
import utils.IUtilFicheros

class RepoUsuariosFich(private val rutaArchivo: String, private val fich: IUtilFicheros) : RepoUsuariosMem(), ICargarUsuariosIniciales {
    val usuarios: MutableList<Usuario> = mutableListOf()

    override fun cargarUsuarios(): Boolean {
        val usuariosCargados = fich.leerArchivo(rutaArchivo)
        usuariosCargados.forEach {
            
        }
    }

    override fun eliminar(usuario: Usuario): Boolean {
        if (fich.escribirArchivo(rutaArchivo, usuarios.filter { it != usuario })) {
            return super.eliminar(usuario)
        }
        return false
    }
}