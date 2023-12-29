package com.example.eshop.ui.ViewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eshop.repositories.AuthenticationRepository
import com.example.eshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    private val _userInfoLiveData = MutableLiveData<Resource<String>>()
    val userInfoLiveData: LiveData<Resource<String>> get() = _userInfoLiveData

    fun setUserInformationValue() {
        _userInfoLiveData.value = Resource.Idle()
    }

    fun uploadUserInformation(userName: String, imageUri: Uri?, userLocation: String) {
        _userInfoLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _userInfoLiveData.postValue(authenticationRepository.uploadUserInformation(userName, imageUri, userLocation))
        }

    }
}