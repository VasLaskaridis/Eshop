package com.example.eshop.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eshop.db.Product
import com.example.eshop.models.MainShopItem
import com.example.eshop.repositories.AuthenticationRepository
import com.example.eshop.repositories.ShopRepository
import com.example.eshop.utils.Resource
import com.example.eshop.models.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    private val _shopListLiveData =MutableLiveData<Resource<List<MainShopItem>>>()
    val shopListLiveData: LiveData<Resource<List<MainShopItem>>> = _shopListLiveData

    private val _searchedProductsLiveData = MutableLiveData<Resource<List<Product>>>()
    val searchedProductsLiveData: LiveData<Resource<List<Product>>> = _searchedProductsLiveData

    private val _cartProductsLiveData = MutableLiveData<Resource<Any>>()
    val cartProductsLiveData: LiveData<Resource<Any>> = _cartProductsLiveData

    private val _userInformation = MutableLiveData<Resource<UserInfo>>()
    val userInformationLiveData : LiveData<Resource<UserInfo>> = _userInformation

    private var firstLoad = true

    fun setCartProductValue(){
        _cartProductsLiveData.value = Resource.Idle()
    }

    fun getShopList() {
        if (!firstLoad) return
        firstLoad = false
        _shopListLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _shopListLiveData.postValue(shopRepository.getMainShopList())
        }
    }

    fun getProductsHasContainName(searchName: String) {
        _searchedProductsLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _searchedProductsLiveData.postValue(shopRepository.getProductsContainName(searchName))
            delay(500)
            _searchedProductsLiveData.postValue(Resource.Idle())
        }
    }

    fun addProductToCart(productModel: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            _cartProductsLiveData.postValue(
                shopRepository.addProductsToCart(listOf(productModel), false)
            )
        }
    }

    fun getUserInformation(){
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.getUserInformation(_userInformation)
        }
    }
}