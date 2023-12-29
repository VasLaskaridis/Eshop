package com.example.eshop.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.eshop.R
import com.example.eshop.ui.ViewModels.SpecificProductViewModel
import com.example.eshop.databinding.FragmentSpecificProductBinding
import com.example.eshop.db.Product
import com.example.eshop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpecificProductFragment : Fragment() {

    private var _binding: FragmentSpecificProductBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<SpecificProductFragmentArgs>()
    private val product by lazy { args.product }
    private val specificProductViewModel by activityViewModels<SpecificProductViewModel>()
    private var productQuantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSpecificProductBinding.inflate(inflater, container, false)
        val view = binding.root
        initViews()
        observeListener()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeListener()
    }

    private fun observeListener() {
        // check if product saved in favorite database and change icon image as following.
        specificProductViewModel.favoriteLiveData(product.id).observe(viewLifecycleOwner) { product ->
            if (product != null) {
                Glide.with(requireContext()).load(R.drawable.ic_favorite_2).into(binding.favoriteProductImg)
            } else {
                Glide.with(requireContext()).load(R.drawable.ic_favorite).into(binding.favoriteProductImg)
            }
        }
        specificProductViewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Product has been added to cart", Toast.LENGTH_SHORT).show()
                    specificProductViewModel.setCartProductValue()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun initViews() {
        binding.backButton.setOnClickListener(View.OnClickListener {
            findNavController().popBackStack()
        })

        binding.shareProductImgbtn.setOnClickListener(View.OnClickListener {
            shareProduct()
        })
        Glide.with(requireContext()).load(product.image).into(binding.specificProductImageImg)
        Glide.with(requireContext()).load(product.image).into(binding.specificProductShadowImg)

        binding.productNameTv.text=product.name
        binding.productQuantityTv.text=product.quantityType

        binding.favoriteProductImg.setOnClickListener(View.OnClickListener {
            specificProductViewModel.saveProductInFavorites(product)
        })

        binding.productQuantityEt.setText(product.quantity.toString())

        binding.productQuantityMinusImg.setOnClickListener(View.OnClickListener {
            changeProductQuantity(false)
        })
        binding.productQuantityPlusImg.setOnClickListener(View.OnClickListener {
            changeProductQuantity(true)
        })

        binding.productPriceTv.text = product.price.toString()+"$"
        binding.textProductDetailTv.text=product.detail

        // change total price by quantity value in edit text.
        binding.productQuantityEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val quantity = binding.productQuantityEt.text.toString().trim()
                if (quantity.isNotEmpty() && TextUtils.isDigitsOnly(quantity)) {
                    productQuantity = quantity.toDouble().toInt()
                    if (productQuantity > 0) {
                        binding.productPriceTv.text = ""+(product.price * productQuantity)+"$"
                    }
                }
            }
        })
        binding.specificProductImageImg.transitionName = product.image

        binding.productCaloriesTv.text=product.calories.toString()
        binding.productFatTv.text=product.fat.toString()
        binding.productProteinTv.text=product.protein.toString()
        binding.productCarbTv.text=product.carb.toString()
        binding.addToBasketBtn.setOnClickListener(View.OnClickListener {
            addProductToCart()
        })
    }

    private fun createTempProductWithNewQuantity(): Product? {
        val quantity = binding.productQuantityEt.text.toString().trim()
        return if (TextUtils.isDigitsOnly(quantity)) {
            product.copy().let { temp ->
                temp.quantity = productQuantity
                temp
            }
        } else {
            binding.productQuantityEt.setError("Please add valid quantity!", null)
            null
        }
    }

    override fun onResume() {
        super.onResume()
        binding.specificProductImageImg.visibility = View.VISIBLE
    }

    private fun changeProductQuantity(increasePrice: Boolean) {
        // increase or decrease quantity value .
        var quantity = binding.productQuantityEt.text.toString().trim().toInt()
        if (increasePrice) {
            quantity++
        } else if (!increasePrice && quantity > 1) {
            quantity--
        }
        binding.productQuantityEt.setText(quantity.toString())
    }

    // share product with messaging app.
    private fun shareProduct() {
        val intent = Intent(Intent.ACTION_SEND)
        val shareBody =
            getString(R.string.shareProduct, product.name, product.price)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(intent)
    }

    private fun addProductToCart() {
        // change product quantity with what user last saved.
        val productTemp = createTempProductWithNewQuantity()
        if (productTemp != null)
            specificProductViewModel.addProductToCart(productTemp)
    }

}