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
class SpecificProductViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _cartProductsLiveData = MutableLiveData<Resource<Any>>()
    val cartProductsLiveData: LiveData<Resource<Any>> = _cartProductsLiveData

    fun setCartProductValue(){
        _cartProductsLiveData.value = Resource.Idle()
    }

    fun favoriteLiveData(id: String) = shopRepository.getProductFromFavoriteLiveData(id)

    fun saveProductInFavorites(productModel: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            shopRepository.saveOrRemoveProductFromFavorite(productModel)
        }
    }

    fun addProductToCart(productModel: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            _cartProductsLiveData.postValue(
                shopRepository.addProductsToCart(listOf(productModel), false)
            )
        }
    }
}