package data

import model.Usuario
import utils.IUtilFicheros

class RepoUsuariosFich(private val fich: IUtilFicheros) : RepoUsuariosMem(), ICargarUsuariosIniciales {

    override fun cargarUsuarios(): Boolean {

    }

    override fun eliminar(usuario: Usuario): Boolean {
        if (fich.escribirArchivo(rutaArchivo, usuarios.filter { it != usuario })) {
            return super.eliminar(usuario)
        }
        return false
    }
}