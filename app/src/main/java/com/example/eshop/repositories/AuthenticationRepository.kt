package com.example.eshop.repositories

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.eshop.R
import com.example.eshop.models.UserInfo
import com.example.eshop.utils.Constants.FIRST_LOGGED_IN_APP
import com.example.eshop.utils.Constants.USERS_COLLECTION
import com.example.eshop.utils.MyTransformer
import com.example.eshop.utils.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@ViewModelScoped
class AuthenticationRepository
@Inject
constructor(
    private val sharedPref: SharedPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseFirestore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
) {

    private val userUid by lazy { firebaseAuth.uid!! }
    private val firebaseUserCollection by lazy { firebaseFirestore.collection(USERS_COLLECTION) }
    private val transformer:MyTransformer = MyTransformer()

    init {
        firebaseAuth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    }

    fun checkIfUserLoggedIn(): Boolean {
        val user = firebaseAuth.currentUser
        return user != null
    }

    suspend fun uploadUserInformation(
        userName: String,
        imageUri: Uri?,
        userLocation: String
    ): Resource<String> {
        return try {
            var accountStatusMessage = "Account created successfully."
            if (imageUri != null) {
                val uploadedImagePath = uploadUserImage(imageUri)
                val userInfoModel = UserInfo(userUid, userName, uploadedImagePath, userLocation)
                firebaseUserCollection.document(userUid).set(transformer.userInfoToMap(userInfoModel)).await()
            } else {
                val userInfoModel =
                    UserInfo(userUid, userName, "", userLocation)
                firebaseUserCollection.document(userUid).update(transformer.userInfoToMap(userInfoModel)).await()
                accountStatusMessage = "Account Updated Successfully"
            }
            Resource.Success(accountStatusMessage)
        } catch (e: Exception) {
            Resource.Error(context.getString(R.string.errorCreateAccount))
        }
    }

    private suspend fun uploadUserImage(imageUri: Uri): String {
        var uploadingResult =""
        val reference = firebaseStorage.reference.child("${USERS_COLLECTION}/images")
        reference.putFile(imageUri).continueWithTask { task ->
            reference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uploadingResult=task.result.toString()
            }
        }.await()
        return uploadingResult
    }

    suspend fun signInWithCredential(credential: AuthCredential): Resource<Unit?> {
        return try {
            firebaseAuth.signInWithCredential(credential).await()
            Resource.Success(null)
        } catch (e: Exception) {
            Resource.Error(context.getString(R.string.errorMessage))
        }
    }

    // check if user has data into firebase firestore or not.
    fun getUserInformation(userInfoLiveData: MutableLiveData<Resource<UserInfo>>) {
        firebaseFirestore.collection(USERS_COLLECTION).document(userUid)
            .addSnapshotListener { value, _ ->
                if (value == null || value.data == null) {
                    userInfoLiveData.postValue(Resource.Error(context.getString(R.string.errorMessage)))
                } else {
                    val userInfoModel = transformer.convertMapToUserInfo(value.data!!)
                    userInfoLiveData.postValue(Resource.Success(userInfoModel))
                }
            }
    }

    suspend fun changeUserLocation(location: String): Boolean {
        return try {
            firebaseFirestore.collection(USERS_COLLECTION).document(userUid).update(
                mapOf("userLocationName" to location)
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun checkIfFirstAppOpened(): Boolean {
        val isFirstLogApp = sharedPref.getBoolean(FIRST_LOGGED_IN_APP, true)
        if (isFirstLogApp){
            val editor = sharedPref.edit()
            editor.putBoolean(FIRST_LOGGED_IN_APP, false).apply()
        }
        return isFirstLogApp
    }

}