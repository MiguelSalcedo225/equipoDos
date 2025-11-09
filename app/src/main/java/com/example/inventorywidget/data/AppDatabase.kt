package com.example.inventorywidget.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.utils.Constants.NAME_BD

/**
 * Base de datos principal de la aplicación usando Room.
 * Implementa patrón Singleton para asegurar una sola instancia.
 */
@Database(
    entities = [Product::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "inventory_database"

        /**
         * Obtiene la instancia única de la base de datos.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // recrea la BD si cambia versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
