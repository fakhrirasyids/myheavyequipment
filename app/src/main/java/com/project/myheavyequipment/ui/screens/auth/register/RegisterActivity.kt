package com.project.myheavyequipment.ui.screens.auth.register

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
import com.project.myheavyequipment.databinding.ActivityRegisterBinding
import com.project.myheavyequipment.ui.screens.main.MainActivity
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.Event
import com.project.myheavyequipment.utils.Result
import com.project.myheavyequipment.utils.dataStore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel> {
        AuthViewModelFactory(
            ApiConfig.getApiService(),
            AccountPreferences.getPrefInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeError()
        observeLogin()
        observeLoading()
        observeToast()

        setListeners()
    }

    private fun observeError() {
        binding.apply {
            registerViewModel.canRegister.observe(this@RegisterActivity) { register ->
                btnRegister.isEnabled = register
            }
        }
    }

    private fun observeLoading() {
        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun observeToast() {
        registerViewModel.toastMessage.observe(this) { messageEvent ->
            messageEvent.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
    }

    private fun observeLogin() {
        registerViewModel.getAccountToken().observe(this) { preferences ->
            if (preferences != AccountPreferences.preferenceDefaultValue) {
                registerViewModel.getAccountID().observe(this) { userID ->

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
            edNameRegister.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                if (edNameRegister.text.isNullOrEmpty()) {
                    registerViewModel.isNameError.postValue(true)
                } else {
                    registerViewModel.isNameError.postValue(false)
                }
            })

            edEmailRegister.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                if (!edEmailRegister.error.isNullOrEmpty() || edEmailRegister.text.isNullOrEmpty()) {
                    registerViewModel.isEmailError.postValue(true)
                } else {
                    registerViewModel.isEmailError.postValue(false)
                }
            })

            edPasswordRegister.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                if (!edPasswordRegister.error.isNullOrEmpty() || edPasswordRegister.text.isNullOrEmpty()) {
                    registerViewModel.isPassError.postValue(true)
                } else {
                    registerViewModel.isPassError.postValue(false)
                }
            })

            btnRegister.setOnClickListener {
                showLoading(true)
                val name = edNameRegister.text.toString()
                val email = edEmailRegister.text.toString()
                val pass = edPasswordRegister.text.toString()

                registerViewModel.register(name, email, pass)
                    .observe(this@RegisterActivity) { res ->
                        when (res) {
                            is Result.Loading -> {
                                registerViewModel.isLoading.postValue(true)
                            }

                            is Result.Error -> {
                                registerViewModel.isLoading.postValue(false)
                                if (res.error.substringBefore(" ") == getString(R.string.use)) {
                                    registerViewModel.toastMessage.postValue(Event(getString(R.string.error_email_invalid)))
                                } else if (res.error.substringBefore(" ") == getString(R.string.unable)) {
                                    registerViewModel.toastMessage.postValue(Event(getString(R.string.error_no_internet)))
                                } else {
                                    registerViewModel.toastMessage.postValue(Event(res.error))
                                }
                            }

                            is Result.Success -> {
                                registerViewModel.isLoading.postValue(false)
                                registerViewModel.saveToken(
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
            btnRegister.isVisible = !isLoading
            edNameRegister.isEnabled = !isLoading
            edEmailRegister.isEnabled = !isLoading
            edPasswordRegister.isEnabled = !isLoading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}