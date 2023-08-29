package com.example.doancoso3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.ListMovieChapter
import com.example.doancoso3.Interface.RcInterface
import com.example.doancoso3.R

class FilmListCategoryAdapter( val nameCategory: String,val list: MutableList<Film>, val PhimOnClick: RcInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class FilmViewHolder(item: View):RecyclerView.ViewHolder(item)
    lateinit var ds: ArrayList<ListMovieChapter>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_film_catagory, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            val imgFilmImage = findViewById<ImageView>(R.id.imgFilmImage)
            val tvFilmName = findViewById<TextView>(R.id.tvFilmName)
            tvFilmName.text = list[position].movieName
            if (list[position] != null) {
                Glide.with(this).load(list[position].movieImage).into(imgFilmImage)
            }
            if (imgFilmImage != null) {
                imgFilmImage.setOnClickListener {
                    // gọi sự kiện của FlimListCategory
                    PhimOnClick.goiSukien(position)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size
}