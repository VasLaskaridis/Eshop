package com.example.eshop.repositories

import android.content.Context
import com.example.eshop.R
import com.example.eshop.db.FavoriteDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import androidx.lifecycle.LiveData
import com.example.eshop.db.Product
import com.example.eshop.models.CategoryItem
import com.example.eshop.models.MainShopItem
import com.example.eshop.models.Order
import com.example.eshop.models.OrderEnums
import com.example.eshop.utils.Constants.CART_COLLECTION
import com.example.eshop.utils.Constants.CATEGORY
import com.example.eshop.utils.Constants.INCOMPLETE_ORDERS
import com.example.eshop.utils.Constants.ITEMS
import com.example.eshop.utils.Constants.PRODUCTS
import com.example.eshop.utils.Constants.SHOP_LIST
import com.example.eshop.utils.Constants.USERS_COLLECTION
import com.example.eshop.utils.MyTransformer
import com.example.eshop.utils.Resource
import kotlinx.coroutines.tasks.await

@ViewModelScoped
class ShopRepository
@Inject
constructor(
    private val fireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val favoriteDao: FavoriteDao,
    @ApplicationContext private val context: Context
) {

    private val errorMessage by lazy { context.getString(R.string.errorMessage) }
    private val userUid by lazy { firebaseAuth.uid!! }
    private val cartCollection by lazy {
        fireStore.collection(USERS_COLLECTION).document(userUid).collection(CART_COLLECTION)
    }
    private val transformer:MyTransformer = MyTransformer()

    // get all products main shop from firebase.
    suspend fun getMainShopList(): Resource<List<MainShopItem>> {
        return try {
            val resultList = fireStore.collection(SHOP_LIST).get().await()
            val shopList = transformer.convertDocumentListToMainShopList(resultList.documents)
            for (shop in shopList) {
                shop.list.addAll(getProductsBySavedShopList(shop))
            }
            Resource.Success(shopList)
        } catch (e: Exception) {
            Resource.Error(msg = errorMessage)
        }
    }

    // get all products by categories from firebase.
    private suspend fun getCategoryProductsList(): List<MainShopItem> {
        return try {
            val result = fireStore.collection(CATEGORY).get().await()
            val categoryList = transformer.convertDocumentsToCategoryList(result.documents)
            getProductsByCategory(categoryList)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // get all categories from firebase.
    suspend fun getCategoryList(): Resource<List<CategoryItem>> {
        return try {
            val result = fireStore.collection(CATEGORY).get().await()
            Resource.Success(transformer.convertDocumentsToCategoryList(result.documents))
        } catch (e: Exception) {
            Resource.Error(errorMessage)
        }
    }

    // get all products that contain to category item id.
    private suspend fun getProductsByCategory(categoryList: List<CategoryItem>): List<MainShopItem> {
        val list = mutableListOf<MainShopItem>()
        categoryList.forEach {
            val products =
                fireStore.collection(PRODUCTS).whereEqualTo("category", it.name)
                    .get()
                    .await()
            list.add(transformer.convertToMainShopItem(it,transformer.convertDocumentToProductList(products.documents)))
        }
        return list
    }

    // get all products that contain to main shop item id.
    private suspend fun getProductsBySavedShopList(shop: MainShopItem): List<Product> {
        val productsList = mutableListOf<Product>()
        val result =
            fireStore.collection(SHOP_LIST).document(shop.id).collection(ITEMS).get().await()
        val productIdList = mutableListOf<String>()
        result.documents.forEach { doc ->
            productIdList.add(doc.id)
        }
        productIdList.forEach { id ->
            val product = fireStore.collection(PRODUCTS).document(id).get().await()
            productsList.add(transformer.convertMapToProduct(product.data!!))
        }
        return productsList
    }

    // change product in database (save or remove) by check if it saved before or not.
    suspend fun saveOrRemoveProductFromFavorite(productModel: Product) {
        val isSavedBefore = getProductFromFavorite(productModel.id)
        return if (isSavedBefore) {
            favoriteDao.removeProductFromFavorites(productModel)
        } else {
            favoriteDao.saveProduct(productModel)
        }
    }

    // check if product is saved into favorite database or not .
    private suspend fun getProductFromFavorite(id: String): Boolean {
        val productModel = favoriteDao.getSpecificFavoriteProduct(id)
        return productModel != null
    }

    // observe to specific product when save or not to change favorite icon .
    fun getProductFromFavoriteLiveData(id: String): LiveData<Product?> =
        favoriteDao.getSpecificFavoriteProductLiveData(id)

    fun getFavoriteProductsLiveData(): LiveData<List<Product>> =
        favoriteDao.getAllFavoriteProducts()


    //get products from specific category by get category value and get products from
    //getProductsByCategory function and get all products where it contain to category id.
    suspend fun getSpecificCategoryProducts(categoryId: String): Resource<MainShopItem> {
        return try {
            val result = fireStore.collection(CATEGORY).document(categoryId).get().await()
            val categoryItem = transformer.convertMapToCategoryItem(result.data!!)
            Resource.Success(getProductsByCategory(listOf(categoryItem))[0])
        } catch (e: Exception) {
            Resource.Error(errorMessage)
        }
    }

    // search for products by names contain search value into search bar from firebase.
    suspend fun getProductsContainName(searchName: String): Resource<List<Product>> {
        return try {
            val result =
                fireStore.collection(PRODUCTS).get().await()
            val products = transformer.convertDocumentToProductList(result.documents)
            val selectedProducts = mutableListOf<Product>()
            selectedProducts.addAll(products.filter {
                it.name.lowercase().contains(searchName.lowercase())
            })
            Resource.Success(selectedProducts)
        } catch (e: Exception) {
            Resource.Error(errorMessage)
        }

    }

    // save products to user cart to buy it anytime.
    suspend fun addProductsToCart(
        list: List<Product>,
        deleteFavoriteProducts: Boolean
    ): Resource<Any> {
        return try {
            val cartProductsList = getAllUserProducts().data
            list.forEach { product ->
                // check if same item saved to cart before and if it saved before here we will get last quantity saved of item and
                // added to new product quantity.
                if (cartProductsList != null && cartProductsList.any { it.id == product.id }) {
                    val selectedProduct = cartProductsList.last { it.id == product.id }
                    product.quantity += selectedProduct.quantity
                }
                cartCollection.document(product.id).set(product).await()
            }
            if (deleteFavoriteProducts)
                favoriteDao.deleteAllProducts()
            Resource.Success(Any())
        } catch (e: Exception) {
            Resource.Error(errorMessage)
        }

    }

    // delete specific product from user cart.
    suspend fun deleteProductFromUserCart(productId: String) {
        cartCollection.document(productId).delete().await()
    }

    // get all products from user cart to show into cart fragment.
    suspend fun getAllUserProducts(): Resource<List<Product>> {
        return try {
            val result = cartCollection.get().await()
            val products = transformer.convertDocumentToProductList(result.documents)
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(errorMessage)
        }
    }

    // after submit the order successfully here we'll add all cart products to incomplete orders to show to admin panel
    //and delete it from user cart.
    suspend fun uploadProductsToOrders(
        cartProductsList: Array<Product>,
        userLocation: String,
        totalCost: Float
    ): Resource<Order> {
        return try {
            val orderCollection = fireStore.collection(INCOMPLETE_ORDERS)
            val id = orderCollection.document().id
            val orderModel = Order(
                id,
                userUid,
                System.currentTimeMillis(),
                userLocation,
                OrderEnums.PLACED,
                totalCost,
                cartProductsList.toList()
            )
            orderCollection.document(id).set(transformer.orderToMap(orderModel)).await()
            removeUserCartProducts()
            Resource.Success(orderModel)
        } catch (e: Exception) {
            Resource.Error(errorMessage)
        }
    }

    // delete all products from user cart .
    private suspend fun removeUserCartProducts() {
        cartCollection.get().await().let {
            it.forEach { doc ->
                cartCollection.document(doc.id).delete().await()
            }
        }
    }

    suspend fun getUserOrders(): Resource<List<Order>> {
        return try {
            val result = fireStore.collection(INCOMPLETE_ORDERS).whereEqualTo("userUid", userUid).get().await()
            val orders = transformer.convertDocumentsToOrderList(result.documents)
            Resource.Success(orders)
        }catch (e: Exception){
            println(">>>>>>>>>>>>>> ${e.message}")
            Resource.Error(errorMessage)
        }
    }
}