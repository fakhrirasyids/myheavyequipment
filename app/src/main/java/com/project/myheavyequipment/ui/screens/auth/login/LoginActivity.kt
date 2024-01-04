package com.project.myheavyequipment.ui.screens.auth.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.project.myheavyequipment.R
import com.project.myheavyequipment.data.factory.AuthViewModelFactory
import com.project.myheavyequipment.data.remote.ApiConfig
import com.project.myheavyequipment.databinding.ActivityLoginBinding
import com.project.myheavyequipment.ui.screens.main.MainActivity
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.AccountPreferences.Companion.preferenceDefaultValue
import com.project.myheavyequipment.utils.Event
import com.project.myheavyequipment.utils.Result
import com.project.myheavyequipment.utils.dataStore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        AuthViewModelFactory(
            ApiConfig.getApiService(),
            AccountPreferences.getPrefInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeError()
        observeLoading()
        observeToast()
        observeLogin()

        setListeners()
    }

    private fun observeError() {
        binding.apply {
            loginViewModel.canLogin.observe(this@LoginActivity) { login ->
                btnLogin.isEnabled = login
            }
        }
    }

    private fun observeLoading() {
        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun observeToast() {
        loginViewModel.toastMessage.observe(this) { messageEvent ->
            messageEvent.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
    }

    private fun observeLogin() {
        loginViewModel.getAccountToken().observe(this) { preferences ->
            if (preferences != preferenceDefaultValue) {
                loginViewModel.getAccountID().observe(this) { userID ->

                    val iMain = Intent(this, MainActivity::class.java)
                    iMain.putExtra(MainActivity.ACCOUNT_TOKEN, preferences)
                    iMain.putExtra(MainActivity.ACCOUNT_ID, userID)

                    startActivity(iMain)
                    finishAffinity()
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            edEmailLogin.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                if (!edEmailLogin.error.isNullOrEmpty() || edEmailLogin.text.isNullOrEmpty()) {
                    loginViewModel.isEmailError.postValue(true)
                } else {
                    loginViewModel.isEmailError.postValue(false)
                }
            })

            edPasswordLogin.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                if (!edPasswordLogin.error.isNullOrEmpty() || edPasswordLogin.text.isNullOrEmpty()) {
                    loginViewModel.isPassError.postValue(true)
                } else {
                    loginViewModel.isPassError.postValue(false)
                }
            })

            btnLogin.setOnClickListener {
                showLoading(true)
                val email = edEmailLogin.text.toString()
                val pass = edPasswordLogin.text.toString()

                loginViewModel.login(email, pass).observe(this@LoginActivity) { res ->
                    when (res) {
                        is Result.Loading -> {
                            loginViewModel.isLoading.postValue(true)
                        }

                        is Result.Error -> {
                            loginViewModel.isLoading.postValue(false)
                            if (res.error.substringBefore(" ") == getString(R.string.unable)) {
                                loginViewModel.toastMessage.postValue(Event(getString(R.string.error_no_internet)))
                            } else {
                                loginViewModel.toastMessage.postValue(Event(getString(R.string.error_account_invalid)))
                            }
                        }

                        is Result.Success -> {
                            loginViewModel.isLoading.postValue(false)
                            loginViewModel.saveToken(
                                userID = res.data.user!!.id!!.toString(),
                                userName = res.data.user.name!!,
                                userEmail = res.data.user.email!!,
                                userToken = res.data.accessToken!!
                            )
                        }

                        is Result.ErrorFirstFetch -> {}
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressbar.isVisible = isLoading
            btnLogin.isVisible = !isLoading
            edEmailLogin.isEnabled = !isLoading
            edPasswordLogin.isEnabled = !isLoading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}