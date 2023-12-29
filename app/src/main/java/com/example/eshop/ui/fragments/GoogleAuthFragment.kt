package com.example.eshop.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.eshop.ui.ViewModels.GoogleAuthViewModel
import com.example.eshop.R
import com.example.eshop.databinding.FragmentGoogleAuthBinding
import com.example.eshop.utils.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GoogleAuthFragment : Fragment() {

    private var _binding: FragmentGoogleAuthBinding? = null

    private val binding get() = _binding!!

    private val viewModel by activityViewModels<GoogleAuthViewModel>()

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoogleAuthBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
        configureGoogleSignIn()
    }

    private fun observeListener() {
        viewModel.googleAuthLiveData.observe(viewLifecycleOwner) { userState ->
            when (userState) {
                is Resource.Loading -> binding.progressBar.visibility=View.VISIBLE
                is Resource.Success -> {
                    findNavController().navigate(R.id.action_googleAuthFragment_to_shopFragment)
                    binding.progressBar.visibility=View.GONE
                }

                is Resource.Error -> {
                    binding.progressBar.visibility=View.GONE
                    Toast.makeText(requireContext(), userState.msg, Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }

                else -> {}
            }
        }
    }

    // start google authentication intent to show all google save accounts to choose an account to sign in with it.
    private fun configureGoogleSignIn() {
        val mGoogleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            googleSignInOptions
        )
        val signInIntent = mGoogleSignInClient.signInIntent
        val googleLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                viewModel.handleGoogleAuthRequest(task,getString(R.string.errorMessage))
            }
        googleLauncher.launch(signInIntent)
    }
}