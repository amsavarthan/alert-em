package com.amsavarthan.security.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amsavarthan.security.data.database.guardian.Guardian
import com.amsavarthan.security.databinding.ItemContactBinding

class ItemsAdapter(
    private val handler: Handler,
    private val isHelpLineNumber: Boolean = false
) : ListAdapter<Guardian, ItemsAdapter.ViewHolder>(Companion) {

    inner class ViewHolder(
        private val binding: ItemContactBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Guardian) {
            binding.contact = item
            binding.handler = handler
            binding.icon = isHelpLineNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemContactBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object : DiffUtil.ItemCallback<Guardian>() {
        override fun areItemsTheSame(oldItem: Guardian, newItem: Guardian): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber
        }

        override fun areContentsTheSame(oldItem: Guardian, newItem: Guardian): Boolean {
            return oldItem == newItem
        }
    }

    interface Handler {
        fun onIconClicked(guardian: Guardian)
        fun onItemClicked(guardian: Guardian)
    }

}