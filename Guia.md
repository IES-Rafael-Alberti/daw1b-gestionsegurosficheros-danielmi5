## **Guía de Implementación Paso a Paso**

1. **Crea los paquetes** (`model`, `data`, `service`, `ui`, `utils`).
2. **Implementa los interfaces, clases y enumeraciones en `model`**.
3. **Desarrolla los repositorios en `data`**, diferenciando memoria y ficheros.
4. **Crea los servicios en `service`**, aplicando inyección de dependencias.
5. **Diseña la UI en `ui`** con una implementación en consola.
6. **Gestiona los ficheros en `utils`** y usa la interfaz definida para mantener el código desacoplado.
7. **Implementa el `Main.kt`** para iniciar el programa, gestionar el menú y las dependencias.

***NOTA (20/03/2025 20:15)*** - Por ahora os dejo explicados y guiados casi al 100% los apartados 1, 2 y 6, a menos que se me ocurra algo más o vea algún problema que debemos solucionar.
Cuando tenga otro rato, terminaré mi versión y os subiré el resto de apartados, actualizando esta información.

---

Aquí tenéis un desglose del proyecto con **indicaciones detalladas** sobre qué debe hacer cada paquete y cómo implementar cada clase.

### **1. Estructura del Proyecto (paquetes)**  
Debéis crear los siguientes paquetes:

- **📂 `app`** → Contendrá las clases encargadas de gestionar el flujo principal de la aplicación, como el menú y la navegación entre opciones.  
- **📂 `data`** → Maneja la persistencia de datos, con repositorios que almacenan información en memoria o en ficheros.  
- **📂 `model`** → Define la estructura de los datos, incluyendo clases, enumeraciones y estructuras necesarias para representar la información del sistema.  
- **📂 `service`** → Contiene la lógica de negocio, implementando la gestión de seguros y usuarios mediante la interacción con los repositorios.  
- **📂 `ui`** → Se encarga de la interacción con el usuario, implementando la interfaz en consola o cualquier otro medio de entrada/salida.  
- **📂 `utils`** → Agrupa herramientas y utilidades auxiliares, como gestión de ficheros y encriptación.  

**Con esta organización, el código será más modular, mantenible y escalable.**
   
### **2. `model` (Modelo de Datos)**
Este paquete contiene **todas las clases y enumeraciones** que definen los datos que maneja la aplicación.

#### **ENUMERACIONES**

##### **`Perfil`**: Define los roles de usuario.
  
   - Valores: `ADMIN, GESTION, CONSULTA`
  
      * `ADMIN`: Puede gestionar usuarios y seguros.
      * `GESTION`: Puede gestionar seguros, pero no crear/eliminar usuarios.
      * `CONSULTA`: Solo puede ver información.

   - Métodos estáticos: `getPerfil(valor: String): Cobertura` *(Retorna el valor de la enumeración correspondiente a la cadena de caracteres que se pasa por argumento o CONSULTA si no existe. Por ejemplo: getPerfil("gestion") retornaría GESTION)*

##### **`Cobertura`**: Indica el tipo de cobertura de los seguros de automóvil.
    
   - Valores: `TERCEROS, TERCEROS_AMPLIADO, FRANQUICIA_200, FRANQUICIA_300, FRANQUICIA_400, FRANQUICIA_500, TODO_RIESGO`  

   - Propiedades: `desc` *(Terceros, Terceros +, Todo Riesgo con Franquicia de 200€, ... , Todo Riesgo)*

   - Métodos estáticos: `getCobertura(valor: String): Cobertura` *(Retorna el valor de la enumeración correspondiente a la cadena de caracteres que se pasa por argumento o TERCEROS si no existe. Por ejemplo: getCobertura("terceros_ampliado") retornaría TERCEROS_AMPLIADO)*

