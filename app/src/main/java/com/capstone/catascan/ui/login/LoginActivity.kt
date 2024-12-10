package com.capstone.catascan.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.capstone.catascan.R
import com.capstone.catascan.Utils.setFullScreen
import com.capstone.catascan.Utils.setLoading
import com.capstone.catascan.data.UserModel
import com.capstone.catascan.data.pref.UserPreference
import com.capstone.catascan.data.pref.dataStore
import com.capstone.catascan.databinding.ActivityLoginBinding
import com.capstone.catascan.ui.main.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(dataStore)
    }

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(userPreference)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)
        setFullScreen(window)
        setData()
        setAction()
        setAnimation()
    }

    private fun setData() {
        viewModel.isLoading.observe(this) {
            setLoading(binding.progressBar, it)
        }

        viewModel.popupMessage.observe(this) {
            if (it.isNotEmpty() && it == "Login Success") {
                showAlertDialogAndDoAction(it) {
                    val loginResult = viewModel.loginResult.value
                    lifecycleScope.launch {
                        if (loginResult != null) {
                            viewModel.saveSession(
                                UserModel(
                                    binding.emailEditText.text.toString(),
                                    loginResult.token,
                                    true
                                )
                            )
                        }
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            } else if (it.isNotEmpty() && it != "Login Success"){
                showAlertDialogAndDoAction(it)
            }
        }
    }

    private fun setAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.emailEditText.doOnTextChanged { text, _, _, _ ->
            binding.edLoginEmail.error = if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) getString(
                R .string.invalid_email
            ) else null
        }
        binding.passwordEditText.doOnTextChanged { text, _, _, _ ->
            binding.edLoginPassword.error = if (text.isNullOrBlank() || text.length < 8) getString(R .string.password_must_be_at_least_8_characters) else null
        }
        binding.loginButton.setOnClickListener {
            viewModel.login(
                mapOf(
                    "email" to binding.emailEditText.text.toString(),
                    "password" to binding.passwordEditText.text.toString()
                )
            )
        }
    }

    private fun setAnimation() {

        val titleTextAnim = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val regisImgAnim = ObjectAnimator.ofFloat(binding.authImageView, View.ALPHA, 1f).setDuration(200)
        val loginAccountAnim = ObjectAnimator.ofFloat(binding.loginAccountTextView, View.ALPHA, 1f).setDuration(200)
        val loginTextAnim = ObjectAnimator.ofFloat(binding.LoginTextView, View.ALPHA, 1f).setDuration(200)
        val loginEmailAnim = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(200)
        val passwordAnim = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(200)
        val loginButtonAnim = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                titleTextAnim,
                regisImgAnim,
                loginAccountAnim,
                loginTextAnim,
                loginEmailAnim,
                passwordAnim,
                loginButtonAnim
            )
            start()
        }
    }

    private fun showAlertDialogAndDoAction(message: String, action: () -> Unit = {}): Boolean {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Message")
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
            action()
        }
        alertDialog.create().show()
        return true
    }
}