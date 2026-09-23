package com.example.civfix.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
//import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// ==========================================
// MARK: - Live Supabase & Local Docker Connection
// ==========================================

object SupabaseClient {
    // 10.0.2.2 points to local docker
    private const val LOCAL_SUPABASE_URL = "http://10.0.2.2:54321"
    private const val LOCAL_SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // Default local anon key or your project key

    val client = createSupabaseClient(
        supabaseUrl = LOCAL_SUPABASE_URL,
        supabaseKey = LOCAL_SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
    }
}