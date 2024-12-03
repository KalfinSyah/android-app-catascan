package com.capstone.catascan.ui.register

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
import com.capstone.catascan.databinding.ActivityRegisterBinding
import com.capstone.catascan.ui.main.MainActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val userPreference: UserPreference by lazy {
        UserPreference.getInstance(dataStore)
    }

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(userPreference)
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
            if (it.isNotEmpty() && it == "Register Success") {
                showAlertDialogAndDoAction(it) {
                    val loginResult = viewModel.loginResult.value
                    lifecycleScope.launch {
                        viewModel.saveSession(UserModel(loginResult!!.name, binding.emailEditText.text.toString(), loginResult.token))
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            } else if (it.isNotEmpty() && it != "Register Success") {
                showAlertDialogAndDoAction(it)
            }
        }
        viewModel.agreeTerms.observe(this) {
            binding.registenButton.isEnabled = it
            binding.termsCheckBox.isChecked = it
        }
    }


    private fun setAction() {
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.usernameEditText.doOnTextChanged { text, _, _, _ ->
            binding.edRegisterName.error = if (text.isNullOrBlank()) getString(R .string.username_cannot_be_empty) else null
        }
        binding.emailEditText.doOnTextChanged { text, _, _, _ ->
            binding.edRegisterEmail.error = if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) getString(R.string.invalid_email) else null
        }
        binding.passwordEditText.doOnTextChanged { text, _, _, _ ->
            binding.edRegisterPassword.error = if (text.isNullOrBlank() || text.length < 8) getString(R.string.password_must_be_at_least_8_characters) else null
        }
        binding.registenButton.setOnClickListener {
            viewModel.register(
                binding.usernameEditText.text.toString(),
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }
        binding.termsCheckBox.setOnClickListener {
            binding.registenButton.isEnabled = binding.termsCheckBox.isChecked
            viewModel.agreeTerms.value = binding.termsCheckBox.isChecked
        }
    }

    private fun setAnimation() {
        val titleAnim = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val createAccountAnim = ObjectAnimator.ofFloat(binding.createAccountTextView, View.ALPHA, 1f).setDuration(200)
        val registerTextAnim = ObjectAnimator.ofFloat(binding.registerTextView, View.ALPHA, 1f).setDuration(200)
        val nameAnim = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(200)
        val emailAnim = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(200)
        val passwordAnim = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(200)
        val termsAnim = ObjectAnimator.ofFloat(binding.termsLayout, View.ALPHA, 1f).setDuration(200)
        val termsTextAnim = ObjectAnimator.ofFloat(binding.termsTextView, View.ALPHA, 1f).setDuration(200)
        val signupButtonAnim = ObjectAnimator.ofFloat(binding.registenButton, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                titleAnim,
                createAccountAnim,
                registerTextAnim,
                nameAnim,
                emailAnim,
                passwordAnim,
                termsAnim,
                termsTextAnim,
                signupButtonAnim
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