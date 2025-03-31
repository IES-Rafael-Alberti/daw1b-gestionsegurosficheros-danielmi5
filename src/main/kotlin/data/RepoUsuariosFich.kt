package data

import model.Usuario
import utils.IUtilFicheros

class RepoUsuariosFich(private val rutaArchivo: String, private val fich: IUtilFicheros) : RepoUsuariosMem(), ICargarUsuariosIniciales {

    init {
        if (fich.existeFichero(rutaArchivo)){
            cargarUsuarios()
        }
    }

    override fun cargarUsuarios(): Boolean {
        val usuariosCargados = fich.leerArchivo(rutaArchivo)
        if (usuariosCargados.isEmpty()) return false

        usuariosCargados.forEach {
            listaUsuarios.add(Usuario.crearUsuario(it.split(";")))
        }
        return true
    }

    override fun eliminar(usuario: Usuario): Boolean {
        if (fich.escribirArchivo(rutaArchivo, listaUsuarios.filter { it != usuario })) {
            return super.eliminar(usuario)
        }
        return false
    }

    override fun eliminar(nombreUsuario: String): Boolean {
        if (fich.escribirArchivo(rutaArchivo, listaUsuarios.filter { it.nombre != nombreUsuario })) {
            if (buscar(nombreUsuario) != null) return super.eliminar(buscar(nombreUsuario)!!)
        }
        return false
    }

    override fun agregar(usuario: Usuario): Boolean {
        if (buscar(usuario.nombre) == null) {
            if (fich.agregarLinea(rutaArchivo, usuario.serializar(";"))){
                return super.agregar(usuario)
            }
        }
        return false
    }

    override fun cambiarClave(usuario: Usuario, nuevaClave: String): Boolean {
        usuario.cambiarClave(nuevaClave)
        return fich.escribirArchivo(rutaArchivo, listaUsuarios)
    }






}