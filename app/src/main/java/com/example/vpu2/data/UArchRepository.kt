package com.example.vpu2.data

import com.example.vpu2.appUi.UArch
import com.example.vpu2.supabase.SupabaseClient
import kotlinx.coroutines.flow.Flow

class UArchRepository(
    private val uArchDao: UArchDao,
    private val supabaseClient: SupabaseClient
) {
    fun getUArchs(): Flow<List<UArch>> {
        return uArchDao.getAll()
    }

    suspend fun refreshUArchs() {
        val uArchs = supabaseClient.getUArchs()
        uArchDao.insertAll(uArchs)
    }
}