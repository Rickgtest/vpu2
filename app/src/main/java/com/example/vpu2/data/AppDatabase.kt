package com.example.vpu2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vpu2.appUi.UArch

@Database(entities = [UArch::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun uArchDao(): UArchDao
}