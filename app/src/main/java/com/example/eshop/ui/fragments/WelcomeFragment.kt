package com.example.eshop.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.eshop.R
import com.example.eshop.databinding.FragmentWelcomeBinding
import com.example.eshop.ui.ViewModels.WelcomeViewModel

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null

    private val binding get() = _binding!!

    private val viewModel by activityViewModels<WelcomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* check if it the first time for the user open the app
            so if it the first time Welcome fragment still shown but if its not will run
            a function that check if user logged in with firebase authentication if he has logged in
            will open MainFragment if not will open Authentication Fragment.
         */
        val isFirstLogIn = viewModel.checkIfFirstAppOpened()
        if(!isFirstLogIn){
            checkIfUserLoggedIn()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.getStartedBtn.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_authFragment)
        })
        return view
    }

    // check if user logged in and open main fragment if user logged in
    // or authentication fragment if not.
    private fun checkIfUserLoggedIn() {
        val isLoggedIn = viewModel.checkIfUserLoggedIn()
        if(isLoggedIn){
            findNavController().navigate(R.id.action_welcomeFragment_to_shopFragment)
        }else{
            findNavController().navigate(R.id.action_welcomeFragment_to_authFragment)
        }
    }
}