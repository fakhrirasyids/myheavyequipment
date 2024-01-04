package com.project.myheavyequipment.ui.screens.history

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.project.myheavyequipment.utils.Result
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myheavyequipment.R
import com.project.myheavyequipment.data.factory.HistoryViewModelFactory
import com.project.myheavyequipment.data.model.HistoryItem
import com.project.myheavyequipment.data.remote.ApiConfig
import com.project.myheavyequipment.databinding.ActivityHistoryBinding
import com.project.myheavyequipment.databinding.LayoutCustomDialogAddRemoveBinding
import com.project.myheavyequipment.databinding.LayoutCustomLoadingBinding
import com.project.myheavyequipment.ui.adapter.HistoryAdapter
import com.project.myheavyequipment.ui.screens.historydetail.HistoryDetailActivity
import com.project.myheavyequipment.ui.screens.historydetail.HistoryDetailActivity.Companion.KEY_HISTORY
import com.project.myheavyequipment.ui.screens.main.MainActivity
import com.project.myheavyequipment.utils.Constants.dateStringGenerator
import com.project.myheavyequipment.utils.Constants.reparationTextGenerator
import com.project.myheavyequipment.utils.Event
import java.text.SimpleDateFormat
import java.util.Date

@Suppress("DEPRECATION")
class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private var historyAdapter = HistoryAdapter()

    private val accountToken by lazy { intent.getStringExtra(MainActivity.ACCOUNT_TOKEN) }
    private val userID by lazy { intent.getStringExtra(MainActivity.ACCOUNT_ID) }

    private val alberJenis by lazy { intent.getStringExtra(KEY_ALBER_JENIS) }
    private val alberType by lazy { intent.getStringExtra(KEY_ALBER_TYPE) }
    private val alberId by lazy { intent.getIntExtra(KEY_ALBER_ID, -1) }

    private val historyViewModel by viewModels<HistoryViewModel> {
        HistoryViewModelFactory(
            accountToken!!,
            alberId,
            ApiConfig.getApiService()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeError()
        observeToast()
        observeAlberHistory()
        observeLatestStatus()

        setToolbar()
        setListeners()
    }

    private fun observeError() {
        historyViewModel.errorState.observe(this) { errorState ->
            if (errorState == 1) run {
                setLoadingState(4)
            }
        }
    }

    private fun observeToast() {
        historyViewModel.errorMessage.observe(this) { messageEvent ->
            messageEvent.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
    }

    private fun observeLatestStatus() {
        historyViewModel.latestStatus.observe(this) {
            historyViewModel.firstHistoryItem.observe(this) { firstHistory ->
                if (firstHistory != null) {
                    binding.tvAlberHistoryInfo.text = reparationTextGenerator(
                        firstHistory.updatedAt!!,
                        firstHistory.hoursMeter!!
                    )

                    if (it == 1) {
                        showStopRunningReparation(false, firstHistory)
                    } else {
                        showStopRunningReparation(true, firstHistory)
                    }
                }
            }
        }
    }

    private fun observeAlberHistory() {
        historyViewModel.alberHistory.observe(this) { alberHistoryList ->
            if (alberHistoryList == null) {
                setLoadingState(1)
            } else {
                if (alberHistoryList.isEmpty()) {
                    setLoadingState(2)
                } else {
                    setLoadingState(3)
                    setHistoryRecyclerView(alberHistoryList)
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnReload.setOnClickListener {
                historyViewModel.alberHistory.postValue(null)
                historyViewModel.getAlberHistoryList()
            }

            btnAddRemoveMaintenance.setOnClickListener {
                customDialogAlberAddRemoveMaintenance(historyViewModel.latestStatus.value!!)
            }
        }
    }

    private fun setToolbar() {
        binding.apply {
            toolbar.navigationIcon?.mutate()?.let {
                it.setTint(ContextCompat.getColor(this@HistoryActivity, R.color.orange))
                binding.toolbar.navigationIcon = it
            }

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun setHistoryRecyclerView(alberHistoryList: ArrayList<HistoryItem?>) {
        binding.apply {
            historyAdapter.setDataItem(alberHistoryList)

            historyAdapter.onHistoryClick = { _, history ->
                val iHistoryDetail = Intent(this@HistoryActivity, HistoryDetailActivity::class.java)
                iHistoryDetail.putExtra(KEY_HISTORY, history)
                iHistoryDetail.putExtra(KEY_ALBER_JENIS, alberJenis)
                iHistoryDetail.putExtra(KEY_ALBER_TYPE, alberType)
                startActivity(iHistoryDetail)
            }

            rvHistory.adapter = historyAdapter
            rvHistory.layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun customDialogAlberAddRemoveMaintenance(status: Int) {
        val dialogBinding: LayoutCustomDialogAddRemoveBinding =
            LayoutCustomDialogAddRemoveBinding.inflate(layoutInflater)

        val dialog = Dialog(this)

        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogBinding.apply {
            if (status == 1) {
                val formatter = SimpleDateFormat("dd-MM-yyyy")
                val date = Date()
                val current = formatter.format(date)

                tvMaintenanceTitle.text = StringBuilder("Tambah Perbaikan")
                tvMaintenanceDate.text = StringBuilder(current)

                edMaintenanceHoursMeter.isEnabled = true
                edMaintenanceNote.isEnabled = true

                btnAccept.text = StringBuilder("Setujui Perbaikan")
                btnCancel.text = StringBuilder("Batalkan Perbaikan")

                btnAccept.setOnClickListener {
                    if (edMaintenanceHoursMeter.text.toString().isEmpty()) {
                        showToast("Hours Meter harus diisi!")
                    } else if (edMaintenanceNote.text.toString().isEmpty()) {
                        showToast("Catatan harus diisi!")
                    } else {
                        dialog.dismiss()
                        val loaderDialogBinding: LayoutCustomLoadingBinding =
                            LayoutCustomLoadingBinding.inflate(layoutInflater)

                        val dialogLoader = Dialog(this@HistoryActivity)
                        dialogLoader.setContentView(loaderDialogBinding.root)
                        dialogLoader.setCancelable(false)
                        dialogLoader.window?.setLayout(lp.width, lp.height)
                        historyViewModel.storeAlberReparation(
                            alberId,
                            Integer.parseInt(
                                edMaintenanceHoursMeter.text.toString()
                            ), edMaintenanceNote.text.toString()
                        ).observe(this@HistoryActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        dialogLoader.show()
                                    }

                                    is Result.Success -> {
                                        dialogLoader.dismiss()

                                        historyViewModel.alberHistory.postValue(null)
                                        historyViewModel.getAlberHistoryList()

                                        if (result.data.success!!) {
                                            historyViewModel.latestStatus.postValue(2)
                                        }

                                        historyViewModel.errorMessage.postValue(Event(result.data.message.toString()))
                                    }

                                    is Result.Error -> {
                                        dialogLoader.dismiss()
                                        if (result.error.substringBefore(" ") == getString(R.string.unable)) {
                                            historyViewModel.errorMessage.postValue(Event(getString(R.string.error_no_internet)))
                                        } else {
                                            historyViewModel.errorMessage.postValue(Event(result.error))
                                        }
                                    }

                                    is Result.ErrorFirstFetch -> {}
                                }
                            }
                        }
                    }
                }

                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }
            } else {
                val firstHistory = historyViewModel.firstHistoryItem.value

                tvMaintenanceDate.text = dateStringGenerator(firstHistory!!.updatedAt)
                edMaintenanceHoursMeter.setText(firstHistory.hoursMeter)
                edMaintenanceNote.setText(firstHistory.note)
                tvMaintenanceTitle.text = StringBuilder("Hentikan Perbaikan")

                edMaintenanceHoursMeter.isEnabled = false
                edMaintenanceNote.isEnabled = false

                btnAccept.text = StringBuilder("Hetikan Perbaikan")
                btnCancel.text = StringBuilder("Batalkan Penghentian")

                btnAccept.setOnClickListener {
                    dialog.dismiss()
                    val loaderDialogBinding: LayoutCustomLoadingBinding =
                        LayoutCustomLoadingBinding.inflate(layoutInflater)

                    val dialogLoader = Dialog(this@HistoryActivity)
                    dialogLoader.setContentView(loaderDialogBinding.root)
                    dialogLoader.setCancelable(false)
                    dialogLoader.window?.setLayout(lp.width, lp.height)
                    historyViewModel.updateAlberReparationToWork(alberId, firstHistory.id!!)
                        .observe(this@HistoryActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        dialogLoader.show()
                                    }

                                    is Result.Success -> {
                                        dialogLoader.dismiss()

                                        historyViewModel.alberHistory.postValue(null)
                                        historyViewModel.getAlberHistoryList()

                                        if (result.data.success!!) {
                                            historyViewModel.latestStatus.postValue(1)
                                        }
                                        historyViewModel.errorMessage.postValue(Event(result.data.message.toString()))
                                    }

                                    is Result.Error -> {
                                        dialogLoader.dismiss()
                                        if (result.error.substringBefore(" ") == getString(R.string.unable)) {
                                            historyViewModel.errorMessage.postValue(Event(getString(R.string.error_no_internet)))
                                        } else {
                                            historyViewModel.errorMessage.postValue(Event(result.error))
                                        }
                                    }

                                    is Result.ErrorFirstFetch -> {}
                                }
                            }
                        }
                }

                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

            }

        }

        dialog.window?.setLayout(lp.width, lp.height)
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        historyViewModel.pauseJob()
    }

    override fun onResume() {
        super.onResume()
        historyViewModel.alberHistory.postValue(null)
        historyViewModel.getAlberHistoryList()
    }

    private fun showStopRunningReparation(isRunning: Boolean, firstHistory: HistoryItem) {
        binding.apply {
            tvAlberHistoryInfo.isVisible = isRunning

            if (isRunning) {
                if (userID != firstHistory.userId) {
                    btnAddRemoveMaintenance.isEnabled = false
                    btnAddRemoveMaintenance.text =
                        StringBuilder("Sedang diperbaiki oleh ${firstHistory.user?.name}")
                } else {
                    btnAddRemoveMaintenance.isEnabled = true
                    btnAddRemoveMaintenance.text = StringBuilder("Hentikan Perbaikan")
                }
            } else {
                btnAddRemoveMaintenance.isEnabled = true
                btnAddRemoveMaintenance.text = StringBuilder("Tambah Perbaikan")
            }
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
                    layoutContent.isVisible = false
                    tvEmptyHistory.isVisible = false
                    rvHistory.isVisible = false
                    layoutError.isVisible = false
                }

                2 -> {
                    progressbar.isVisible = false
                    layoutContent.isVisible = true
                    tvEmptyHistory.isVisible = true
                    rvHistory.isVisible = false
                    layoutError.isVisible = false
                }

                3 -> {
                    progressbar.isVisible = false
                    layoutContent.isVisible = true
                    tvEmptyHistory.isVisible = false
                    rvHistory.isVisible = true
                    layoutError.isVisible = false
                }

                4 -> {
                    progressbar.isVisible = false
                    layoutContent.isVisible = false
                    tvEmptyHistory.isVisible = false
                    rvHistory.isVisible = false
                    layoutError.isVisible = true
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        const val KEY_ALBER_ID = "key_alber_id"
        const val KEY_ALBER_JENIS = "key_alber_jenis"
        const val KEY_ALBER_TYPE = "key_alber_type"
    }
}