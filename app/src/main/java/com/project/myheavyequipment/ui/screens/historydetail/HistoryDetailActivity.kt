package com.project.myheavyequipment.ui.screens.historydetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.project.myheavyequipment.R
import com.project.myheavyequipment.data.model.HistoryItem
import com.project.myheavyequipment.databinding.ActivityHistoryDetailBinding
import com.project.myheavyequipment.ui.screens.history.HistoryActivity
import com.project.myheavyequipment.ui.screens.main.MainActivity
import com.project.myheavyequipment.utils.Constants.dateStringGenerator

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding
    private lateinit var history: HistoryItem

    private val alberJenis by lazy { intent.getStringExtra(HistoryActivity.KEY_ALBER_JENIS) }
    private val alberType by lazy { intent.getStringExtra(HistoryActivity.KEY_ALBER_TYPE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        history = intent.getParcelableExtra(KEY_HISTORY)!!

        setToolbar()
        setContent()
    }

    private fun setToolbar() {
        binding.apply {
            toolbar.navigationIcon?.mutate()?.let {
                it.setTint(ContextCompat.getColor(this@HistoryDetailActivity, R.color.orange))
                binding.toolbar.navigationIcon = it
            }

            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun setContent() {
        binding.apply {
            tvAlberName.text = StringBuilder("$alberJenis $alberType")

            tvAlberMekanik.text = history.user?.name
            tvAlberStartDate.text = dateStringGenerator(history.createdAt)
            tvAlberEndDate.text = dateStringGenerator(history.updatedAt)

            tvAlberJenis.text = alberJenis
            tvAlberType.text = alberType
            tvAlberHours.text = StringBuilder("${history.hoursMeter} Jam")

            tvAlberStatus.text = if (history.status == "1") "Bekerja" else "Perbaikan"
            tvAlberNote.text = history.note
        }
    }

    companion object {
        const val KEY_HISTORY = "key_history"
    }
}