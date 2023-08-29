package com.example.doancoso3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doancoso3.Data.FavoriteMovies
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Interface.FavoriteMoviesInterface
import com.example.doancoso3.R

class FavoriteMoviesAdapter(val filmList: MutableList<Film>, val favoriteMoviesList: MutableList<FavoriteMovies>, val click: FavoriteMoviesInterface): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class FavoriteMoviesViewHolder(item: View):RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film_search, parent, false)
        return FavoriteMoviesViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            if (filmList[position] != null) {
                val tvMoviesTitle = findViewById<TextView>(R.id.tvMoviesTitle)
                val tvMoviesDesc = findViewById<TextView>(R.id.tvMoviesDesc)
                val imgMoviesImage = findViewById<ImageView>(R.id.imgMoviesImage)
                tvMoviesTitle.text = filmList[position].movieName
                tvMoviesDesc.text = "Thời gian: ${filmList[position].movieTime} \nMới nhất: ${filmList[position].movieEpisodes}"
                Glide.with(this).load(filmList[position].movieImage).into(imgMoviesImage)
            }

//          Xử lý sự kiện nhấn giữ item đó
            holder.itemView.setOnLongClickListener {
                click.holdItem(position)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return filmList.size
    }
}