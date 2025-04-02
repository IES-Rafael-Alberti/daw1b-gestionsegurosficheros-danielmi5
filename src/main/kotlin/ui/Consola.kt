package ui

import org.jline.reader.EndOfFileException
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.TerminalBuilder
import java.time.LocalDate

class Consola : IEntradaSalida {
    override fun mostrar(msj: String, salto: Boolean, pausa: Boolean) {
        if (salto) println(msj) else print(msj)
        if (pausa) pausar("Introduce ENTER para continuar...")
    }

    override fun mostrarError(msj: String, pausa: Boolean) {
        val mensajeError = if (msj.startsWith("ERROR - ")) msj else "ERROR - $msj"
        mostrar(mensajeError, true, pausa)
    }

    override fun pedirInfo(msj: String): String {
        if (msj.isNotEmpty()) mostrar("$msj >>", false)
        return readln().trim()
    }

    override fun pedirInfo(msj: String, error: String, debeCumplir: (String) -> Boolean): String {
        val input = pedirInfo(msj)
        require(debeCumplir(input)){error}
        return input
    }

    override fun pedirDouble(prompt: String, error: String, errorConv: String, debeCumplir: (Double) -> Boolean): Double {
        val input = pedirInfo(prompt).replace(',', '.')
        val numero = input.toDoubleOrNull()
        require(numero != null){errorConv}
        require(debeCumplir(numero)){error}
        return numero

    }

    override fun pedirEntero(prompt: String, error: String, errorConv: String, debeCumplir: (Int) -> Boolean): Int {
        val input = pedirInfo(prompt)
        val numero = input.toIntOrNull()
        require(numero is Int){errorConv}
        require(debeCumplir(numero)){error}
        return numero
    }

    override fun pedirInfoOculta(prompt: String): String {
        return try {
            val terminal = TerminalBuilder.builder()
                .dumb(true)
                .build()

            val reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build()

            reader.readLine(prompt, '*')
        } catch (e: UserInterruptException) {
            mostrarError("Entrada cancelada por el usuario (Ctrl + C).", pausa = false)
            ""
        } catch (e: EndOfFileException) {
            mostrarError("Se alcanzó el final del archivo (EOF ó Ctrl+D).", pausa = false)
            ""
        } catch (e: Exception) {
            mostrarError("Problema al leer la contraseña: ${e.message}", pausa = false)
            ""
        }
    }

    override fun pausar(msj: String) {
        pedirInfo(msj)
    }

    override fun limpiarPantalla(numSaltos: Int) {
        if (System.console() != null) {
            mostrar("\u001b[H\u001b[2J", false)
            System.out.flush()
        } else {
            repeat(numSaltos) { mostrar("") }
        }
    }

    override fun preguntar(mensaje: String): Boolean {
        var resp: Boolean?
        do {
            resp = when (pedirInfo("$mensaje (s/n)").lowercase()) {
                "s" -> true
                "n" -> false
                else -> {
                    mostrarError("La respuesta debe ser 's' o 'n'")
                    null
                }
            }
        } while (resp == null)

        return resp
    }

    fun preguntar2(msj: String): Boolean{
        var resp: String
        do {
            resp = try {
                pedirInfo(msj, "Respuesta incorrecta", { cadena -> cadena in listOf("s", "n") })

                //pedirInfo(msj, "Respuesta incorrecta", ::validarP)
            } catch (e: IllegalArgumentException){
                mostrarError(e.message.toString())
                ""
            }
        } while (resp.isEmpty())
        return resp == "s"
    }

    fun validarP(it: String): Boolean{
        return it in listOf("s","n")
    }

    override fun pedirValorDouble(msj: String): Double{
        var valor = 0.0
        do {
            try {
                valor = pedirDouble(msj, "El valor no puede ser negativo", "Debes introducir un número decimal positivo", {it >= 0.0})
            } catch (e: Exception){
                mostrarError(e.message.toString())
            }
        } while (valor < 0)
        return valor
    }

    override fun pedirValorInt(msj: String): Int{
        var valor = 0
        do {
            try {
                valor = pedirEntero(msj, "El valor no puede ser negativo", "Debes introducir un número entere¡o positivo", {it >= 0})
            } catch (e: Exception){
                mostrarError(e.message.toString())
            }
        } while (valor < 0)
        return valor
    }


    override fun pedirCadena(msj: String): String{
        var valor = ""
        do {
            try {
                valor = pedirInfo(msj, "La cadena no puede estar vacía", {cadena: String -> cadena.isNotBlank()})
            } catch (e: Exception){
                mostrarError(e.message.toString())
            }
        } while (valor.isEmpty())
        return valor
    }

    override fun pedirAnio(msj: String): Int {
        val anioActual = LocalDate.now().year
        var anioI = 0
        do {
            try {
                anioI = pedirEntero(msj, "El año no puede ser menor a 0 o mayor a $anioActual", "Año no introducido con un número entero", { anio: Int -> anio in 0..anioActual})
            } catch (e: Exception){
                mostrarError(e.message.toString())
            }
        } while (anioI in 0..anioActual)
        return anioI
    }
}