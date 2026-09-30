package com.example.civfix.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// ==========================================
// MARK: - Live Supabase Cloud Connection
// ==========================================

object SupabaseClient {
    private const val SUPABASE_URL = "https://wfozmdmivhhfuwxmjyqo.supabase.co"
    private const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_D2xvHK80xjWazcfXLWti_g_zcqAkpb_"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_PUBLISHABLE_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}