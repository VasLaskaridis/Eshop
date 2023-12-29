package com.example.eshop.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.eshop.R
import com.example.eshop.databinding.FragmentUserInfoBinding
import com.example.eshop.ui.MainActivity
import com.example.eshop.ui.ViewModels.UserInfoViewModel
import com.example.eshop.utils.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserInfoFragment : Fragment() {

    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()

    private var _binding: FragmentUserInfoBinding? = null

    private val binding get() = _binding!!

    private var mImageUri: Uri? = null

    private val args:UserInfoFragmentArgs by navArgs()
    private val userInfo by lazy { args.userInfo }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).hideBottomNav()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.userProfileImage.setOnClickListener(View.OnClickListener {
            changeUserProfileImage()
        })
        binding.userInfoSubmitBtn.setOnClickListener(View.OnClickListener {
            submitUserInfo()
        })
        if(userInfo != null){
            binding.userNameEt.setText(userInfo!!.userName)
            Glide.with(requireContext()).load(userInfo?.userImage).into(binding.userProfileImage)
            binding.selectLocationEt.setText(userInfo!!.userLocationName)
        }else {
            binding.userNameEt.setText("")
            binding.selectLocationEt.setText("")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
    }

    override fun onResume() {
        super.onResume()
        showUserImage()
    }

    private fun observeListener() {
        userInfoViewModel.userInfoLiveData.observe(viewLifecycleOwner) { info ->
            when (info) {
                is Resource.Success -> {
                    binding.progressBar.visibility=View.GONE
                    Toast.makeText(requireContext(), info.data, Toast.LENGTH_SHORT).show()
                    userInfoViewModel.setUserInformationValue()
                    findNavController().popBackStack()
                }

                is Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                    Toast.makeText(requireContext(), info.msg, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> binding.progressBar.visibility=View.VISIBLE
                else -> {}
            }
        }
    }

    fun submitUserInfo() = with(binding) {
        val userName = userNameEt.text.toString().trim()
        val userLocation = selectLocationEt.text.toString().trim()
        if (userName.isEmpty()) {
            Toast.makeText(requireContext(), "Please add your name first!", Toast.LENGTH_SHORT).show()
            return@with
        }
        if (mImageUri == null && userInfo == null) {
            Toast.makeText(requireContext(), "Please add an  image to your account!", Toast.LENGTH_SHORT).show()
            return@with
        }
        if (userLocation.isEmpty()) {
            Toast.makeText(requireContext(), "Please add your location first!", Toast.LENGTH_SHORT).show()
            return@with
        }
        userInfoViewModel.uploadUserInformation(userName, mImageUri, userLocation)

    }

    fun changeUserProfileImage() {
        selectImageFromGallery()
    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        showUserImage()
        resultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            mImageUri = result.data?.data
            showUserImage()
        }

    private fun showUserImage() {
        if (mImageUri != null)
            binding.userProfileImage.setImageURI(mImageUri)
    }
}