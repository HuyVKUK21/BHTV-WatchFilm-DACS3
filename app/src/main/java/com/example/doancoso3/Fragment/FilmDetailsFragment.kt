package com.example.doancoso3.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import at.blogc.android.views.ExpandableTextView
import com.example.doancoso3.Adapter.ViewPagerAdapter
import com.example.doancoso3.Data.FavoriteMovies
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Interface.DatabaseCallback
import com.example.doancoso3.R
import com.example.doancoso3.SignInActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_film_details.*
import kotlinx.android.synthetic.main.item_film_catagory.*

class FilmDetailsFragment(val nameCategory: String, val list: MutableList<Film>) : Fragment(), DatabaseCallback {
    private lateinit var dialog: AlertDialog
    private lateinit var userId: String
    private lateinit var movieId: String
    private lateinit var favoriteMoviesList: MutableList<FavoriteMovies>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.fragment_film_details, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = ""
        movieId = ""
        getData(this)
        val expandableTextview = view.findViewById<ExpandableTextView>(R.id.expandableTextview)
        val tv_Title = view.findViewById<TextView>(R.id.tv_Title)
        val txt_toggle = view.findViewById<TextView>(R.id.txt_toggle)
        val tv_Desc = view.findViewById<ExpandableTextView>(R.id.expandableTextview)
        val movieTimeFilm = view.findViewById<TextView>(R.id.movieTime)
        val shareButton = view.findViewById<ImageView>(R.id.shareButton)
        val favoriteButton = view.findViewById<ImageView>(R.id.favoriteButton)
        val bundle = this.arguments
        val movieName = bundle?.getString("movieName")
        userId = bundle?.getString("userId").toString()
        movieId = bundle?.getString("movieId").toString()
        val movieTime = bundle?.getInt("movieTime")
        val movieDescription = bundle?.getString("movieDescription")

        // set animation trượt khi ấn nút xem thêm
        expandableTextview.setAnimationDuration(750L)
        expandableTextview.setInterpolator(OvershootInterpolator())

        tv_Title.setText(movieName)
        tv_Desc.setText(movieDescription)
        movieTimeFilm.text = movieTime.toString() + " Phút"

        // gọi sự kiện khi bấm nút xem thêm
        txt_toggle.setOnClickListener {
            showHideDescription()
        }

