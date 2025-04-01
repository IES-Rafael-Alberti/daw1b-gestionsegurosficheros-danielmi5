package utils
import java.io.File
import model.IExportable
import model.Seguro
import ui.IEntradaSalida
import java.io.IOException


class Ficheros(private val ui: IEntradaSalida) : IUtilFicheros {
    override fun leerArchivo(ruta: String): List<String> {
        try {
            val file = File(ruta)
            if (file.exists()) {
                return file.readLines()
            } else {
                throw IllegalArgumentException("El archivo no existe")
            }
        } catch (e: IllegalArgumentException) {
            ui.mostrarError("No existe el archivo: ${e.message}")
        } catch (e: IOException) {
            ui.mostrarError("No se pudo leer el archivo: ${e.message}")
        } catch (e: Exception){
            ui.mostrarError(e.message.toString())
        }
        return listOf<String>()
    }

    override fun agregarLinea(ruta: String, linea: String): Boolean {
        return try {
            val file = File(ruta)
            file.appendText(linea + "\n")
            true
        } catch (e: Exception) {
            ui.mostrarError(e.message.toString())
            false
        }
    }

    override fun <T : IExportable> escribirArchivo(ruta: String, elementos: List<T>): Boolean {
        try {
            val file = File(ruta)
            elementos.forEach {
                file.writeText(it.serializar(";"))
            }
        } catch (e: Exception) {
            ui.mostrarError(e.message.toString())
            return false
        }

        return true
    }

    override fun existeFichero(ruta: String): Boolean {
        return File(ruta).isFile
    }

    override fun existeDirectorio(ruta: String): Boolean {
        return File(ruta).isDirectory
    }
}