##### **`Auto`**: Tipo de automóvil asegurado.

   - Valores: `COCHE, MOTO, CAMION`

   - Métodos estáticos: `getAuto(valor: String): Auto` *(Retorna el valor de la enumeración correspondiente a la cadena de caracteres que se pasa por argumento o COCHE si no existe. Por ejemplo: getAuto("moto") retornaría MOTO)*

##### **`Riesgo`**: Define los niveles de riesgo en los seguros de vida.

   - Valores: `BAJO, MEDIO, ALTO`

   - Propiedades: `interesAplicado` *(2.0, 5.0, 10.0)*.

   - Métodos estáticos: `getRiesgo(valor: String): Riesgo` *(Retorna el valor de la enumeración correspondiente a la cadena de caracteres que se pasa por argumento o MEDIO si no existe. Por ejemplo: getRiesgo("bajo") retornaría BAJO)*

#### **INTERFACES**

##### **`IExportable`**

- Contiene un único método `serializar(): String`

#### **CLASES**

##### **`Usuario`**

- Debe implementar un contrato como una clase de tipo `IExportable`.

- **Atributos:** `nombre`, `clave` y `perfil`. El nombre de usuario debe ser único.

- **Propiedades y métodos estácticos:**
  - `crearUsuario(datos: List<String>): Usuario`: Retorna una instancia de `Usuario`. El parámetro que recibe, `datos`, contiene el valor de cada propiedad en orden y deben ser convertidos según el tipo de la propiedad si es necesario. Muy atentos a controlar su llamada para evitar EXCEPCIONES por conversiones erróneas *(aunque si almacenamos bien la info no debería ocurir, pero un buen programador/a lo controla SIEMPRE)*

- **Métodos:**
  - `verificarClave(claveEncriptada: String): Boolean`: Retorna si la clave es la misma que la almacenada en el usuario.
  - `cambiarClave(nuevaClaveEncriptada: String)`: Actualiza la clave del usuario *(este método va a actualizar la clave del usuario directamente, pero en el servicio que gestiona los usuarios debe solicitar la antigua clave, verificarla y pedir la nueva)*.

- **Métodos que sobreescribe:**
  - `serializar(): String`: Retornar una cadena de caracteres con los valores de los atributos de la clase separados por `;`.

##### **`Seguro`**

- Representa cualquier tipo de seguro. Será la clase base de `SeguroHogar`, `SeguroAuto` y `SeguroVida`.

- Debe implementar un contrato como una clase de tipo `IExportable`.

- **Atributos:** `numPoliza` *(única por seguro)*, `dniTitular`, `importe`. Los dos primeros no serán accesibles desde fuera de la clase y el último solo será accesible desde la clase y las clases que la extiendan.

- **Métodos abstractos:**
  - `calcularImporteAnioSiguiente(interes: Double): Double`
  - `tipoSeguro(): String`

- **Métodos:**
  - `comprobarNumPoliza(numPoliza: Int): Boolean`: Retorna `true/false` indicando si el parámetro que hemos pasado como argumento al método es igual o no al atributo `numPoliza` de la instancia.

- **Métodos que sobreescribe:**
  - `serializar(): String`: Retornar una cadena de caracteres con los valores de los atributos de la clase separados por `;` *(por ejemplo: "100001;44027777K;327.40")*
  - `toString(): String`: Retornar la información del seguro con el siguiente formato *"Seguro(numPoliza=100001, dniTitular=44027777K, importe=327.40)"*. El `importe`siempre con dos posiciones decimales.
  - `hashCode(): Int`: Cómo `numPoliza`será único por cada seguro, retornar el valor de hashCode de un seguro en base solo a la dicha propiedad *(sin utilizar ningún número primo, ni más propiedades)*.
  - `equals(other: Any?): Boolean`: Utilizad igual que en el anterior método, solo la propiedad `numPoliza` para su comparación *(por supuesto, hacedlo bien, antes debéis realizar la comparación por referencia y verificar también si se trata de un `Seguro`)*

##### **CLASES QUE HEREDAN DE `Seguro`**

##### **`SeguroHogar`**

