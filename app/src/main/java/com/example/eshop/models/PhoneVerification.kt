package com.example.eshop.models

import android.os.Parcelable
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhoneVerification (
    val verificationId: String,
    val verificationToken: PhoneAuthProvider.ForceResendingToken,
    val phoneNumber: String
): Parcelable