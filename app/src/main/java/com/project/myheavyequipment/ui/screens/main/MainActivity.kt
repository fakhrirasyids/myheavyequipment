package com.project.myheavyequipment.ui.screens.main

import android.app.Dialog
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myheavyequipment.R
import com.project.myheavyequipment.data.factory.MainViewModelFactory
import com.project.myheavyequipment.data.model.PayloadItem
import com.project.myheavyequipment.data.remote.ApiConfig
import com.project.myheavyequipment.databinding.ActivityMainBinding
import com.project.myheavyequipment.databinding.LayoutCustomCodeOptionBinding
import com.project.myheavyequipment.databinding.LayoutCustomDialogBinding
import com.project.myheavyequipment.databinding.LayoutCustomLoadingBinding
import com.project.myheavyequipment.ui.adapter.AlberAdapter
import com.project.myheavyequipment.ui.screens.addalber.AddAlberActivity
import com.project.myheavyequipment.ui.screens.history.HistoryActivity
import com.project.myheavyequipment.ui.screens.history.HistoryActivity.Companion.KEY_ALBER_ID
import com.project.myheavyequipment.ui.screens.history.HistoryActivity.Companion.KEY_ALBER_JENIS
import com.project.myheavyequipment.ui.screens.history.HistoryActivity.Companion.KEY_ALBER_TYPE
import com.project.myheavyequipment.ui.screens.landing.LandingActivity
import com.project.myheavyequipment.ui.screens.profile.ProfileActivity
import com.project.myheavyequipment.ui.screens.scanner.ScannerActivity
import com.project.myheavyequipment.ui.screens.specs.SpecsActivity
import com.project.myheavyequipment.ui.screens.specs.SpecsActivity.Companion.KEY_SPECIFICATION
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.Result
import com.project.myheavyequipment.utils.dataStore


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var alberAdapter: AlberAdapter

    private val accountToken by lazy { intent.getStringExtra(ACCOUNT_TOKEN) }
    private val userID by lazy { intent.getStringExtra(ACCOUNT_ID) }

    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModelFactory(
            accountToken,
            ApiConfig.getApiService(),
            AccountPreferences.getPrefInstance(dataStore)
        )
    }

    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                val urlQrCode = result.data!!.getStringExtra(URL_QR_CODE)

                if (urlQrCode != null) {
                    observeAddedQrCode(urlQrCode)
                }
            }
        } else if (result.resultCode == RESULT_ERROR) {
            showToast("Gagal Scan!")
        }
    }

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            mainViewModel.alberList.postValue(null)
            mainViewModel.getAlberList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeUsername()
        observeAlberList()
        observeError()

        setupFabButtons()
        setListeners()
    }

    private fun observeError() {
        mainViewModel.errorState.observe(this) { errorState ->
            if (errorState == 1) run {
                setLoadingState(4)
            }
        }
    }

    private fun observeUsername() {
        mainViewModel.getAccountName().observe(this) { name ->
            if (name != AccountPreferences.preferenceDefaultValue) {
                binding.tvUsername.text = StringBuilder("Halo, $name")
            }
        }
    }

    private fun observeAlberList() {
        mainViewModel.alberList.observe(this@MainActivity) { alberList ->
            if (alberList == null) {
                setLoadingState(1)
            } else {
                if (alberList.isEmpty()) {
                    setLoadingState(2)
                } else {
                    setLoadingState(3)
                    setAlberRecyclerView(alberList)
                }
            }
        }
    }

    private fun observeAddedQrCode(urlQrCode: String?) {
        val loaderDialogBinding: LayoutCustomLoadingBinding =
            LayoutCustomLoadingBinding.inflate(layoutInflater)

        val dialogLoader = Dialog(this@MainActivity)
        dialogLoader.setContentView(loaderDialogBinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogLoader.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogLoader.setCancelable(false)
        dialogLoader.window?.setLayout(lp.width, lp.height)
        mainViewModel.scanQRCode(urlQrCode!!).observe(this@MainActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        dialogLoader.show()
                    }

                    is Result.Success -> {
                        dialogLoader.dismiss()
                        showToast(result.data.message.toString())
                    }

                    is Result.Error -> {
                        dialogLoader.dismiss()
                        showToast(getString(R.string.error_scan_alber))
                    }

                    is Result.ErrorFirstFetch -> {
                    }
                }
            }
        }

    }

    private fun setListeners() {
        binding.apply {
            btnReload.setOnClickListener {
                mainViewModel.alberList.postValue(null)
                mainViewModel.getAlberList()
            }

            btnQrCode.setOnClickListener {
                val iScanner = Intent(this@MainActivity, ScannerActivity::class.java)
                scannerLauncher.launch(iScanner)
            }

            btnOptionLink.setOnClickListener {
                optionLinkDialog()
            }

            btnLogout.setOnClickListener {
                val loaderDialogBinding: LayoutCustomLoadingBinding =
                    LayoutCustomLoadingBinding.inflate(layoutInflater)

                val dialogLoader = Dialog(this@MainActivity)
                dialogLoader.setContentView(loaderDialogBinding.root)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialogLoader.window?.attributes)
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialogLoader.setCancelable(false)
                dialogLoader.window?.setLayout(lp.width, lp.height)
                dialogLoader.show()

                mainViewModel.logoutAccount().observe(this@MainActivity) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                dialogLoader.show()
                            }

                            is Result.Success -> {
                                val iLanding =
                                    Intent(this@MainActivity, LandingActivity::class.java)
                                mainViewModel.clearAccountPreferences()
                                dialogLoader.dismiss()
                                finishAffinity()
                                startActivity(iLanding)
                            }

                            is Result.Error -> {
                                dialogLoader.dismiss()
                                showToast("Gagal Log out!")
                            }

                            is Result.ErrorFirstFetch -> {

                            }
                        }
                    }
                }
            }
        }
    }

    private fun optionLinkDialog() {
        val dialogOptionLinkBinding: LayoutCustomCodeOptionBinding =
            LayoutCustomCodeOptionBinding.inflate(layoutInflater)

        val dialogOption = Dialog(this)
        dialogOption.setContentView(dialogOptionLinkBinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogOption.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogOption.window?.setLayout(lp.width, lp.height)

        dialogOptionLinkBinding.apply {
            btnConfirm.setOnClickListener {
                if (edAlberCode.text.toString().isEmpty()) {
                    showToast("Kode Alat tidak boleh kosong!")
                } else {
                    observeAddedQrCode(edAlberCode.text.toString())
                    dialogOption.dismiss()
                }
            }
        }

        dialogOption.show()
    }

    private fun setAlberRecyclerView(alberList: ArrayList<PayloadItem?>) {
        binding.apply {
            alberAdapter = AlberAdapter()
            Log.i("TAG", "setAlberRecyclerView: ${alberList.size}")
            alberAdapter.setDataItem(alberList)

            alberAdapter.onAlberClick = { _, position ->
                customDialogAlberOptions(position)
            }

            rvAlber.adapter = alberAdapter
            rvAlber.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun customDialogAlberOptions(position: Int) {
        val dialogBinding: LayoutCustomDialogBinding =
            LayoutCustomDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(this)

        dialog.setContentView(dialogBinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.window?.setLayout(lp.width, lp.height)
        dialog.show()

        mainViewModel.alberList.observe(this@MainActivity) { alberList ->
            if (!alberList.isNullOrEmpty()) {
                dialogBinding.apply {
                    tvAlberName.text =
                        StringBuilder("${alberList[position]?.jenis} ${alberList[position]?.type}")

                    btnHistory.setOnClickListener {
                        dialog.dismiss()
                        val iHistory = Intent(this@MainActivity, HistoryActivity::class.java)
                        iHistory.putExtra(ACCOUNT_TOKEN, accountToken)
                        iHistory.putExtra(KEY_ALBER_ID, alberList[position]?.id)
                        iHistory.putExtra(KEY_ALBER_JENIS, alberList[position]?.jenis)
                        iHistory.putExtra(KEY_ALBER_TYPE, alberList[position]?.type)
                        iHistory.putExtra(ACCOUNT_ID, userID)
                        activityLauncher.launch(iHistory)
                    }

                    btnSpecs.setOnClickListener {
                        dialog.dismiss()
                        val iSpecs = Intent(this@MainActivity, SpecsActivity::class.java)
                        iSpecs.putExtra(KEY_SPECIFICATION, alberList[position])

                        activityLauncher.launch(iSpecs)
                    }

                    btnDeleteAlber.setOnClickListener {
                        dialog.dismiss()
                        val loaderDialogBinding: LayoutCustomLoadingBinding =
                            LayoutCustomLoadingBinding.inflate(layoutInflater)

                        val dialogLoader = Dialog(this@MainActivity)
                        dialogLoader.setContentView(loaderDialogBinding.root)
                        lp.copyFrom(dialogLoader.window?.attributes)
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                        dialogLoader.setCancelable(false)
                        dialogLoader.window?.setLayout(lp.width, lp.height)
                        alberList[position]?.id?.let { index ->
                            mainViewModel.deleteAlber(index).observe(this@MainActivity) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        dialogLoader.show()
                                    }

                                    is Result.Success -> {
                                        dialogLoader.dismiss()
                                        showToast(result.data.message.toString())
                                    }

                                    is Result.Error -> {
                                        dialogLoader.dismiss()
                                        showToast(result.error)
                                    }

                                    is Result.ErrorFirstFetch -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupFabButtons() {
        binding.apply {
            fabMenu.shrink()

            fabMenu.setOnClickListener {
                expandOrCollapseFAB()
            }

            fabMenuAccount.setOnClickListener {
                val iAccount = Intent(this@MainActivity, ProfileActivity::class.java)
                activityLauncher.launch(iAccount)
                expandOrCollapseFAB()
            }

            fabMenuAddAlber.setOnClickListener {
                val iAdd = Intent(this@MainActivity, AddAlberActivity::class.java)
                iAdd.putExtra(ACCOUNT_TOKEN, accountToken)
                activityLauncher.launch(iAdd)
                expandOrCollapseFAB()
            }
        }
    }

    private fun expandOrCollapseFAB() {
        if (binding.fabMenu.isExtended) {
            binding.fabMenu.shrink()
            binding.fabMenuAccount.hide()
            binding.fabTvAccount.visibility = View.GONE
            binding.fabMenuAddAlber.hide()
            binding.fabTvAddAlber.visibility = View.GONE
        } else {
            binding.fabMenu.extend()
            binding.fabMenuAccount.show()
            binding.fabTvAccount.visibility = View.VISIBLE
            binding.fabMenuAddAlber.show()
            binding.fabTvAddAlber.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setLoadingState(state: Int) {
        binding.apply {
            when (state) {
                1 -> {
                    progressbar.isVisible = true
                    tvEmptyAlber.isVisible = false
                    rvAlber.isVisible = false
                    btnReload.isVisible = false
                }

                2 -> {
                    tvEmptyAlber.text = getString(R.string.error_empty_alber)
                    progressbar.isVisible = false
                    tvEmptyAlber.isVisible = true
                    rvAlber.isVisible = false
                    btnReload.isVisible = false
                }

                3 -> {
                    progressbar.isVisible = false
                    tvEmptyAlber.isVisible = false
                    rvAlber.isVisible = true
                    btnReload.isVisible = false
                }

                4 -> {
                    tvEmptyAlber.text = getString(R.string.error_failed_fetch)
                    progressbar.isVisible = false
                    tvEmptyAlber.isVisible = true
                    rvAlber.isVisible = false
                    btnReload.isVisible = true
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.pauseJob()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.alberList.postValue(null)
        mainViewModel.getAlberList()
    }

    companion object {
        const val ACCOUNT_TOKEN: String = "account_token"
        const val ACCOUNT_ID: String = "account_id"
        const val URL_QR_CODE: String = "url_qr_code"
    }
}