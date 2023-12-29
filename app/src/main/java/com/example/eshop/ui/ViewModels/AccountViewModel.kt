package com.example.eshop.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eshop.models.UserInfo
import com.example.eshop.repositories.AuthenticationRepository
import com.example.eshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository
    ): ViewModel() {

        private val _userInformation = MutableLiveData<Resource<UserInfo>>()
        val userInformationLiveData : LiveData<Resource<UserInfo>> = _userInformation

        init {
            getUserInformation()
        }

        private fun getUserInformation(){
            viewModelScope.launch(Dispatchers.IO) {
                authenticationRepository.getUserInformation(_userInformation)
            }
        }
}