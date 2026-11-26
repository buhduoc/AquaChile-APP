
package com.duoc.aquachileapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.duoc.aquachileapp.model.EvidenciaEquipo
import com.duoc.aquachileapp.model.EVIDENCIAS_OBLIGATORIAS_ZARPE
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.PreChequeo
import java.util.UUID

@Entity(
    tableName = "pre_chequeos",
    indices = [
        Index(value = ["uuid"], unique = true),
        Index(value = ["estadoSincronizacion"])
    ]
)
data class PreChequeoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val centro: String,
    val fecha: String,
    val buzo: String,
    val supervisor: String,
    val itemsJson: String,
    val observaciones: String,
    val fotosJson: String,
    val sistolica: Int,
    val diastolica: Int,
    val saturacion: Int,
    val alertasJson: String,
    val edad: Int,
    val pesoKg: Double,
    val alturaCm: Double,
    val examenPreocupacionalVigente: Boolean,
    val testEsfuerzoVigente: Boolean,
    val puntajeEscuelaCalbuco: Int,

    // Campos para sincronizacion offline

    @ColumnInfo(defaultValue = "''")
    val uuid: String = UUID.randomUUID().toString(),

    @ColumnInfo(defaultValue = "'LEGADO_REVISAR'")
    val estadoSincronizacion: String =
        EstadoSincronizacion.PENDIENTE,

    val idServidor: Long? = null,

    val ultimoErrorSincronizacion: String? = null,

    @ColumnInfo(defaultValue = "'[]'")
    val evidenciasEquiposJson: String = "[]"
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromItemList(value: List<ItemChecklist>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toItemList(value: String): List<ItemChecklist> {
        val type =
            object : TypeToken<List<ItemChecklist>>() {}.type

        return gson.fromJson<List<ItemChecklist>>(
            value, type
        ) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type =
            object : TypeToken<List<String>>() {}.type

        return gson.fromJson<List<String>>(
            value, type
        ) ?: emptyList()
    }
}

fun PreChequeoEntity.toDomain(): PreChequeo {
    val converters = Converters()

    return PreChequeo(
        id = id,
        centro = centro,
        fecha = fecha,
        buzo = buzo,
        supervisor = supervisor,
        items = converters.toItemList(itemsJson),
        observaciones = observaciones,
        fotos = converters.toStringList(fotosJson),
        sistolica = sistolica,
        diastolica = diastolica,
        saturacion = saturacion,
        alertas = converters.toStringList(alertasJson),
        edad = edad,
        pesoKg = pesoKg,
        alturaCm = alturaCm,
        examenPreocupacionalVigente =
            examenPreocupacionalVigente,
        testEsfuerzoVigente = testEsfuerzoVigente,
        puntajeEscuelaCalbuco =
            puntajeEscuelaCalbuco,
        evidenciasEquipos = Gson().fromJson<List<EvidenciaEquipo>>(
            evidenciasEquiposJson, object : TypeToken<List<EvidenciaEquipo>>() {}.type
        ).orEmpty().ifEmpty { EVIDENCIAS_OBLIGATORIAS_ZARPE }
    )
}

fun PreChequeo.toEntity(): PreChequeoEntity {
    val converters = Converters()

    return PreChequeoEntity(
        id = id,
        centro = centro,
        fecha = fecha,
        buzo = buzo,
        supervisor = supervisor,
        itemsJson = converters.fromItemList(items),
        observaciones = observaciones,
        fotosJson = converters.fromStringList(fotos),
        sistolica = sistolica,
        diastolica = diastolica,
        saturacion = saturacion,
        alertasJson = converters.fromStringList(alertas),
        edad = edad,
        pesoKg = pesoKg,
        alturaCm = alturaCm,
        examenPreocupacionalVigente =
            examenPreocupacionalVigente,
        testEsfuerzoVigente = testEsfuerzoVigente,
        puntajeEscuelaCalbuco =
            puntajeEscuelaCalbuco,
        evidenciasEquiposJson = Gson().toJson(evidenciasEquipos)
    )
}
