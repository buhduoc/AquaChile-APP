
package com.duoc.aquachileapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [PreChequeoEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun preChequeoDao(): PreChequeoDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
                    ALTER TABLE pre_chequeos
                    ADD COLUMN uuid TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    ALTER TABLE pre_chequeos
                    ADD COLUMN estadoSincronizacion TEXT
                    NOT NULL DEFAULT 'LEGADO_REVISAR'
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    ALTER TABLE pre_chequeos
                    ADD COLUMN idServidor INTEGER
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    ALTER TABLE pre_chequeos
                    ADD COLUMN ultimoErrorSincronizacion TEXT
                    """.trimIndent()
                )

                // Identificadores únicos para registros anteriores.
                db.execSQL(
                    """
                    UPDATE pre_chequeos
                    SET uuid = lower(hex(randomblob(16)))
                    WHERE uuid = ''
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_pre_chequeos_uuid
                    ON pre_chequeos(uuid)
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_pre_chequeos_estadoSincronizacion
                    ON pre_chequeos(estadoSincronizacion)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pre_chequeos ADD COLUMN evidenciasEquiposJson TEXT NOT NULL DEFAULT '[]'")
            }
        }

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aquachile_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
