package utils
import java.io.File
import model.IExportable
import model.Seguro


class Ficheros : IUtilFicheros {
    override fun leerArchivo(ruta: String): List<String> {
        val file = File(ruta)
        return file.bufferedReader().readLines()
    }

    override fun leerSeguros(ruta: String, mapaSeguros: Map<String, (List<String>) -> Seguro>): List<Seguro> {
        val lista = leerArchivo(ruta)
        lista.forEach {
            val datos = it.split(";")
        }
    }

    override fun agregarLinea(ruta: String, linea: String): Boolean {
        val file = File(ruta)
        file.bufferedWriter().newLine()
        file.writeText(linea)
        return true
    }

    override fun <T : IExportable> escribirArchivo(ruta: String, elementos: List<T>): Boolean {
        elementos.forEach {
            if (!agregarLinea(ruta, it.serializar())) return false
        }
        return true
    }

    override fun existeFichero(ruta: String): Boolean {
        return File(ruta).isFile
    }

    override fun existeDirectorio(ruta: String): Boolean {
        val file = File(ruta)
        return file.isDirectory
    }
}