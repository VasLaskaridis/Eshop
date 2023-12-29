package com.example.eshop.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eshop.models.Order
import com.example.eshop.repositories.ShopRepository
import com.example.eshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _userOrdersLiveData = MutableLiveData<Resource<List<Order>>>()
    val userOrdersLiveData: LiveData<Resource<List<Order>>> = _userOrdersLiveData

    init {
        getUserOrders()
    }

    private fun getUserOrders(){
        _userOrdersLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _userOrdersLiveData.postValue(shopRepository.getUserOrders())
        }
    }

}