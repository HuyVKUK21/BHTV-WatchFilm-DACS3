package com.example.doancoso3.Activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.doancoso3.Adapter.ViewPagerAdapter
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.ListMovieChapter
import com.example.doancoso3.Fragment.FilmChapterFragment
import com.example.doancoso3.Fragment.FilmDetailsFragment
import com.example.doancoso3.Fragment.VideoPlayerFragment
import com.example.doancoso3.MainActivity
import com.example.doancoso3.R
import com.example.doancoso3.databinding.ActivityMovieDetailsBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.tabs.TabLayoutMediator

class MovieDetailsActivity()  : AppCompatActivity() {

    // khai báo biến toàn cục để gọi từ VideoPlayerFragment sang
    companion object {
        var isFullScreen = false
        var isLock = false
    }

    lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = mutableListOf<Film>()
        val pageLayout = binding.pageDemo
        val tabLayout = binding.tabDemo
        val chapter_total = binding.chapterTotal
        val filmDetailsFragment = FilmDetailsFragment("", list)
        val i = intent
        val bundle_Sent = Bundle()

        // Nhận dữ liệu từ FilmListCategory
        val movieId = intent.getStringExtra("movieId")
        val categoryId = intent.getStringExtra("categoryId")
        val movieName = intent.getStringExtra("movieName")
        val movieEpisodes = intent.getIntExtra("movieEpisodes", 0)
        val movieTime = intent.getIntExtra("movieTime", 0)
        val movieLink = intent.getStringExtra("movieLink")
        val movieDescription = intent.getStringExtra("movieDescription")
        val test = intent.getStringExtra("nameCategory")
        val userId = intent.getStringExtra("userId")
        val arrayList = intent.getSerializableExtra("myArrayList") as ArrayList<Film>
        val filmListMovie =
            intent.getSerializableExtra("filmListMovie") as ArrayList<ListMovieChapter>
        chapter_total.text = "Tổng số tập: ${filmListMovie.size.toString()} / " + movieEpisodes.toString() + " - Cập nhật đến: 23/4/2023"
        val video_player = VideoPlayerFragment("null")

        // gán giá trị cho Adapter TabLayout, ViewPager2
        val adapter = movieId?.let {
            ViewPagerAdapter(
                supportFragmentManager, lifecycle, test.toString(), arrayList,
                it, userId.toString()
            )
        }

        // truyền giá trị cho Fragment con
        bundle_Sent.putString("categoryId", categoryId)
        bundle_Sent.putString("movieId", movieId)
        bundle_Sent.putString("userId", userId)
        bundle_Sent.putString("movieName", movieName)
        bundle_Sent.putString("movieEpisodes", movieEpisodes.toString())
        bundle_Sent.putInt("movieTime", movieTime)
        bundle_Sent.putString("movieDescription", movieDescription)
        bundle_Sent.putSerializable("filmListMovie", filmListMovie)

        supportFragmentManager.beginTransaction().apply {
            filmDetailsFragment.arguments = bundle_Sent
            replace(R.id.ll_descMovie, filmDetailsFragment).commit()
        }

        supportFragmentManager.beginTransaction().apply {
            video_player.arguments = bundle_Sent
            add(R.id.video_player, video_player).commit()
        }

        val filmChapterFragment = FilmChapterFragment(movieId.toString())
        supportFragmentManager.beginTransaction().apply {
            filmChapterFragment.arguments = bundle_Sent
            replace(R.id.movie_chapter, filmChapterFragment).commit()
        }


        pageLayout.adapter = adapter
        pageLayout.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, pageLayout) { tab, pos ->
            Log.d("ukinown", pos.toString())
            when (pos) {
                0 -> {
                    tab.text = "Đề xuất cho bạn"
                }
                1 -> {
                    tab.text = "Đánh giá"
                }
            }

        }.attach()


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }
}