package com.example.inventorywidget.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.appcompat.app.AlertDialog
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.inventorywidget.databinding.ActivityMainBinding
import com.example.inventorywidget.R
import com.example.inventorywidget.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)


        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment)

        )


        setupActionBarWithNavController(navController, appBarConfiguration)




        navController.addOnDestinationChangedListener { _, destination, _ ->


            binding.toolbar.visibility = View.VISIBLE
            supportActionBar?.show()
            binding.btnLogout.visibility = View.GONE

            // 2. Handle the EXCEPTIONS
            when (destination.id) {
                R.id.homeFragment -> {


                    // Show logout button only on home
                    binding.btnLogout.visibility = View.VISIBLE
                    binding.btnLogout.setOnClickListener {
                        mostrarDialogoCerrarSesion()
                    }
                }

                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
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
                viewModel.logout()
                navigateToLogin()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Optional: Clear binding reference to avoid memory leaks
    }


}