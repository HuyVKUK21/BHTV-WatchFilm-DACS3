package com.example.doancoso3.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doancoso3.Adapter.SearchResultAdapter
import com.example.doancoso3.Data.Category
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.ListMovieChapter
import com.example.doancoso3.Interface.DatabaseCallback
import com.example.doancoso3.Interface.RcInterface
import com.example.doancoso3.databinding.ActivityFilmInCategoryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class FilmInCategoryActivity : AppCompatActivity(), DatabaseCallback {
    lateinit var binding: ActivityFilmInCategoryBinding
    private lateinit var filmList: MutableList<Film>
    private lateinit var filmTempList: MutableList<Film>
    private lateinit var categoryId: String
    private lateinit var categoryName: String
    private lateinit var userId: String
    lateinit var arrayList: ArrayList<Film>
    lateinit var filmListMovie: ArrayList<ListMovieChapter>
    private lateinit var listCategoryFilm: ArrayList<Film>
    private lateinit var listAll: ArrayList<Film>
    private lateinit var nameCategory: ArrayList<Category>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilmInCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listAll = arrayListOf<Film>()
        val i = intent.extras
        categoryId = ""
        userId = ""
        categoryName = ""
        if (i != null) {
            categoryId = i.getString("categoryId").toString()
            categoryName = i.getString("categoryName").toString()
            userId = i.getString("userId").toString()
        }
        binding.tvCategoryTitle.setText(categoryName)
        binding.imgBack.setOnClickListener {
            finish()
        }

        getData(this)
    }


    override fun onDatabaseLoaded() {
        filmTempList = mutableListOf()
        for (i in filmList) {
            if (i.categoryId == categoryId) {
                filmTempList.add(i)
            }
        }

        filmListMovie = arrayListOf<ListMovieChapter>()
        arrayList = arrayListOf<Film>()
        listCategoryFilm = arrayListOf<Film>()
        nameCategory = arrayListOf<Category>()

        val dbRef = FirebaseDatabase.getInstance().getReference("ListOfEpisodes")
        val dbRef_1 = FirebaseDatabase.getInstance().getReference("Film")
        val dbRef_2 = FirebaseDatabase.getInstance().getReference("Category")
        binding.rvFilmInCategory.adapter = SearchResultAdapter(filmTempList, object : RcInterface {
            override fun goiSukien(pos: Int) {
                val movieId = filmTempList[pos].movieId
                val query = dbRef.orderByChild("movieId").equalTo(movieId)
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        filmListMovie.clear()
                        if (snapshot.exists()) {
                            for (listMovieSnap in snapshot.children) {
                                val listMovieData =
                                    listMovieSnap.getValue(ListMovieChapter::class.java)
                                filmListMovie.add(listMovieData!!)
                            }
                        }
                        val query_1 = dbRef_1.orderByChild("movieId").equalTo(movieId)
                        query_1.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                arrayList.clear()
                                if (snapshot.exists()) {
                                    for (arrayListSnap in snapshot.children) {
                                        val arrayListData = arrayListSnap.getValue(Film::class.java)
                                        arrayList.add(arrayListData!!)
                                    }
                                }
                                listCategoryFilm.clear()
                                for (i in filmList) {
                                    if (arrayList[0].categoryId == i.categoryId) {
                                        listCategoryFilm.add(i)
                                    }
                                }
                                val query_2 = dbRef_2.orderByChild("categoryId")
                                    .equalTo(arrayList[0].categoryId)
                                query_2.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        nameCategory.clear()
                                        if (snapshot.exists()) {
                                            for (nameCategorySnap in snapshot.children) {
                                                val nameCategoryData =
                                                    nameCategorySnap.getValue(Category::class.java)
                                                nameCategory.add(nameCategoryData!!)
                                            }
                                        }
                                        val i = Intent(
                                            this@FilmInCategoryActivity,
                                            MovieDetailsActivity::class.java
                                        )
                                        i.putExtra("movieId", filmTempList[pos].movieId)
                                        i.putExtra(
                                            "nameCategory",
                                            nameCategory[0].categoryName.toString()
                                        )
                                        i.putExtra("categoryId", filmTempList[pos].categoryId)
                                        i.putExtra("movieName", filmTempList[pos].movieName)
                                        i.putExtra(
                                            "movieEpisodes",
                                            filmTempList[pos].movieEpisodes
                                        )
                                        i.putExtra("movieTime", filmTempList[pos].movieTime)
                                        i.putExtra(
                                            "movieDescription",
                                            filmTempList[pos].movieDescription
                                        )
                                        i.putExtra("userId", userId)
                                        i.putExtra("myArrayList", listCategoryFilm)
                                        i.putExtra("filmListMovie", filmListMovie)
                                        startActivity(i)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })


                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
        })
        binding.rvFilmInCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    fun getData(callback: DatabaseCallback) {
        val dbRef2 = FirebaseDatabase.getInstance().getReference("Film")
        filmList = mutableListOf()
        dbRef2.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (filmSnap in snapshot.children) {
                        val filmData = filmSnap.getValue(Film::class.java)
                        if (filmData != null) {
                            filmList.add(filmData)
                        }
                    }
                }
            }
            callback.onDatabaseLoaded()
        }
    }
}