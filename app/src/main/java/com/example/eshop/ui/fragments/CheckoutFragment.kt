 package com.example.eshop.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.eshop.R
import com.example.eshop.ui.ViewModels.CheckoutViewModel
import com.example.eshop.databinding.FragmentCheckoutBinding
import com.example.eshop.models.Order
import com.example.eshop.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResultCallback
import org.json.JSONException
import org.json.JSONObject

 class CheckoutFragment : BottomSheetDialogFragment() {

     private var _binding: FragmentCheckoutBinding? = null

     private val binding get() = _binding!!

     private val checkoutViewModel by activityViewModels<CheckoutViewModel>()

     private val args by navArgs<CheckoutFragmentArgs>()
     private val totalCost by lazy { args.totalCost }
     private val cartProductsList by lazy { args.productList }
     private lateinit var orderModel: Order
     private var userLocation = ""

     private val secretKey = "sk_test_51OPVjgEAiZgfWXnZvzEfzJOJhfrN9jBh0p8kQKJg72h3bmBJkdy8Lvx2yRXgrnEmFf2UUuTbbNHD3PWBlrOIf2hN00ILnsHj8n"
     private val publishableKey = "pk_test_51OPVjgEAiZgfWXnZmnKT8uhOpLyyaHw6p6O3jtvEbI9jxEOem0SfFIWK3XI8591KucGVlw8oWy0guioD6oFxK430006wOV3Hsx"
     private var customerId = ""
     private var ephemeralKey = ""
     private var clientSecret = ""
     private var orderPlaced=false

     private  lateinit var paymentSheet:PaymentSheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        val view = binding.root

        PaymentConfiguration.init(requireContext(), publishableKey)

        createCustomer()
        binding.orderProgress.visibility=View.GONE
        paymentSheet=  PaymentSheet(this, PaymentSheetResultCallback {
            if(it is PaymentSheetResult.Completed ){
                Toast.makeText(requireContext(),"Payment success",Toast.LENGTH_LONG)
                orderPlaced=true
                checkoutViewModel.pushUserOrder(cartProductsList, userLocation, totalCost)
            }
            binding.orderProgress.visibility=View.GONE
        })

        binding.closeDialImg.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })
        binding.orderNowTv.setOnClickListener(View.OnClickListener {
            binding.orderProgress.visibility = View.VISIBLE
            paymentSheet.presentWithPaymentIntent(clientSecret,
                PaymentSheet.Configuration(
                    "My Eshop",
                    PaymentSheet.CustomerConfiguration(customerId, ephemeralKey)
                )
            )
        })
        observeListener()

        return view
    }

     private fun observeListener() {
         // gt user information from firebase to get user location and save in userLocation object to pass with user order .
         checkoutViewModel.userInformationLiveData.observe(viewLifecycleOwner) { userInfo ->
             when (userInfo) {
                 is Resource.Success -> {
                     var userInfoModel = userInfo.data
                     userLocation=userInfoModel!!.userLocationName
                     binding.checkoutTotalCostTv.text=totalCost.toString()+"$"
                     binding.userLocationForOrderTv.text=userLocation
                     binding.userNameForOrderTv.text=userInfoModel!!.userName
                 }
                 is Resource.Error -> {
                     Toast.makeText(requireContext(), userInfo.msg, Toast.LENGTH_SHORT).show()
                 }
                 else -> {}
             }
         }

         // observe if payment process successfully after order uploaded and the money sent successfully.
         checkoutViewModel.orderProductsLiveData.observe(viewLifecycleOwner) {
             when (it) {
                 is Resource.Success -> {
                     orderModel= it.data!!
                     if(orderPlaced) {
                         navigateToOrderStatusFragment(true)
                     }
                 }
                 is Resource.Error -> {
                     navigateToOrderStatusFragment(false)
                 }
                 is Resource.Loading -> {}
                 else -> {}
             }
         }
     }

     private fun navigateToOrderStatusFragment(isOrderSubmitted: Boolean) {
         val action = CheckoutFragmentDirections.actionCheckoutFragmentToOrderStatusFragment(orderModel, isOrderSubmitted)
         findNavController().navigate(action)
     }

     private fun createCustomer(){
         var request= object:StringRequest(Request.Method.POST,"https://api.stripe.com/v1/customers",
              Response.Listener<String> {
                  try {
                      var responce = JSONObject(it)
                      customerId = responce.getString("id")
                      getEmphericalKey()
                  }catch (error: JSONException){
                      error.printStackTrace()
                  }
             },Response.ErrorListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
             }){
             override fun getHeaders():Map<String,String> {
                 val header = HashMap<String, String>()
                 header.put("Authorization","Bearer "+ secretKey)
                 return header
             }
         }
         val requestQueue = Volley.newRequestQueue(requireContext())
         requestQueue.add(request)
     }

     fun getEmphericalKey(){
         var request= object:StringRequest(Request.Method.POST,"https://api.stripe.com/v1/ephemeral_keys",
             Response.Listener<String> {
                 try {
                     var responce = JSONObject(it)
                     ephemeralKey = responce.getString("id")
                     getClientSecret(customerId)
                 }catch (error: JSONException){
                     error.printStackTrace()
                 }
             },Response.ErrorListener {
                 Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
             }){
             override fun getHeaders():Map<String,String> {
                 val header = HashMap<String, String>()
                 header.put("Authorization","Bearer "+ secretKey)
                 header.put("Stripe-Version","2023-10-16")
                 return header
             }

             override fun getParams(): MutableMap<String, String>? {
                 val params = HashMap<String, String>()
                 params.put("customer",customerId)
                 return params
             }
         }
         val requestQueue = Volley.newRequestQueue(requireContext())
         requestQueue.add(request)
     }

     fun getClientSecret(customerId:String){
         var request= object:StringRequest(Request.Method.POST,"https://api.stripe.com/v1/payment_intents",
             Response.Listener<String> {
                 try {
                     var responce = JSONObject(it)
                     clientSecret = responce.getString("client_secret")
                 }catch (error: JSONException){
                     error.printStackTrace()
                 }
             },Response.ErrorListener {
                 Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
             }){
             override fun getHeaders():Map<String,String> {
                 val header = HashMap<String, String>()
                 header.put("Authorization","Bearer "+ secretKey)
                 return header
             }

             override fun getParams(): MutableMap<String, String>? {
                 val params = HashMap<String, String>()
                 val cost = ""+(totalCost*100).toInt()
                 params.put("customer",customerId)
                 params.put("amount", cost)
                 params.put("currency","eur")
                 return params
             }
         }
         val requestQueue = Volley.newRequestQueue(requireContext())
         requestQueue.add(request)
     }
}