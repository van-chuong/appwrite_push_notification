package com.example.appwritepushnoti

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.services.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Bắt đầu một coroutine để lấy token từ Firebase và gọi createPushTarget
        CoroutineScope(Dispatchers.Main).launch {
            getTokenAndCreatePushTarget()
        }
    }

    private suspend fun getTokenAndCreatePushTarget() {
        // Lấy token từ Firebase
        try {
            token = FirebaseMessaging.getInstance().token.await()
            Log.d("Token", token)
            // Gọi createPushTarget
            createPushTarget()
        } catch (e: Exception) {
            Log.e("Token", "Error getting Firebase token", e)
        }
    }

    private suspend fun createPushTarget() {
        val client = Client(applicationContext)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("663b932300030cf0a819")
            .setSelfSigned(true)
        val account = Account(client)
        account.createEmailPasswordSession(
            "chuong@gmail.com", "12345678"
        )
        val result = account.createPushTarget(ID.unique(), token, "663b994c00244d2f2325")
        Log.d("Token", result.toString())
    }
}
