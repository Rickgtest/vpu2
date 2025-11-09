package com.example.vpu2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vpu2.appUi.UArch
import kotlinx.coroutines.flow.Flow

@Dao
interface UArchDao {
    @Query("SELECT * FROM u_arch")
    fun getAll(): Flow<List<UArch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(uArchs: List<UArch>)
}