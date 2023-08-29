package com.example.doancoso3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.SearchHistory
import com.example.doancoso3.Interface.ClickSearchHistory
import com.example.doancoso3.R
import kotlinx.android.synthetic.main.item_content_search_history.view.*

class SearchHistoryAdapter(val list: MutableList<SearchHistory>, val click: ClickSearchHistory): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class SearchHistoryViewHolder(item: View):RecyclerView.ViewHolder(item)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_content_search_history, parent, false)
        return SearchHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            val btnHistory = findViewById<Button>(R.id.btnHistory)
            val btnNorthWest = findViewById<Button>(R.id.btnNorthWest)
            val tvContentSearchHistory = findViewById<TextView>(R.id.tvContentSearchHistory)
            tvContentSearchHistory.text = list[position].historyContent
//          lắng nghe sự kiện khi click vào btnNorthWest
            btnNorthWest.setOnClickListener {
                click.onClickNorthWest(position)
            }
//          lắng nghe sự kiện click vào btnHistory
            btnHistory.setOnClickListener {
                click.onClickHistory(position)
            }
//          Lắng nghe sự kiện khi giữ vào tvContentSearchHistory
            tvContentSearchHistory.setOnLongClickListener {
                click.onHoldTV(position)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}