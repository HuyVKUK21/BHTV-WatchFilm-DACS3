package com.example.doancoso3.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.doancoso3.R

class SpinnerSearchTypeAdapter(private val context: Context, private val items: List<String>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_search_type, parent, false)

        val itemTextView = view.findViewById<TextView>(R.id.tvItemSpinner)
        itemTextView.text = items[position]
        return view
    }

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = items.size
}