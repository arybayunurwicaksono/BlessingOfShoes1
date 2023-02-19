package com.example.blessingofshoes_1.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.blessingofshoes_1.utils.Converters

@TypeConverters(Converters::class)
@Database(entities =
[
    Users::class,
    Product::class,
    Cart::class,
    Transaction::class,
    Restock::class,
    Return::class,
    Balance::class,
    BalanceReport::class,
    Accounting::class
],
    version = 2)

abstract class AppDb : RoomDatabase() {

    abstract fun dbDao(): DbDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_8_9: Migration = object : Migration(8, 9) {
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
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
                        //.fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE as AppDb
        }

    }
}