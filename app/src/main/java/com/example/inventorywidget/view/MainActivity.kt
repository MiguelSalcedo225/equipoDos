package com.example.inventorywidget.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AlertDialog
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.inventorywidget.databinding.ActivityMainBinding
import com.example.inventorywidget.R
import com.example.inventorywidget.repository.AuthRepository
import com.example.inventorywidget.viewmodel.LoginViewModel
import com.google.android.material.appbar.MaterialToolbar


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
//                R.id.loginFragment -> {
//                    // Hide toolbar and logout button on login
//                    binding.toolbar.visibility = View.GONE
//                    supportActionBar?.hide()
//                    binding.btnLogout.visibility = View.GONE
//                }
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


//    private fun displayUserInfo() {
//        val user = repository.currentUser
//        user?.let {
//            val info = """
//                Name: ${it.displayName ?: "N/A"}
//                Email: ${it.email ?: "N/A"}
//                UID: ${it.uid}
//            """.trimIndent()
//            binding.userInfoTextView.text = info
//        }
//    }

//    private fun setupListeners() {
//        binding.btnLogout.setOnClickListener {
//            repository.logout()
//
//        }
//    }

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