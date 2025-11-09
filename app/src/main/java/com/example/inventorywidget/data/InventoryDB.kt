package com.example.inventorywidget.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inventorywidget.model.Inventory
import com.example.inventorywidget.utils.Constants.NAME_BD

/**
 * Base de datos principal de la aplicación usando Room.
 * Implementa patrón Singleton para asegurar una sola instancia.
 */
@Database(
    entities = [Inventory::class],
    version = 1,
    exportSchema = false
)
abstract class InventoryDB : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: InventoryDB? = null

        /**
         * Obtiene la instancia única de la base de datos.
         */
        fun getDatabase(context: Context): InventoryDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDB::class.java,
                    NAME_BD
                )
                    .fallbackToDestructiveMigration() // recrea la BD si cambia versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
