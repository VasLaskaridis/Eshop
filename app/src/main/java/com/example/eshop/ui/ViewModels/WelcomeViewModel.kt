package com.example.eshop.ui.ViewModels

import androidx.lifecycle.ViewModel
import com.example.eshop.repositories.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel
@Inject
constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    fun checkIfFirstAppOpened(): Boolean = authenticationRepository.checkIfFirstAppOpened()

    fun checkIfUserLoggedIn(): Boolean = authenticationRepository.checkIfUserLoggedIn()

}