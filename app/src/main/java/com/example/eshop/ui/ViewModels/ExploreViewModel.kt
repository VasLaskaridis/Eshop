package com.example.eshop.ui.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eshop.db.Product
import com.example.eshop.models.CategoryItem
import com.example.eshop.models.MainShopItem
import com.example.eshop.repositories.ShopRepository
import com.example.eshop.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel
@Inject
constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {
    private val _categoryLiveData = MutableLiveData<Resource<List<CategoryItem>>>()
    val categoryLiveData: LiveData<Resource<List<CategoryItem>>> = _categoryLiveData

    private val _searchedProductsLiveData = MutableLiveData<Resource<List<Product>>>()
    val searchedProductsLiveData: LiveData<Resource<List<Product>>> = _searchedProductsLiveData

    private val _categoryProductsLiveData = MutableLiveData<Resource<MainShopItem>>()
    val categoryProductsLiveData: LiveData<Resource<MainShopItem>> = _categoryProductsLiveData


    fun getCategoryList() {
        _categoryLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _categoryLiveData.postValue(
                shopRepository.getCategoryList()
            )
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

    fun getProductsByCategoryId(id: String) {
        _categoryProductsLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            _categoryProductsLiveData.postValue(shopRepository.getSpecificCategoryProducts(id))
            delay(500)
            _categoryProductsLiveData.postValue(Resource.Idle())
        }
    }
}