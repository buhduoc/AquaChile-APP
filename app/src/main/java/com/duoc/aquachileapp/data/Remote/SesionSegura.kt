package com.duoc.aquachileapp.data.Remote

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.duoc.aquachileapp.model.Usuario
import com.google.gson.Gson
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SesionSegura {
    private const val PREFS = "sesion_segura"
    private const val CLAVE_SESION = "sesion_cifrada"
    private const val ALIAS = "aquachile_sesion_aes"
    private val gson = Gson()
    private var preferencias: SharedPreferences? = null

    private data class Sesion(val token: String, val usuario: Usuario)

    @Volatile
    private var sesion: Sesion? = null

    val token: String? get() = sesion?.token
    val usuario: Usuario? get() = sesion?.usuario

    @Synchronized
    fun iniciar(context: Context) {
        if (preferencias != null) return
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        preferencias = prefs
        try {
            val datos = prefs.getString(CLAVE_SESION, null) ?: return
            val recuperada = gson.fromJson(descifrar(datos), Sesion::class.java)
            require(recuperada.token.isNotBlank())
            requireNotNull(recuperada.usuario)
            sesion = recuperada
        } catch (e: Exception) {
            sesion = null
            prefs.edit().remove(CLAVE_SESION).apply()
        }
    }

    @Synchronized
    fun guardar(token: String, usuario: Usuario) {
        require(token.isNotBlank()) { "El token no puede estar vacío" }
        val prefs = checkNotNull(preferencias) { "Debes iniciar SesionSegura primero" }
        val nueva = Sesion(token, usuario)
        val datos = cifrar(gson.toJson(nueva))
        check(prefs.edit().putString(CLAVE_SESION, datos).commit()) {
            "No se pudo guardar la sesión"
        }
        sesion = nueva
    }

    @Synchronized
    fun cerrar() {
        sesion = null
        preferencias?.let { prefs ->
            check(prefs.edit().remove(CLAVE_SESION).commit()) {
                "No se pudo eliminar la sesión almacenada"
            }
        }
    }

    private fun obtenerClave(): SecretKey {
        val almacen = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (almacen.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val generador = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        generador.init(
            KeyGenParameterSpec.Builder(
                ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generador.generateKey()
    }

    private fun cifrar(texto: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, obtenerClave())
        val cifrado = cipher.doFinal(texto.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv, Base64.NO_WRAP) + ":" +
                Base64.encodeToString(cifrado, Base64.NO_WRAP)
    }

    private fun descifrar(datos: String): String {
        val partes = datos.split(":", limit = 2)
        require(partes.size == 2)
        val iv = Base64.decode(partes[0], Base64.NO_WRAP)
        val cifrado = Base64.decode(partes[1], Base64.NO_WRAP)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, obtenerClave(), GCMParameterSpec(128, iv))
        return String(cipher.doFinal(cifrado), Charsets.UTF_8)
    }
}
