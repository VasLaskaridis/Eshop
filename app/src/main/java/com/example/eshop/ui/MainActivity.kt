package com.example.eshop.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.eshop.R
import com.example.eshop.databinding.ActivityMainBinding
import com.example.eshop.utils.ConnectionLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private val connectionLiveData by lazy { ConnectionLiveData(this) }
    private var firstCheckInternetConnection = true


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        hideSystemUI()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener(this)
        binding.bottomNavigationView.setupWithNavController(navController)

        observeNetworkConnection()
    }

    private fun observeNetworkConnection() {
        connectionLiveData.observe(this) { isInternetAvailable ->
            if (isInternetAvailable && !firstCheckInternetConnection) {
                Snackbar.make(
                    binding.mainCl,
                    "Back Online",
                    Snackbar.LENGTH_SHORT
                )
                    .setBackgroundTint(getColor(R.color.green))
                    .show()
            } else if (!isInternetAvailable) {
                Snackbar.make(
                    binding.mainCl,
                    "Connection Lost",
                    Snackbar.LENGTH_SHORT
                )
                    .setBackgroundTint(getColor(R.color.red))
                    .show()
            }
            firstCheckInternetConnection = false
        }

    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.shopFragment, R.id.cartFragment, R.id.favoriteFragment,
            R.id.exploreFragment, R.id.accountFragment, R.id.checkoutFragment -> showBottomNav()

            else -> hideBottomNav()
        }
    }

    override fun onPause() {
        super.onPause()
        firstCheckInternetConnection = true
    }

    fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    fun showBottomNav(){
        val navigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if(!navigation.isShown)
            navigation.visibility=View.VISIBLE
    }

    fun hideBottomNav(){
        val navigation = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navigation.visibility=View.GONE
    }

}