package com.project.myheavyequipment.ui.screens.addalber

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.project.myheavyequipment.BuildConfig
import com.project.myheavyequipment.R
import com.project.myheavyequipment.data.factory.MainViewModelFactory
import com.project.myheavyequipment.data.remote.ApiConfig
import com.project.myheavyequipment.databinding.ActivityAddAlberBinding
import com.project.myheavyequipment.databinding.LayoutCustomDialogSavedQrBinding
import com.project.myheavyequipment.ui.screens.main.MainActivity
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.Event
import com.project.myheavyequipment.utils.Result
import com.project.myheavyequipment.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar

class AddAlberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAlberBinding

    private val accountToken by lazy { intent.getStringExtra(MainActivity.ACCOUNT_TOKEN) }
    private val addAlberViewModel by viewModels<AddAlberViewModel> {
        MainViewModelFactory(
            accountToken,
            ApiConfig.getApiService(),
            AccountPreferences.getPrefInstance(dataStore),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAlberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeShowMore()
        observeToast()

        setToolbar()
        setListeners()
    }

    private fun observeShowMore() {
        addAlberViewModel.showMoreInput.observe(this@AddAlberActivity) {
            showMoreHandle(it)
        }
    }

    private fun observeToast() {
        addAlberViewModel.toastMessage.observe(this) { messageEvent ->
            messageEvent.getContentIfNotHandled()?.let { message ->
                showToast(message)
            }
        }
    }


    private fun setToolbar() {
        binding.apply {
            toolbar.navigationIcon?.mutate()?.let {
                it.setTint(ContextCompat.getColor(this@AddAlberActivity, R.color.orange))
                binding.toolbar.navigationIcon = it
            }

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setListeners() {
        binding.apply {
            btnShowMoreInput.setOnClickListener {
                addAlberViewModel.showMoreInput.postValue(!addAlberViewModel.showMoreInput.value!!)
            }

            btnAddAlber.setOnClickListener {
                if (isValid()) {
                    showLoading(true)

                    val jenis = binding.edAlberJenis.text.toString()
                    val type = binding.edAlberType.text.toString()
                    val hoursMeter = Integer.parseInt(binding.edAlberHoursMeter.text.toString())
                    val kapasitas = Integer.parseInt(binding.edAlberKapasitas.text.toString())
                    val engine = binding.edAlberEngine.text.toString()

                    val liftingHeight =
                        if (!binding.edAlberLiftingHeight.text.isNullOrEmpty()) Integer.parseInt(
                            binding.edAlberLiftingHeight.text.toString()
                        ) else null

                    val stage =
                        if (!binding.edAlberStage.text.isNullOrEmpty()) Integer.parseInt(
                            binding.edAlberStage.text.toString()
                        ) else null

                    val loadCenter =
                        if (!binding.edAlberLoadCenter.text.isNullOrEmpty()) Integer.parseInt(
                            binding.edAlberLoadCenter.text.toString()
                        ) else null

                    lifecycleScope.launch(Dispatchers.Main) {
                        addAlberViewModel.postAlberToQRCode(
                            jenis,
                            type,
                            hoursMeter,
                            kapasitas,
                            engine,
                            liftingHeight,
                            stage,
                            loadCenter
                        ).observe(this@AddAlberActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        showLoading(true)
                                    }

                                    is Result.Success -> {
                                        if (checkPermission()) {
                                            Log.d(
                                                TAG,
                                                "onCreate: Permission already granted, create folder"
                                            )
                                            val time = Calendar.getInstance().time
                                            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
                                            val current = formatter.format(time)
                                            val fileName =
                                                "${jenis}_${type} - $current".replace(
                                                    ":",
                                                    "."
                                                ) + ".pdf"
                                            val file =
                                                File(
                                                    "${Environment.getExternalStorageDirectory()}/${
                                                        getString(
                                                            R.string.app_name
                                                        )
                                                    }"
                                                )
                                            val path =
                                                file.absolutePath + "/" + fileName
                                            createFolder()
                                            val savedFile = saveFile(result.data.body(), path)
                                            if (savedFile.isEmpty()) {
                                                showToast("Gagal menyimpan QR Code!")
                                            } else {
                                                Log.i(
                                                    TAG,
                                                    "setListeners: succesfully save $savedFile"
                                                )
                                                customDialogSavedFile(fileName, File(savedFile))
                                            }
                                        } else {
                                            Log.d(
                                                TAG,
                                                "onCreate: Permission was not granted, request"
                                            )
                                            requestPermission()
                                        }
                                        showLoading(false)
                                    }

                                    is Result.Error -> {
                                        showLoading(false)
                                        if (result.error.substringBefore(" ") == getString(R.string.unable)) {
                                            addAlberViewModel.toastMessage.postValue(Event(getString(R.string.error_no_internet)))
                                        } else {
                                            addAlberViewModel.toastMessage.postValue(Event(result.error))
                                        }
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

    private fun showMoreHandle(isShowMore: Boolean) {
        binding.apply {
            edAlberLiftingHeight.isVisible = isShowMore
            edAlberStage.isVisible = isShowMore
            edAlberLoadCenter.isVisible = isShowMore

            if (isShowMore) {
                btnShowMoreInput.text = StringBuilder("Lebih Sedikit Input")
                btnShowMoreInput.icon =
                    ContextCompat.getDrawable(this@AddAlberActivity, R.drawable.ic_up)
            } else {
                edAlberLiftingHeight.setText("")
                edAlberStage.setText("")
                edAlberLoadCenter.setText("")
                btnShowMoreInput.text = StringBuilder("Lebih Banyak Input")
                btnShowMoreInput.icon =
                    ContextCompat.getDrawable(this@AddAlberActivity, R.drawable.ic_down)
            }
        }
    }

    private fun saveFile(body: ResponseBody?, path: String): String {
        if (body == null)
            return ""
        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(path)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return path
        } catch (e: Exception) {
            Log.e("saveFile", e.toString())
        } finally {
            input?.close()
        }
        return ""
    }

    private fun isValid(): Boolean {
        binding.apply {
            if (edAlberJenis.text.isNullOrEmpty()) {
                showToast("Jenis Unit tidak boleh kosong!")
                return false
            } else if (edAlberType.text.isNullOrEmpty()) {
                showToast("Type tidak boleh kosong!")
                return false
            } else if (edAlberHoursMeter.text.isNullOrEmpty()) {
                showToast("Hours Meter tidak boleh kosong!")
                return false
            } else if (edAlberKapasitas.text.isNullOrEmpty()) {
                showToast("Kapasitas tidak boleh kosong!")
                return false
            } else if (edAlberEngine.text.isNullOrEmpty()) {
                showToast("Engine tidak boleh kosong!")
                return false
            } else {
                return true
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressbar.isVisible = isLoading
            btnAddAlber.isVisible = !isLoading
            btnShowMoreInput.isEnabled = !isLoading
            edAlberJenis.isEnabled = !isLoading
            edAlberType.isEnabled = !isLoading
            edAlberHoursMeter.isEnabled = !isLoading
            edAlberKapasitas.isEnabled = !isLoading
            edAlberEngine.isEnabled = !isLoading
            edAlberLiftingHeight.isEnabled = !isLoading
            edAlberStage.isEnabled = !isLoading
            edAlberLoadCenter.isEnabled = !isLoading
        }
    }

    private fun createFolder() {
        val file =
            File("${Environment.getExternalStorageDirectory()}/${getString(R.string.app_name)}")

        if (!file.isDirectory) {
            val folderCreated = file.mkdir()

            if (folderCreated) {
                showToast("Folder Created: ${file.absolutePath}")
            } else {
                showToast("Folder not created....")
            }
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Log.d("TAG", "requestPermission: try")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e("TAG", "requestPermission: ", e)
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "storageActivityResultLauncher: ")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Log.d(
                        TAG,
                        "storageActivityResultLauncher: Manage External Storage Permission is granted"
                    )
                    createFolder()
                } else {
                    Log.d(
                        TAG,
                        "storageActivityResultLauncher: Manage External Storage Permission is denied...."
                    )
                    showToast("Manage External Storage Permission is denied....")
                }
            } else {
                //Android is below 11(R)
            }
        }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read) {
                    Log.d(TAG, "onRequestPermissionsResult: External Storage Permission granted")
                    createFolder()
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: External Storage Permission denied...")
                    showToast("External Storage Permission denied...")
                }
            }
        }
    }

    private fun customDialogSavedFile(filename: String, path: File) {
        val pdfUri = FileProvider.getUriForFile(
            this@AddAlberActivity,
            BuildConfig.APPLICATION_ID + ".provider",
            path
        )

        val dialogSavedFileBinding: LayoutCustomDialogSavedQrBinding =
            LayoutCustomDialogSavedQrBinding.inflate(layoutInflater)

        val dialogSavedFile = Dialog(this)

        dialogSavedFile.setContentView(dialogSavedFileBinding.root)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogSavedFile.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogSavedFile.setCancelable(false)

        dialogSavedFileBinding.apply {
            tvFileName.text = filename

            btnOpenFile.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(
                    pdfUri, "application/pdf"
                )
                startActivity(intent)
            }

            btnShareFile.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
                shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                shareIntent.type = "application/pdf"
                startActivity(Intent.createChooser(shareIntent, "Bagikan File"))
            }

            btnClose.setOnClickListener {
                dialogSavedFile.dismiss()
                finish()
            }
        }

        dialogSavedFile.window?.setLayout(lp.width, lp.height)
        dialogSavedFile.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }

    private companion object {
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
    }

}