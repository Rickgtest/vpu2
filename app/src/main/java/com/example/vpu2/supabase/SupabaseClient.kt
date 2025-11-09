package com.example.vpu2.supabase

import com.example.vpu2.appUi.UArch
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from

object SupabaseClient {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://cllhbgoottqyzayrcosb.supabase.co",
        //not hiding the password lmao, No point because it's on github.
        supabaseKey = "sb_publishable_MzP4215LuDT6lQ8h5tC-hA_c9in20qo"
    ) {
        install(Postgrest)
    }

    suspend fun getUArchs(): List<UArch> {
        return client.from("uarch").select().decodeList<UArch>()
    }
}