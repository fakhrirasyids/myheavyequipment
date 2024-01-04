package com.project.myheavyequipment.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myheavyequipment.data.model.PayloadItem
import com.project.myheavyequipment.databinding.ItemRowAlberBinding

class AlberAdapter : RecyclerView.Adapter<AlberAdapter.ViewHolder>() {

    var onAlberClick: ((View, Int) -> Unit)? = null

    private var dataAlber: ArrayList<PayloadItem?> = ArrayList()

    fun setDataItem(dataItem: ArrayList<PayloadItem?>) {
        this.dataAlber.clear()
        this.dataAlber = dataItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlberAdapter.ViewHolder {
        val itemBinding: ItemRowAlberBinding =
            ItemRowAlberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding.root)
    }

    override fun onBindViewHolder(holder: AlberAdapter.ViewHolder, position: Int) {
        dataAlber[position]?.let { holder.bind(it, position) }
    }

    override fun getItemCount() = dataAlber.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRowAlberBinding.bind(itemView)

        fun bind(payloadItem: PayloadItem, position: Int) {
            binding.apply {
                tvAlberTitle.text = StringBuilder("${payloadItem.jenis} ${payloadItem.type}")
                tvAlberStatus.text =
                    StringBuilder("Status: ${if (payloadItem.latestStatus == 1) "Bekerja" else "Perbaikan"}")

                root.setOnClickListener {
                    onAlberClick?.invoke(itemView, position)
                }
            }
        }
    }
}