package com.example.inventorywidget.view

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.inventorywidget.R
import com.example.inventorywidget.data.preferences.WidgetPreferences
import com.example.inventorywidget.databinding.ActivityLoginBinding
import com.example.inventorywidget.utils.Resource
import com.example.inventorywidget.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint  // ✅ This enables Hilt dependency injection in this Activity
class LoginActivity : AppCompatActivity() {

    // ✅ Hilt will automatically inject the ViewModel with all its dependencies
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding
    
    // Variables para manejar la redirección desde el widget
    private var fromWidget: Boolean = false
    private var widgetAction: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si viene desde el widget
        fromWidget = intent.getBooleanExtra(InventoryWidgetProvider.EXTRA_FROM_WIDGET, false)
        widgetAction = intent.getStringExtra(InventoryWidgetProvider.EXTRA_WIDGET_ACTION)

        // Check if user is already logged in
        if (viewModel.verifyUserIsLoggedIn()) {
            handleSuccessfulAuth()
            return
        }

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Remove action bar and set fullscreen
        supportActionBar?.hide()
        setFullscreen()

        setupTextWatchers()
        setupObservers()
        setupListeners()
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateFields()
            }
        }

        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validatePassword()
                validateFields()
            }
        })
    }

    private fun validatePassword() {
        val password = binding.passwordEditText.text.toString()

        when {
            password.isEmpty() -> {
                binding.passwordInputLayout.error = null
                binding.passwordInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.white)
            }
            password.length < 6 -> {
                binding.passwordInputLayout.error = "Mínimo 6 dígitos"
                binding.passwordInputLayout.setBoxStrokeColorStateList(
                    ContextCompat.getColorStateList(this, R.color.red_error)!!
                )
            }
            else -> {
                binding.passwordInputLayout.error = null
                binding.passwordInputLayout.boxStrokeColor = ContextCompat.getColor(this, R.color.white)
            }
        }
    }

    private fun validateFields() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()

        val isEmailValid = email.isNotEmpty()
        val isPasswordValid = password.length >= 6

        val allFieldsValid = isEmailValid && isPasswordValid

        binding.loginButton.isEnabled = allFieldsValid
        binding.registerTextView.isEnabled = allFieldsValid
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    handleSuccessfulAuth()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            viewModel.login(email, password)
        }

        binding.registerTextView.setOnClickListener {
            if (binding.registerTextView.isEnabled) {
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString().trim()
                viewModel.register(email, password)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
        binding.registerTextView.isEnabled = !isLoading
    }

    /**
     * Maneja la navegación después de autenticación exitosa
     * Criterio 10: Si viene del widget para mostrar saldo, actualiza el widget y muestra el saldo
     * Criterio 13: Si viene del widget para gestionar, va al Home Inventario
     */
    private fun handleSuccessfulAuth() {
        if (fromWidget) {
            when (widgetAction) {
                InventoryWidgetProvider.ACTION_SHOW_BALANCE -> {
                    // Criterio 10: Mostrar el saldo en el widget después del login
                    val widgetPreferences = WidgetPreferences(this)
                    widgetPreferences.setBalanceVisible(true)
                    
                    // Actualizar todos los widgets
                    InventoryWidgetProvider.updateAllWidgets(this)
                    
                    // Mostrar mensaje y cerrar la actividad
                    Toast.makeText(this, "Saldo visible en el widget", Toast.LENGTH_SHORT).show()
                    finish()
                }
                InventoryWidgetProvider.ACTION_MANAGE -> {
                    // Criterio 13: Ir al Home Inventario después del login
                    navigateToMain()
                }
                else -> {
                    navigateToMain()
                }
            }
        } else {
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onDestroy() {
        super.onDestroy()
        // Optional: Clear binding reference to avoid memory leaks
    }
}