package com.example.eshop.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eshop.db.Product
import com.example.eshop.repositories.ShopRepository
import com.example.eshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject
constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private var _favoriteProductsLiveData: LiveData<List<Product>>?=null
    val favoriteProductsLiveData get() = _favoriteProductsLiveData

    private val _cartProductsLiveData = MutableLiveData<Resource<Any>>()
    val cartProductsLiveData: LiveData<Resource<Any>> = _cartProductsLiveData


    fun getFavoriteProducts(): LiveData<List<Product>>? {
        _favoriteProductsLiveData = shopRepository.getFavoriteProductsLiveData()
        return favoriteProductsLiveData
    }

    fun addProductsToCart(favList: List<Product>) {
        _cartProductsLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _cartProductsLiveData.postValue(
                shopRepository.addProductsToCart(favList, true)
            )
        }
    }
}