- **Atributos específicos:** `metrosCuadrados`, `valorContenido`, `direccion`, `anioConstruccion`. No serán accesibles desde fuera de la clase.

- **Constructores:** Esta clase no implementa un constructor primaro. En su lugar, tiene dos constructores secundarios, los cuales llaman al constructor de la **clase padre `Seguro`** con `super(...)`.
  - Primer constructor secundario: Lo usaremos en la Contratacíon de un **NUEVO** seguro *(genera un número de póliza automáticamente, gracias a una propiedad privada numPolizasAuto que comienza en el número 100000)*
  - Segundo constructor secundario: Lo usaremos para crear una póliza ya existente *(es decir, cuando recuperamos los seguros desde la persistencia de datos)*. Este segundo constructor no se podrá llamar desde fuera de la clase.

- **Propiedades y métodos estácticos:**
  - `numPolizasAuto: Int`: Nos ayuda a generar `numPoliza` de los nuevos seguros. No será accesible desde fuera de la clase.
  - `crearSeguro(datos: List<String>): SeguroHogar`: Retorna una instancia de `SeguroHogar`. El parámetro que recibe, `datos`, contiene el valor de cada propiedad en orden y deben ser convertidos según el tipo de la propiedad si es necesario. Muy atentos a controlar su llamada para evitar EXCEPCIONES por conversiones erróneas *(aunque si almacenamos bien la info no debería ocurir, pero un buen programador/a lo controla SIEMPRE)*

- **Métodos que sobreescribe:**
  - `calcularImporteAnioSiguiente()`: Retornar el importe del año siguiente basándose en el interés que se pasa por parámetro, sumándole un interés residual de 0.02% por cada 5 años de antiguedad del hogar *(Ej: 4.77 años de antiguedad no incrementa, pero 23,07 sumará al interés el valor de 4 x 0.02 = 0.08)*.
  - `tipoSeguro(): String`: Retornar el nombre de la clase usando `this::class.simpleName` y el operador elvis para indicar al compilador que si `simpleName` es `null` *(cosa que nunca debe pasar, ya que la clase tiene un nombre)*, entonces deberá retornar el valor "Desconocido".
  - `serializar(): String`: Modificar el comportamiento de este método heredado, para retornar una cadena de caracteres con los valores de los atributos de la clase separados por `;`.
  - `toString(): String`: Retornar la información del seguro de hogar con el siguiente formato *"Seguro Hogar(numPoliza=100001, dniTitular=44027777K, importe=327.40, ...)"*. ¿Cómo lo podéis hacer si no tenéis accesible los atributos de la clase base `numPoliza` y `dniTitular`?

##### **`SeguroAuto`**

- **Atributos específicos:** `descripcion`, `combustible`, `tipoAuto`, `cobertura`, `asistenciaCarretera`, `numPartes`. No serán accesibles desde fuera de la clase.

- **Constructores:** Esta clase no implementa un constructor primaro. En su lugar, tiene dos constructores secundarios, los cuales llaman al constructor de la **clase padre `Seguro`** con `super(...)`.
  - Primer constructor secundario: Lo usaremos en la Contratacíon de un **NUEVO** seguro *(genera un número de póliza automáticamente, gracias a una propiedad privada numPolizasAuto que comienza en el número 400000)*
  - Segundo constructor secundario: Lo usaremos para crear una póliza ya existente. Este segundo constructor no se podrá llamar desde fuera de la clase.

- **Propiedades y métodos estácticos:**
  - `numPolizasAuto: Int`: Nos ayuda a generar `numPoliza` de los nuevos seguros. No será accesible desde fuera de la clase.
  - `crearSeguro(datos: List<String>): SeguroAuto`: Retorna una instancia de `SeguroAuto`. El parámetro que recibe, `datos`, contiene el valor de cada propiedad en orden y deben ser convertidos según el tipo de la propiedad si es necesario.

