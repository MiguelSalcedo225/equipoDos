package com.example.inventorywidget.view


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.inventorywidget.R
import com.example.inventorywidget.databinding.ActivityLoginBinding
import com.example.inventorywidget.utils.Resource
import com.example.inventorywidget.viewmodel.LoginViewModel
import com.example.inventorywidget.viewmodel.RegisterViewModel

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private val RViewModel: RegisterViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(viewModel.verifyUserIsLoggedIn()){
            navigateToMain()
            return
        }

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Remove action bar and set fullscreen
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

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
        viewModel.loginState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        RViewModel.registerState.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    navigateToMain()
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
                RViewModel.register(email, password)
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

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Optional: Clear binding reference to avoid memory leaks
    }
}