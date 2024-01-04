package com.project.myheavyequipment.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myheavyequipment.data.model.HistoryItem
import com.project.myheavyequipment.databinding.ItemRowHistoryBinding
import com.project.myheavyequipment.utils.Constants.reparationTextGenerator
import java.text.SimpleDateFormat


class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var onHistoryClick: ((View, HistoryItem) -> Unit)? = null


    private var dataHistoryAlber: ArrayList<HistoryItem?> = ArrayList()

    fun setDataItem(dataItem: ArrayList<HistoryItem?>) {
        this.dataHistoryAlber.clear()
        this.dataHistoryAlber = dataItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding: ItemRowHistoryBinding =
            ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dataHistoryAlber[position]?.let { holder.bind(it) }
    }

    override fun getItemCount() = dataHistoryAlber.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRowHistoryBinding.bind(itemView)

        @SuppressLint("SimpleDateFormat")
        fun bind(history: HistoryItem) {
            binding.apply {
                tvAlberHistoryInfo.text = reparationTextGenerator(
                    history.updatedAt!!,
                    history.hoursMeter!!
                )

                root.setOnClickListener {
                    onHistoryClick?.invoke(itemView, history)
                }
            }
        }
    }

}