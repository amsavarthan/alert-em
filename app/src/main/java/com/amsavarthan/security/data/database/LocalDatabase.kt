package com.amsavarthan.security.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amsavarthan.security.data.database.guardian.Guardian
import com.amsavarthan.security.data.database.guardian.GuardianDao
import com.amsavarthan.security.data.database.user.User
import com.amsavarthan.security.data.database.user.UserDao


@Database(entities = [User::class, Guardian::class], version = 1, exportSchema = false)
@TypeConverters(value = [RoomTypeConverters::class])
abstract class LocalDatabase : RoomDatabase() {

    companion object {

        private const val tableName = "local-database"

        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                buildDatabase(context)
            }
        }

        @Synchronized
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                LocalDatabase::class.java,
                tableName
            )
                .fallbackToDestructiveMigration()
                .build()
    }

    abstract fun userDao(): UserDao
    abstract fun guardianDao(): GuardianDao

}