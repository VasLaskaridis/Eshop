package com.example.eshop.utils

import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

sealed class MainAuthState {

    object Idle: MainAuthState()
    object Loading: MainAuthState()
    data class Error(val error: FirebaseException): MainAuthState()
}