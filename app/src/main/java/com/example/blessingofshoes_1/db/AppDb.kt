package com.example.blessingofshoes_1.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.blessingofshoes_1.Converters

@TypeConverters(Converters::class)
@Database(entities =
[
    Users::class,
    Product::class
],
    version = 2)

abstract class AppDb : RoomDatabase() {

    abstract fun dbDao(): DbDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        @Volatile
        private var INSTANCE: AppDb? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDb {
            if(INSTANCE == null) {
                synchronized(AppDb::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDb::class.java, "app_db")
                        .allowMainThreadQueries()
                        .addMigrations(MIGRATION_1_2)
                        //.fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE as AppDb
        }

    }
}