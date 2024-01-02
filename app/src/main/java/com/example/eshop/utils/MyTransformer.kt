package com.example.eshop.utils

import com.example.eshop.db.Product
import com.example.eshop.models.CategoryItem
import com.example.eshop.models.MainShopItem
import com.example.eshop.models.Order
import com.example.eshop.models.OrderEnums
import com.example.eshop.models.UserInfo

import com.google.firebase.firestore.DocumentSnapshot

class MyTransformer {

    fun convertMapToProduct(map: Map<String, Any>): Product {
        return Product(
            map["id"].toString(),
            map["name"].toString(),
            map["image"].toString(),
            map["calories"].toString().toDouble(),
            map["carb"].toString().toDouble(),
            map["fat"].toString().toDouble(),
            map["detail"].toString(),
            map["protein"].toString().toDouble(),
            map["price"].toString().toDouble(),
            map["quantity"].toString().toDouble().toInt(),
            map["quantityType"].toString(),
            map["category"].toString(),
        )
    }

    fun convertArrayMapToProductList(map: ArrayList<Map<String, Any>>): List<Product> {
        val list = mutableListOf<Product>()
        map.forEach {
            list.add(convertMapToProduct(it))
        }
        return list
    }

    fun convertDocumentToProductList(document: List<DocumentSnapshot>): MutableList<Product> {
        val list = mutableListOf<Product>()
        document.forEach { map ->
            val product=
                Product(
                    map["id"].toString(),
                    map["name"].toString(),
                    map["image"].toString(),
                    map["calories"].toString().toDouble(),
                    map["carb"].toString().toDouble(),
                    map["fat"].toString().toDouble(),
                    map["detail"].toString(),
                    map["protein"].toString().toDouble(),
                    map["price"].toString().toDouble(),
                    map["quantity"].toString().toInt(),
                    map["quantityType"].toString(),
                    map["category"].toString(),
                )
            list.add(product)
        }
        return list
    }

    fun ProductToMap(product: Product): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["id"] = product.id
        map["name"] = product.name
        map["image"] = product.image
        map["calories"] = product.calories
        map["carb"] = product.carb
        map["fat"] = product.fat
        map["detail"] = product.detail
        map["protein"] = product.protein
        map["price"] = product.price
        map["quantity"] = product.quantity
        map["quantityType"] = product.quantityType
        map["category"] = product.category
        return map
    }

    fun convertDocumentsToCategoryList(documents: List<DocumentSnapshot>): List<CategoryItem> {
        val list = mutableListOf<CategoryItem>()
        documents.forEach {
            list.add(
                CategoryItem(it["id"].toString(), it["name"].toString(), it["image"].toString())
            )
        }
        return list
    }

    fun convertMapToCategoryItem(map: Map<String, Any>): CategoryItem =
        CategoryItem(map["id"].toString(), map["name"].toString(), map["image"].toString())

    fun convertToMainShopItem(categoryItem:CategoryItem,products: MutableList<Product>): MainShopItem {
        val item = MainShopItem(categoryItem.id, categoryItem.name, 50, false, products)
        return item
    }


    fun convertDocumentListToMainShopList(doc: MutableList<DocumentSnapshot>): MutableList<MainShopItem> {
        val shopList = mutableListOf<MainShopItem>()
        doc.forEach {
            val item = MainShopItem(
                id = it.data?.get("id").toString(),
                name = it.data?.get("name").toString(),
                showInSimpleStyle = it.data?.get("showInSimpleStyle").toString().toBoolean(),
                sort = it.data?.get("sort").toString().toInt()
            )
            shopList.add(item)
        }
        return shopList
    }

    fun orderToMap(order:Order): Map<String, Any>{
        val map = mutableMapOf<String, Any>()
        map.let {
            it["orderId"] = order.orderId
            it["userUid"] = order.userUid
            it["orderSubmittedTime"] = order.orderSubmittedTime
            it["orderLocation"] = order.orderLocation
            it["orderStatus"] = order.orderStatus
            it["totalCost"] = order.totalCost
            it["productsList"] = order.productsList
        }
        return map
    }

    fun convertDocumentsToOrderList(doc: List<DocumentSnapshot>): List<Order>{
        val list = mutableListOf<Order>()
        doc.forEach {
            println(">>>>>>>>>>>>>>>>>>>> ${it["productsList"]}")
            list.add(
                Order(
                    it["orderId"].toString(),
                    it["userUid"].toString(),
                    it["orderSubmittedTime"].toString().toLong(),
                    it["orderLocation"].toString(),
                    OrderEnums.valueOf(it["orderStatus"].toString()),
                    it["totalCost"].toString().toFloat(),
                    convertArrayMapToProductList(it["productsList"] as ArrayList<Map<String, Any>>),
                )
            )
        }
        return list
    }

    fun userInfoToMap(userInfo: UserInfo): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["userUid"] = userInfo.userUid
        map["userName"] = userInfo.userName
        map["userImage"] = userInfo.userImage
        map["userLocationName"] = userInfo.userLocationName
        return map
    }

    fun convertMapToUserInfo(map: Map<String, Any>): UserInfo {
        return UserInfo(
            map["userUid"].toString(),
            map["userName"].toString(),
            map["userImage"].toString(),
            map["userLocationName"].toString()
        )
    }
}
