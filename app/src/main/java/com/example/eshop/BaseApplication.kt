package com.example.eshop

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication: Application() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        // subscribe to firebase cloud messaging topic with user id to just get user notifications.
        val id = firebaseAuth.uid
        if (id != null)
            FirebaseMessaging.getInstance().subscribeToTopic(id.lowercase())
    }
}