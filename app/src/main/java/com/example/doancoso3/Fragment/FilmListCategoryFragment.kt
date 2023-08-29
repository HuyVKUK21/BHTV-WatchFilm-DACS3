package com.example.doancoso3

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doancoso3.Activity.FilmInCategoryActivity
import com.example.doancoso3.Activity.MovieDetailsActivity
import com.example.doancoso3.Adapter.FilmListCategoryAdapter
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.ListMovieChapter
import com.example.doancoso3.Interface.RcInterface
import com.example.doancoso3.databinding.FragmentCategoryFilmBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FilmListCategoryFragment(val nameCategory: String, val list: MutableList<Film>, val userId: String) : Fragment() {
    lateinit var filmListMovie: ArrayList<ListMovieChapter>
    lateinit var binding: FragmentCategoryFilmBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val arrayList: ArrayList<Film> = ArrayList(list)
        filmListMovie = arrayListOf<ListMovieChapter>()
        binding = FragmentCategoryFilmBinding.inflate(layoutInflater)
        // gắn adapter cho FlimListCategoryAdapter
        binding.rvFilm.adapter = FilmListCategoryAdapter(nameCategory, list, object : RcInterface {
            override fun goiSukien(pos: Int) {
                val movieId = list[pos].movieId
                // get link phim để truyền vào MovieDetailsActivity
                val dbRef = FirebaseDatabase.getInstance().getReference("ListOfEpisodes")
                val query = dbRef.orderByChild("movieId").equalTo(movieId)
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        filmListMovie.clear()
                        if(snapshot.exists()) {
                            for(listMovieSnap in snapshot.children) {
                                val listMovieData = listMovieSnap.getValue(ListMovieChapter::class.java)
                                filmListMovie.add(listMovieData!!)
                            }
                        }
                        val i = Intent(requireContext(), MovieDetailsActivity::class.java)
                        i.putExtra("movieId", list[pos].movieId)
                        i.putExtra("nameCategory", nameCategory)
                        i.putExtra("categoryId", list[pos].categoryId)
                        i.putExtra("movieName", list[pos].movieName)
                        i.putExtra("movieEpisodes", list[pos].movieEpisodes)
                        i.putExtra("movieTime", list[pos].movieTime)
                        i.putExtra("movieDescription", list[pos].movieDescription)
                        i.putExtra("userId", userId)
                        i.putExtra("myArrayList", arrayList)
                        i.putExtra("filmListMovie", filmListMovie)
                        startActivity(i)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        })
        binding.rvFilm.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        binding.tvViewALlFilm.setOnClickListener {
            val intent = Intent(context, FilmInCategoryActivity::class.java)
            intent.putExtra("categoryId", list[0].categoryId.toString())
            intent.putExtra("categoryName", nameCategory)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        binding.tvCategoryName.text = nameCategory
        binding.tvCategoryName.setGradientColor()
        return binding.root
    }
}

private fun TextView.setGradientColor() {
    paint.shader = LinearGradient(
        0f,
        0f,
        0f,
        textSize,
        ContextCompat.getColor(this.context, R.color.textview_gradient_start),
        ContextCompat.getColor(this.context, R.color.textview_gradient_end),
        Shader.TileMode.CLAMP
    )
    invalidate()
}
