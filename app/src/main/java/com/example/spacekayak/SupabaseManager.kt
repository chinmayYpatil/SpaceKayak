package com.example.spacekayak.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth

// ⚠️ REPLACE WITH YOUR ACTUAL URL AND ANON KEY ⚠️
private const val SUPABASE_URL = "https://tzkpjkmrwvlprtyupxrl.supabase.co"
private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR6a3Bqa21yd3ZscHJ0eXVweHJsIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjM3MDc2ODMsImV4cCI6MjA3OTI4MzY4M30.ByvqK9rgYG-sSHlRLgwOi73fyc6BFDaitcxefpP27go"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_ANON_KEY
) {
    // Add necessary modules here
    install(Auth)
}