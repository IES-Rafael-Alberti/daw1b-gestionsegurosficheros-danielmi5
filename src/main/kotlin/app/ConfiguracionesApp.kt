package app

import model.*

/**
 * Objeto de configuración global que centraliza elementos reutilizables del sistema.
 *
 * Contiene:
 * - Mapa de funciones para reconstruir seguros desde ficheros (para persistencia).
 * - Estructura completa de menús y acciones disponibles por perfil de usuario.
 *
 * Su uso permite desacoplar la lógica de presentación y creación de objetos del resto de la lógica de negocio.
 */
object ConfiguracionesApp {

    /**
     * Mapa que asocia el nombre del tipo de seguro (guardado en el fichero) con una función
     * que recibe una lista de Strings y construye el objeto correspondiente.
     *
     * Esto permite leer los datos desde un archivo y reconstruir dinámicamente los seguros.
     *
     * Las claves del mapa deben coincidir con el nombre que aparece al final de cada línea
     * serializada en el fichero (por ejemplo: "SeguroAuto").
     */
    val mapaCrearSeguros: Map<String, (List<String>) -> Seguro> = mapOf(
        "SeguroHogar" to SeguroHogar::crearSeguro,
        "SeguroAuto" to SeguroAuto::crearSeguro,
        "SeguroVida" to SeguroVida::crearSeguro
    )

    /**
     * Mapa que define la configuración completa del menú y las acciones disponibles según el perfil del usuario.
     *
     * Cada perfil (admin, gestion, consulta) tiene asociada una lista de:
     * - Menús (niveles jerárquicos con sus opciones).
     * - Acciones que se ejecutarán cuando el usuario seleccione una opción en ese menú.
     *
     * Las acciones se implementan como lambdas que reciben un objeto `GestorMenu` y devuelven un Boolean
     * que indica si debe salir del menú (`true`) o continuar (`false`).
     *
     * Esta configuración permite desacoplar la lógica de los menús del propio controlador `GestorMenu`.
     */
    private val menusAccionesPorPerfil: Map<String, ConfigMenuPerfil> = mapOf(
        "admin" to ConfigMenuPerfil(
            menus = listOf(
                listOf("Usuarios", "Seguros", "Salir"),
                listOf("Nuevo", "Eliminar", "Cambiar contraseña", "Consultar", "Volver"),
                listOf("Contratar", "Eliminar", "Consultar", "Volver"),
                listOf("Hogar", "Auto", "Vida", "Volver"),
                listOf("Todos", "Hogar", "Auto", "Vida", "Volver")
            ),
            acciones = listOf(
                mapOf(
                    1 to { it.iniciarMenu(1); false },
                    2 to { it.iniciarMenu(2); false },
                    3 to { true }
                ),
                mapOf(
                    1 to { it.nuevoUsuario(); false },
                    2 to { it.eliminarUsuario(); false },
                    3 to { it.cambiarClaveUsuario(); false },
                    4 to { it.consultarUsuarios(); false },
                    5 to { true }
                ),
                mapOf(
                    1 to { it.iniciarMenu(3); false },
                    2 to { it.eliminarSeguro(); false },
                    3 to { it.iniciarMenu(4); false },
                    4 to { true }
                ),
                mapOf(
                    1 to { it.contratarSeguroHogar(); false },
                    2 to { it.contratarSeguroAuto(); false },
                    3 to { it.contratarSeguroVida(); false },
                    4 to { true }
                ),
                mapOf(
                    1 to { it.consultarSeguros(); false },
                    2 to { it.consultarSegurosHogar(); false },
                    3 to { it.consultarSegurosAuto(); false },
                    4 to { it.consultarSegurosVida(); false },
                    5 to { true }
                )
            )
        ),
        "gestion" to ConfigMenuPerfil(
            menus = listOf(
                listOf("Seguros", "Salir"),
                listOf("Contratar", "Eliminar", "Consultar", "Volver"),
                listOf("Hogar", "Auto", "Vida", "Volver"),
                listOf("Todos", "Hogar", "Auto", "Vida", "Volver")
            ),
            acciones = listOf(
                mapOf(
                    1 to { it.iniciarMenu(1); false },
                    2 to { true }
                ),
                mapOf(
                    1 to { it.iniciarMenu(2); false },
                    2 to { it.eliminarSeguro(); false },
                    3 to { it.iniciarMenu(3); false },
                    4 to { true }
                ),
                mapOf(
                    1 to { it.contratarSeguroHogar(); false },
                    2 to { it.contratarSeguroAuto(); false },
                    3 to { it.contratarSeguroVida(); false },
                    4 to { true }
                ),
                mapOf(
                    1 to { it.consultarSeguros(); false },
                    2 to { it.consultarSegurosHogar(); false },
                    3 to { it.consultarSegurosAuto(); false },
                    4 to { it.consultarSegurosVida(); false },
                    5 to { true }
                )
            )
        ),
        "consulta" to ConfigMenuPerfil(
            menus = listOf(
                listOf("Seguros", "Salir"),
                listOf("Consultar", "Volver"),
                listOf("Todos", "Hogar", "Auto", "Vida", "Volver")
            ),
            acciones = listOf(
                mapOf(
                    1 to { it.iniciarMenu(1); false },
                    2 to { true }
                ),
                mapOf(
                    1 to { it.iniciarMenu(2); false },
                    2 to { true }
                ),
                mapOf(
                    1 to { it.consultarSeguros(); false },
                    2 to { it.consultarSegurosHogar(); false },
                    3 to { it.consultarSegurosAuto(); false },
                    4 to { it.consultarSegurosVida(); false },
                    5 to { true }
                )
            )
        )
    )

    /**
     * Devuelve el par de lista de opciones del menú y las acciones asociadas a un determinado nivel,
     * en función del perfil de usuario.
     *
     * @param perfil Perfil del usuario (admin, gestion, consulta).
     * @param indice Índice del menú a mostrar (0 = principal).
     * @return Un par con la lista de opciones y un mapa de acciones. Si el perfil o índice no existe, devuelve listas vacías.
     */
    fun obtenerMenuYAcciones(perfil: String, indice: Int): Pair<List<String>, Map<Int, (GestorMenu) -> Boolean>> {
        val config = menusAccionesPorPerfil[perfil] ?: return emptyList<String>() to emptyMap()
        val menu = config.menus.getOrNull(indice) ?: emptyList()
        val acciones = config.acciones.getOrNull(indice) ?: emptyMap()
        return menu to acciones
    }

}