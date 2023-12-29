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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CartViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _cartProductsLiveData = MutableLiveData<Resource<List<Product>>>()
    val cartProductsLiveData: LiveData<Resource<List<Product>>> = _cartProductsLiveData

    fun deleteProductFromCart(productModel: Product){
        viewModelScope.launch(Dispatchers.IO) {
            shopRepository.deleteProductFromUserCart(productModel.id)
            withContext(Dispatchers.Main){
                getAllCartProducts()
            }
        }
    }

    fun getAllCartProducts() {
        _cartProductsLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _cartProductsLiveData.postValue(
                shopRepository.getAllUserProducts()
            )
        }
    }

}