        shareButton.setOnClickListener {
            val message = "Xem phim '${movieName}' trên ứng dụng BH TV ngay. Ứng dụng có mặt trên Play Store!"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(intent, "Chia sẻ qua"))
        }

        if (favoriteButton != null) {
            favoriteButton.setOnClickListener {
                if (userId == "") {
                    val build = AlertDialog.Builder(requireContext())
                    val view = layoutInflater.inflate(R.layout.dialog_search_history_remove, null)
                    build.setView(view)

                    // Ánh xạ đến các element trong dialog_search_history_remove
                    val btnDialogCancel = view.findViewById<Button>(R.id.btnDialogCancel)
                    val btnDialogRemove = view.findViewById<Button>(R.id.btnDialogRemove)
                    val tvDialogRemoveTitle = view.findViewById<TextView>(R.id.tvDialogRemoveTitle)
                    val tvDialogQues = view.findViewById<TextView>(R.id.tvDialogQues)
                    tvDialogRemoveTitle.setText("Chưa đăng nhập!")
                    tvDialogQues.setText("Vui lòng đăng nhập tài khoản.")
                    btnDialogRemove.setText("Đồng ý")

                    //Xử lý các sự kiện của các button
                    btnDialogCancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    btnDialogRemove.setOnClickListener {
                        val intent = Intent(this.context, SignInActivity::class.java)
                        startActivity(intent)
                    }

                    dialog = build.create()
                    dialog.show()
                }else {
                    val dbRef = FirebaseDatabase.getInstance().getReference("FavoriteMovies")
                    val favoriteMoviesId = dbRef.push().key!!
                    val favorite = FavoriteMovies(favoriteMoviesId, movieId, userId)
                    dbRef.child(favoriteMoviesId).setValue(favorite).addOnCompleteListener {
                        favoriteButton.setImageResource(R.drawable.ic_baseline_favorite)
                        getData(this)
                    }
                }
            }
        }
    }

    override fun onDatabaseLoaded() {
        for (i in favoriteMoviesList) {
            if (userId != "" && i.movieId == movieId) {
                favoriteButton.setImageResource(R.drawable.ic_baseline_favorite)
                if (favoriteButton != null) {
                    favoriteButton.setOnClickListener {
                        val dbRef = FirebaseDatabase.getInstance().getReference("FavoriteMovies").child(i.favoriteMoviesId.toString())
                        val mTask = dbRef.removeValue()
                        mTask.addOnSuccessListener {
                            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                            getData(this)
                        }
                    }
                }
            }else if (userId != "" && i.movieId != movieId) {
                favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
//            else if (userId == "" && i.movieId == movieId) {
//                favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
//                if (favoriteButton != null) {
//                    favoriteButton.setOnClickListener {
//                        val build = AlertDialog.Builder(requireContext())
//                        val view = layoutInflater.inflate(R.layout.dialog_search_history_remove, null)
//                        build.setView(view)
//
//                        // Ánh xạ đến các element trong dialog_search_history_remove
//                        val btnDialogCancel = view.findViewById<Button>(R.id.btnDialogCancel)
//                        val btnDialogRemove = view.findViewById<Button>(R.id.btnDialogRemove)
//                        val tvDialogRemoveTitle = view.findViewById<TextView>(R.id.tvDialogRemoveTitle)
//                        val tvDialogQues = view.findViewById<TextView>(R.id.tvDialogQues)
//                        tvDialogRemoveTitle.setText("Chưa đăng nhập!")
//                        tvDialogQues.setText("Vui lòng đăng nhập tài khoản.")
//                        btnDialogRemove.setText("Đồng ý")
//
//                        //Xử lý các sự kiện của các button
//                        btnDialogCancel.setOnClickListener {
//                            dialog.dismiss()
//                        }
//                        btnDialogRemove.setOnClickListener {
//                            val intent = Intent(this.context, SignInActivity::class.java)
//                            startActivity(intent)
//                        }
//
//                        dialog = build.create()
//                        dialog.show()
//                    }
//                }
//            }
//            if (userId != "" && i.movieId != movieId) {
//                if (favoriteButton != null) {
//                    favoriteButton.setOnClickListener {
//                        val dbRef = FirebaseDatabase.getInstance().getReference("FavoriteMovies")
//                        val favoriteMoviesId = dbRef.push().key!!
//                        val favorite = FavoriteMovies(favoriteMoviesId, movieId, userId)
//                        dbRef.child(favoriteMoviesId).setValue(favorite).addOnCompleteListener {
//                            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite)
//                        }
//                    }
//                }
//            }
        }

    }

    private fun getData(callback: DatabaseCallback) {
        val dbRef3 = FirebaseDatabase.getInstance().getReference("FavoriteMovies")
        favoriteMoviesList = mutableListOf()
        dbRef3.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (favoriteMoviesSnap in snapshot.children) {
                        val favoriteMoviesData =
                            favoriteMoviesSnap.getValue(FavoriteMovies::class.java)
                        if (favoriteMoviesData != null) {
                            favoriteMoviesList.add(favoriteMoviesData)
                        }
                    }
                }
            }
            callback.onDatabaseLoaded()
        }
    }

    // hàm xử lý khi bấm nút xem thêm
    private fun showHideDescription() {
        if (expandableTextview.isExpanded) {
            expandableTextview.collapse()
            txt_toggle.setText("Xem thêm")
        } else {
            expandableTextview.expand()
            txt_toggle.setText("Ẩn bớt")
        }
    }

}