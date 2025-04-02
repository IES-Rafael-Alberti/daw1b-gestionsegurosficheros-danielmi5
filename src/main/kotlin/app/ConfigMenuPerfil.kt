package app

/**
 * Clase (**data class**) de configuración que encapsula los menús y las acciones disponibles para un perfil
 * concreto de usuario.
 *
 * Se utiliza dentro del objeto `ConfiguracionesApp` para definir de forma estructurada:
 * - Las distintas pantallas o niveles de menú disponibles para un perfil.
 * - Las acciones que deben ejecutarse al seleccionar cada opción de esos menús.
 *
 * @property menus Lista de menús, donde cada menú es una lista de cadenas (opciones mostradas al usuario).
 * @property acciones Lista de mapas de acciones. Cada mapa asocia un número de opción (1 en adelante) con una
 *                    función lambda que recibe una instancia de `GestorMenu` y devuelve un `Boolean`.
 *
 * El índice de `menus` y `acciones` debe coincidir para cada nivel. Por ejemplo:
 * - `menus[0]` define el menú principal.
 * - `acciones[0]` define las funciones que se ejecutan cuando el usuario selecciona una opción de ese menú principal.
 *
 * **IMPORTANTE:** El valor `Boolean` devuelto por cada lambda indica si se debe **salir del menú actual**:
 * - `true` → el menú finaliza (por ejemplo, al pulsar "Volver" o "Salir").
 * - `false` → se mantiene dentro del mismo menú.
 *
 * Esta clase permite separar la lógica de navegación del controlador (`GestorMenu`) de la definición concreta de
 * opciones por perfil, facilitando su configuración, mantenimiento y ampliación.
 */
data class ConfigMenuPerfil(
    val menus: List<List<String>>,
    val acciones: List<Map<Int, (GestorMenu) -> Boolean>>
)