package com.example.inventorywidget.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AlertDialog
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.inventorywidget.databinding.ActivityMainBinding
import com.example.inventorywidget.R
import com.example.inventorywidget.viewmodel.AuthenticationState
import com.example.inventorywidget.viewmodel.LoginViewModel
import com.example.inventorywidget.viewmodel.LoginViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(application)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)

        observeAuthenticationState()


        navController.addOnDestinationChangedListener { _, destination, _ ->

            // 1. Set the default UI state for most fragments
            binding.btnLogout.visibility = View.GONE
            binding.toolbar.visibility = View.VISIBLE
            supportActionBar?.show()

            // 2. Handle the exceptions
            when (destination.id) {
                R.id.homeFragment -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    // Show logout button only on home
                    binding.btnLogout.visibility = View.VISIBLE
                    binding.btnLogout.setOnClickListener {
                        mostrarDialogoCerrarSesion()

                    }
                }
                R.id.loginFragment -> {
                    // Hide toolbar only on login
                    binding.toolbar.visibility = View.GONE
                    supportActionBar?.hide()
                }
            }
        }


    }
    override fun onSupportNavigateUp(): Boolean {


        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                loginViewModel.logout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeAuthenticationState() {
        loginViewModel.authenticationState.observe(this) { state ->
            when (state) {
                is AuthenticationState.Unauthenticated -> {

                    if (navController.currentDestination?.id != R.id.loginFragment) {
                        navController.navigate(R.id.action_global_to_loginFragment)
                    }
                }

                else -> {
                    // Do nothing (Loading, Error, etc.)
                }
            }
        }
    }
}