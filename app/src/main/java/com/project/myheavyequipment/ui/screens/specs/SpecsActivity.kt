package com.project.myheavyequipment.ui.screens.specs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.project.myheavyequipment.R
import com.project.myheavyequipment.data.model.HistoryItem
import com.project.myheavyequipment.data.model.PayloadItem
import com.project.myheavyequipment.databinding.ActivitySpecsBinding
import com.project.myheavyequipment.ui.screens.historydetail.HistoryDetailActivity

class SpecsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpecsBinding
    private lateinit var specificationItem: PayloadItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpecsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        specificationItem = intent.getParcelableExtra(KEY_SPECIFICATION)!!

        setToolbar()
        setContent()
    }

    private fun setToolbar() {
        binding.apply {
            toolbar.navigationIcon?.mutate()?.let {
                it.setTint(ContextCompat.getColor(this@SpecsActivity, R.color.orange))
                binding.toolbar.navigationIcon = it
            }

            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun setContent() {
        binding.apply {
            tvAlberName.text = StringBuilder("${specificationItem.jenis} ${specificationItem.type}")
            tvAlberJenis.text = StringBuilder(specificationItem.jenis.toString())
            tvAlberType.text = StringBuilder(specificationItem.type.toString())
            tvAlberHours.text = StringBuilder("${specificationItem.hoursMeter} jam")
            tvAlberCapacity.text = StringBuilder("${specificationItem.capacity} kg")
            tvAlberEngine.text = StringBuilder(specificationItem.engine.toString())
            tvAlberLifting.text =
                StringBuilder("${specificationItem.liftingHeight ?: 0} mm (${specificationItem.stage ?: 0} Stage)")
            tvAlberLoad.text = StringBuilder("${specificationItem.loadCenter ?: 0} mm")
            tvAlberStatus.text =
                if (specificationItem.latestStatus == 1) StringBuilder("Bekerja") else StringBuilder(
                    "Perbaikan"
                )
        }
    }

    companion object {
        const val KEY_SPECIFICATION = "key_specification"
    }
}