- **Métodos que sobreescribe:**
  - `calcularImporteAnioSiguiente()`: Retornar el importe del año siguiente basándose en el interés que se pasa por parámetro, sumándole un interés residual del 2% por cada parte declarado.
  - `tipoSeguro(): String`: Retornar el nombre de la clase usando `this::class.simpleName` y el operador elvis para indicar al compilador que si `simpleName` es `null`, entonces deberá retornar el valor "Desconocido".
  - `serializar(): String`: Modificar el comportamiento de este método heredado, para retornar una cadena de caracteres con los valores de los atributos de la clase separados por `;`.
  - `toString(): String`: Retornar la información del seguro de auto con un formato similar al del seguro de hogar.

##### **`SeguroVida`**

- **Atributos específicos:** `fechaNac`, `nivelRiesgo`, `indemnizacion`. Usad el tipo de datos `LocalDate` para `fechaNac`. No serán accesibles desde fuera de la clase.

- **Constructores:** Esta clase no implementa un constructor primaro. En su lugar, tiene dos constructores secundarios, los cuales llaman al constructor de la **clase padre `Seguro`** con `super(...)`.
  - Primer constructor secundario: Lo usaremos en la Contratacíon de un **NUEVO** seguro *(genera un número de póliza automáticamente, gracias a una propiedad privada numPolizasAuto que comienza en el número 800000)*
  - Segundo constructor secundario: Lo usaremos para crear una póliza ya existente. Este segundo constructor no se podrá llamar desde fuera de la clase.

- **Propiedades y métodos estácticos:**
  - `numPolizasAuto: Int`: Nos ayuda a generar `numPoliza` de los nuevos seguros. No será accesible desde fuera de la clase.
  - `crearSeguro(datos: List<String>): SeguroVida`: Retorna una instancia de `SeguroVida`. El parámetro que recibe, `datos`, contiene el valor de cada propiedad en orden y deben ser convertidos según el tipo de la propiedad si es necesario.

- **Métodos que sobreescribe:**
  - `calcularImporteAnioSiguiente()`: Retornar el importe del año siguiente basándose en el interés que se pasa por parámetro, sumándole un interés residual del 0.05% por cada año cumplido y el interés de su nivel de riesgo *(Ver clase enumerada `Riesgo`)*.
  - `tipoSeguro(): String`: Retornar el nombre de la clase usando `this::class.simpleName` y el operador elvis para indicar al compilador que si `simpleName` es `null`, entonces deberá retornar el valor "Desconocido".
  - `serializar(): String`: Modificar el comportamiento de este método heredado, para retornar una cadena de caracteres con los valores de los atributos de la clase separados por `;`.
  - `toString(): String`: Retornar la información del seguro de auto con un formato similar al del seguro de hogar.

---

### **3. `data` (Repositorios y Persistencia)**

#### **Interfaces:**

```
interface IRepoSeguros {
    fun agregar(seguro: Seguro): Boolean
    fun buscar(numPoliza: Int): Seguro?
    fun eliminar(seguro: Seguro): Boolean
    fun eliminar(numPoliza: Int): Boolean
    fun obtenerTodos(): List<Seguro>
    fun obtener(tipoSeguro: String): List<Seguro>
}
```

```kotlin
interface IRepoUsuarios {
}
```

#### **Clases:**

##### **RepoSegurosMem:**

##### **RepoSegurosFich:**

##### **RepoUsuariosMem:**

##### **RepoUsuariosFich:**


---

### **4. `service` (Lógica de Negocio)**
Aquí se implementan las **operaciones principales** que la interfaz de usuario ejecutará.

#### **Interfaces (`IServUsuarios`, `IServSeguros`)**

```kotlin
interface IServSeguros {
}
```

```kotlin
interface IServUsuarios {
}
```

#### **Servicios (`GestorUsuarios`, `GestorSeguros`)**
- Implementan las interfaces y usan los repositorios.
- `GestorUsuarios` maneja la autenticación, creación de nuevos usuarios y cambios de contraseña.
- `GestorSeguros` se encarga de contratar, listar y eliminar seguros.

