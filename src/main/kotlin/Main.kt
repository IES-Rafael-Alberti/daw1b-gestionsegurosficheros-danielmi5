import model.SeguroHogar

fun main() {
    val seguroHogar = SeguroHogar(numPolizas = 12345, dniTitular = "12345678A", importe = 500.0, metrosCuadrados = 120, valorContenido = 30000.0, direccion = "Calle Falsa 123, Madrid", anioConstruccion = 2005)

    println(seguroHogar)
    println(seguroHogar::class.simpleName.toString())
}