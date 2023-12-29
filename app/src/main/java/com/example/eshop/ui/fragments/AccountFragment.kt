package com.example.eshop.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.eshop.ui.ViewModels.AccountViewModel
import com.example.eshop.R
import com.example.eshop.databinding.FragmentAccountBinding
import com.example.eshop.models.UserInfo
import com.example.eshop.utils.Constants.DISPLAY_DIALOG
import com.example.eshop.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private var userInfoModel: UserInfo? = null

    private val viewModel by activityViewModels<AccountViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.orderContainer.setOnClickListener(View.OnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToAllOrdersFragment()
            findNavController().navigate(action)
        })
        binding.settingsContainer.setOnClickListener(View.OnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToUserInfoFragment(userInfoModel)
            findNavController().navigate(action)
        })
        binding.helpContainer.setOnClickListener(View.OnClickListener {
            // TODO: Add help information.
        })
        binding.aboutContainer.setOnClickListener(View.OnClickListener {
            // TODO: Add about app information.
        })
        binding.logoutContainer.setOnClickListener(View.OnClickListener {
            logout()
        })
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
    }

    private fun observeListener() {

        viewModel.userInformationLiveData.observe(viewLifecycleOwner) { userInfo ->
            when (userInfo) {
                is Resource.Success -> {
                    userInfoModel = userInfo.data
                    Glide.with(requireContext()).load(userInfoModel?.userImage).into(binding.userProfileImageImg)
                    binding.userNameTv.text=userInfoModel?.userName
                    binding.progressBar.visibility=View.GONE
                }

                is Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                    Toast.makeText(requireContext(), userInfo.msg, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> binding.progressBar.visibility=View.VISIBLE
                else -> {}
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        val action = AccountFragmentDirections.actionAccountFragmentToAuthenticationFragment()
        findNavController().navigate(action)
        Toast.makeText(requireContext(),"You have logged out successfully", Toast.LENGTH_SHORT).show()
    }
}