##### **GestorUsuarios:**

##### **GestorSeguros:**

---

### **5. `ui` (Interfaz de Usuario)**
Este paquete maneja **cómo interactúa el usuario** con el sistema.

#### **Interfaz `IUserInterface`**
- Define métodos como `mostrar(mensaje: String)`, etc.

#### **`Consola`** (Implementación de `IUserInterface`)
- Imprime mensajes en la terminal y recibe entradas del usuario.

---

### **6. `utils` (Utilidades)**
Contiene herramientas para operaciones repetitivas.

#### **Interfaz `IUtilFicheros`**
- Define métodos de lectura y escritura en archivos.

```kotlin
interface IUtilFicheros {
    fun leerArchivo(ruta: String): List<String>
    fun leerSeguros(ruta: String, mapaSeguros: Map<String, (List<String>) -> Seguro>): List<Seguro>
    fun agregarLinea(ruta: String, linea: String): Boolean
    fun <T: IExportable> escribirArchivo(ruta: String, elementos: List<T>): Boolean
    fun existeFichero(ruta: String): Boolean
    fun existeDirectorio(ruta: String): Boolean
}
```

#### **Clase `Ficheros`**
- Implementa `IUtilFicheros` y maneja el acceso a los `.txt`.

#### **Interfaz `IUtilSeguridad`**
- Define métodos para encriptar y verificar claves.

```kotlin
interface IUtilSeguridad {
    fun encriptarClave(clave: String, nivelSeguridad: Int = 12): String
    fun verificarClave(claveIngresada: String, hashAlmacenado: String): Boolean
}
```

#### **Clase `Seguridad`

- Incluir la implementación de la librería externa BCrypt en el fichero `build.gradle`:

```kotlin
dependencies {
    testImplementation(kotlin("test"))
    implementation("at.favre.lib:bcrypt:0.9.0")
}
```

- Contenido de la clase `Seguridad`:

```kotlin
import at.favre.lib.crypto.bcrypt.BCrypt

class Seguridad : IUtilSeguridad {

    /**
     * Genera un hash seguro de la clave utilizando el algoritmo BCrypt.
     *
     * BCrypt es un algoritmo de hashing adaptativo que permite configurar un nivel de seguridad (coste computacional),
     * lo que lo hace ideal para almacenar contraseñas de forma segura.
     *
     * @param clave La contraseña en texto plano que se va a encriptar.
     * @param nivelSeguridad El factor de coste utilizado en el algoritmo BCrypt. Valores más altos aumentan la seguridad
     * pero también el tiempo de procesamiento. El valor predeterminado es `12`, que se considera seguro para la mayoría
     * de los casos.
     * @return El hash de la clave en formato String, que puede ser almacenado de forma segura.
     */
    override fun encriptarClave(clave: String, nivelSeguridad: Int = 12): String {
        return BCrypt.withDefaults().hashToString(nivelSeguridad, clave.toCharArray())
    }

    /**
     * Verifica si una contraseña ingresada coincide con un hash almacenado previamente usando BCrypt.
     *
     * Esta función permite autenticar a un usuario comprobando si la clave ingresada,
     * tras ser procesada con BCrypt, coincide con el hash almacenado en la base de datos.
     *
     * @param claveIngresada La contraseña en texto plano que se desea comprobar.
     * @param hashAlmacenado El hash BCrypt previamente generado contra el que se verificará la clave ingresada.
     * @return `true` si la clave ingresada coincide con el hash almacenado, `false` en caso contrario.
     */
    override fun verificarClave(claveIngresada: String, hashAlmacenado: String): Boolean {
        return BCrypt.verifyer().verify(claveIngresada.toCharArray(), hashAlmacenado).verified
    }

}
```

---

### **7. `Main.kt` (Punto de Entrada)**
- Inicializa repositorios y servicios.
- Pide credenciales o permite crear un `ADMIN` si no hay usuarios.
- Carga el **menú principal** para gestionar usuarios y seguros.



