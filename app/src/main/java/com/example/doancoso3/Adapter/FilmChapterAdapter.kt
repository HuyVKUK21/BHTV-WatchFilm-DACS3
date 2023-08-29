package com.example.doancoso3.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.ListMovieChapter
import com.example.doancoso3.Interface.ChapterInterface
import com.example.doancoso3.R
import kotlinx.android.synthetic.main.chapter_layout.view.*



class FilmChapterAdapter(val ds : List<Film> = mutableListOf(), val movieId : String, val PhimOnCliok : ChapterInterface, val list: ArrayList<ListMovieChapter> = arrayListOf()) : RecyclerView.Adapter<FilmChapterAdapter.MovieChapterViewHolder>() {
    inner class MovieChapterViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieChapterViewHolder {
        val context = LayoutInflater.from(parent.context).inflate(R.layout.chapter_layout, parent, false)
        return MovieChapterViewHolder(context)
    }
    var selectedPosition = 0
    var lastSelectedPosition = -1
    override fun onBindViewHolder(holder: MovieChapterViewHolder, position: Int) {
        holder.itemView.apply {

            tv_chapterMovie.text = ds[position].movieEpisodes.toString()


            tv_chapterMovie.setOnClickListener {
                lastSelectedPosition = selectedPosition
                selectedPosition = holder.bindingAdapterPosition
                notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)

                PhimOnCliok.goiSukien(position, list)

            }
            if (selectedPosition == holder.getBindingAdapterPosition()) {
                tv_chapterMovie.setBackgroundResource(R.drawable.tv_chapter_click)
            } else {
                tv_chapterMovie.setBackgroundResource(R.drawable.tv_chapter)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}