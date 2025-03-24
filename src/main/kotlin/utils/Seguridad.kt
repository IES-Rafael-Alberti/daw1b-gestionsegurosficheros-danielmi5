package utils

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
    override fun encriptarClave(clave: String, nivelSeguridad: Int) : String { // = 12
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