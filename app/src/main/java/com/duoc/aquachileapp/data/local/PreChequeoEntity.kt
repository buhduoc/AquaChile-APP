package com.duoc.aquachileapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.duoc.aquachileapp.model.ItemChecklist
import com.duoc.aquachileapp.model.PreChequeo

@Entity(tableName = "pre_chequeos")
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
    val alertasJson: String
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromItemList(value: List<ItemChecklist>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toItemList(value: String): List<ItemChecklist> {
        val type = object : TypeToken<List<ItemChecklist>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
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
        alertas = converters.toStringList(alertasJson)
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
        alertasJson = converters.fromStringList(alertas)
    )
}
