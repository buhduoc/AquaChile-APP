package com.duoc.aquachileapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PreChequeoDao {
    @Query("SELECT * FROM pre_chequeos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<PreChequeoEntity>>

    @Query("SELECT * FROM pre_chequeos ORDER BY id DESC")
    suspend fun obtenerTodosSync(): List<PreChequeoEntity>

    @Query("SELECT * FROM pre_chequeos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): PreChequeoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(preChequeo: PreChequeoEntity): Long

    @Delete
    suspend fun eliminar(preChequeo: PreChequeoEntity)

    @Query("DELETE FROM pre_chequeos WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
