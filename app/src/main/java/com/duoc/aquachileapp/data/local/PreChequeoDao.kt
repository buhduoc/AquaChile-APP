
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

    @Query(
        "SELECT * FROM pre_chequeos WHERE uuid = :uuid LIMIT 1"
    )
    suspend fun obtenerPorUuid(
        uuid: String
    ): PreChequeoEntity?

    @Query(
        """
        SELECT * FROM pre_chequeos
        WHERE idServidor = :idServidor
        LIMIT 1
        """
    )
    suspend fun obtenerPorIdServidor(
        idServidor: Long
    ): PreChequeoEntity?

    @Query(
        """
        SELECT * FROM pre_chequeos
        WHERE estadoSincronizacion IN ('PENDIENTE', 'ERROR', 'SINCRONIZANDO')
        ORDER BY id ASC
        """
    )
    suspend fun obtenerPendientes(): List<PreChequeoEntity>

    @Query(
        """
        SELECT COUNT(*) FROM pre_chequeos
        WHERE estadoSincronizacion IN ('PENDIENTE', 'ERROR', 'SINCRONIZANDO')
        """
    )
    fun contarPendientes(): Flow<Int>

    @Query(
        """
        UPDATE pre_chequeos
        SET estadoSincronizacion = :estado,
            ultimoErrorSincronizacion = :error
        WHERE id = :id
        """
    )
    suspend fun actualizarEstado(
        id: Int,
        estado: String,
        error: String? = null
    )

    @Query(
        """
        UPDATE pre_chequeos
        SET estadoSincronizacion = 'SINCRONIZADO',
            idServidor = :idServidor,
            ultimoErrorSincronizacion = NULL
        WHERE id = :id
        """
    )
    suspend fun marcarSincronizado(
        id: Int,
        idServidor: Long
    )

    // Se conservan los métodos originales
    // para mantener compatibilidad con el ViewModel.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(
        preChequeo: PreChequeoEntity
    ): Long

    @Delete
    suspend fun eliminar(
        preChequeo: PreChequeoEntity
    )

    @Query("DELETE FROM pre_chequeos WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
