package com.amsavarthan.security.extensions

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.amsavarthan.security.R
import com.amsavarthan.security.data.adapter.ItemsAdapter
import com.google.android.material.textfield.MaterialAutoCompleteTextView

@BindingAdapter(value = ["text", "nullText"], requireAll = true)
fun TextView.setTextViewValue(value: String?, nullText: String) {
    text = if (value.isNullOrBlank()) {
        nullText
    } else {
        value
    }
}

@BindingAdapter(value = ["setAdapter"])
fun RecyclerView.assignAdapter(itemsAdapter: ItemsAdapter) {
    itemAnimator = DefaultItemAnimator()
    adapter = itemsAdapter
}

@BindingAdapter(value = ["setAdapter", "value"], requireAll = true)
fun <T> MaterialAutoCompleteTextView.assignAdapter(adapter: ArrayAdapter<T>, selected: String?) {
    setAdapter(adapter)
    setText(selected)
    adapter.filter.filter(null)
}

@BindingAdapter(value = ["icon"])
fun ImageView.setIcon(isHelpLineItem: Boolean) {
    val drawableResourceId = if (isHelpLineItem)
        R.drawable.ic_call_24
    else
        R.drawable.ic_baseline_clear_24
    val drawable = ContextCompat.getDrawable(context, drawableResourceId)
    setImageDrawable(drawable)
}
