package com.duoc.aquachileapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.centroDataStore by preferencesDataStore(
    name = "preferencias_centro"
)

object PreferenciasCentro {

    private val CENTRO_KEY = stringPreferencesKey("centro")

    // Recuperar el último centro seleccionado
    suspend fun leer(context: Context): String? {
        return context.applicationContext.centroDataStore
            .data.first()[CENTRO_KEY]
    }

    // Guardar el centro seleccionado
    suspend fun guardar(context: Context, centro: String) {
        context.applicationContext.centroDataStore.edit { preferencias ->
            preferencias[CENTRO_KEY] = centro
        }
    }

    // Eliminar el centro guardado
    suspend fun eliminar(context: Context) {
        context.applicationContext.centroDataStore.edit { preferencias ->
            preferencias.remove(CENTRO_KEY)
        }
    }
}