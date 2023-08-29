package com.example.doancoso3.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.doancoso3.Adapter.FavoriteMoviesAdapter
import com.example.doancoso3.Data.Category
import com.example.doancoso3.Data.FavoriteMovies
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Interface.DatabaseCallback
import com.example.doancoso3.Interface.FavoriteMoviesInterface
import com.example.doancoso3.MainActivity
import com.example.doancoso3.R
import com.example.doancoso3.databinding.ActivityFavoriteMoviesBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FavoriteMoviesActivity : AppCompatActivity(), DatabaseCallback {
    lateinit var binding: ActivityFavoriteMoviesBinding
    lateinit var userId: String
    private lateinit var filmList: MutableList<Film>
    private lateinit var filmTempList: MutableList<Film>
    private lateinit var favoriteMoviesList: MutableList<FavoriteMovies>
    private lateinit var favoriteMoviesTempList: MutableList<FavoriteMovies>
    lateinit var dialog: AlertDialog
    lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      Nhận dữ liệu từ MenuFragment gửi qua
        val i = intent.extras
        if (i != null) {
            userId = i.getString("userId").toString()
        }

//      xử lý sự kiện trở về
        binding.imgBack.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("userId",userId)
//            startActivity(intent)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

//      xử lý sự kiện trở về trang chủ
        binding.imgHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

//      duyệt dữ liệu từ firebase
        getDatabase(this)
    }

    override fun onDatabaseLoaded() {
        filmTempList = mutableListOf()
        favoriteMoviesTempList = mutableListOf()
        for (i in favoriteMoviesList) {
            if (i.userId == userId) {
                favoriteMoviesTempList.add(i)
                for (j in filmList) {
                    if (i.movieId == j.movieId) {
                        filmTempList.add(j)
                    }
                }
            }
        }
        binding.rvFavoriteMovies.adapter = FavoriteMoviesAdapter(filmTempList, favoriteMoviesTempList, object: FavoriteMoviesInterface {
            override fun holdItem(position: Int) {
                val build = AlertDialog.Builder(this@FavoriteMoviesActivity)
                val view = layoutInflater.inflate(R.layout.dialog_search_history_remove, null)
                build.setView(view)

//              Ánh xạ đến các element trong dialog_search_history_remove
                val btnDialogCancel = view.findViewById<Button>(R.id.btnDialogCancel)
                val btnDialogRemove = view.findViewById<Button>(R.id.btnDialogRemove)
                val tvDialogRemoveTitle = view.findViewById<TextView>(R.id.tvDialogRemoveTitle)
                val tvDialogQues = view.findViewById<TextView>(R.id.tvDialogQues)
                tvDialogRemoveTitle.setText(filmTempList[position].movieName)
                tvDialogQues.setText("Xóa khỏi yêu thích.")

//              Xử lý các sự kiện của các button
                btnDialogCancel.setOnClickListener {
                    dialog.dismiss()
                }
                btnDialogRemove.setOnClickListener {
                    dbRef = FirebaseDatabase.getInstance().getReference("FavoriteMovies").child(favoriteMoviesList[position].favoriteMoviesId.toString())
                    dbRef.removeValue()
                    getDatabase(this@FavoriteMoviesActivity)
                    dialog.dismiss()
                }
                dialog = build.create()
                dialog.show()
            }
        })
        binding.rvFavoriteMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun getDatabase(callback: DatabaseCallback) {
        val dbRef1 = FirebaseDatabase.getInstance().getReference("FavoriteMovies")
        favoriteMoviesList = mutableListOf()
        dbRef1.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (favoriteMoviesSnap in snapshot.children) {
                        val favoriteMoviesData = favoriteMoviesSnap.getValue(FavoriteMovies::class.java)
                        if (favoriteMoviesData != null) {
                            favoriteMoviesList.add(favoriteMoviesData)
                        }
                    }
                }
            }
        }

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