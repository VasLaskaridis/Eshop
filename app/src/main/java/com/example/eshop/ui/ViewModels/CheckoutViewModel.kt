package com.example.eshop.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eshop.db.Product
import com.example.eshop.models.Order
import com.example.eshop.models.UserInfo
import com.example.eshop.repositories.AuthenticationRepository
import com.example.eshop.repositories.ShopRepository
import com.example.eshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _orderProductsLiveData = MutableLiveData<Resource<Order>>()
    val orderProductsLiveData: LiveData<Resource<Order>> = _orderProductsLiveData

    private val _userInformation = MutableLiveData<Resource<UserInfo>>()
    val userInformationLiveData : LiveData<Resource<UserInfo>> = _userInformation

    init{
        getUserInformation()
    }

    private fun getUserInformation(){
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.getUserInformation(_userInformation)
        }
    }

    fun pushUserOrder(cartProductsList: Array<Product>, userLocation: String, totalCost: Float) {
        _orderProductsLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _orderProductsLiveData.postValue(
                shopRepository.uploadProductsToOrders(cartProductsList, userLocation, totalCost)
            )
        }
    }
}