package com.example.doancoso3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Interface.RcInterface
import com.example.doancoso3.R
import kotlinx.android.synthetic.main.item_film_search.view.*

class SearchResultAdapter(val list: MutableList<Film>, val PhimOnClick : RcInterface): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class SearchResultViewHolder(item: View):RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film_search, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            if (list[position] != null) {
                val tvMoviesTitle = findViewById<TextView>(R.id.tvMoviesTitle)
                val tvMoviesDesc = findViewById<TextView>(R.id.tvMoviesDesc)
                val imgMoviesImage = findViewById<ImageView>(R.id.imgMoviesImage)
                tvMoviesTitle.text = list[position].movieName
                tvMoviesDesc.text = "Thời gian: ${list[position].movieTime} \nMới nhất: ${list[position].movieEpisodes}"
                Glide.with(this).load(list[position].movieImage).into(imgMoviesImage)
                layout_film.setOnClickListener {
                    PhimOnClick.goiSukien(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}