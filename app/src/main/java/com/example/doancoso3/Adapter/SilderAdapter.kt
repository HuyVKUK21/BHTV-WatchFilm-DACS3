package com.example.doancoso3.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.doancoso3.Data.FavoriteMovies
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Interface.SliderInterface
import com.example.doancoso3.R
import java.util.*

class SilderAdapter(val context: Context, var filmList: MutableList<Film>, var favoriteMoviesList: MutableList<FavoriteMovies>,val click: SliderInterface, val userId: String) : PagerAdapter(), ViewPager.PageTransformer {
    private var timer: Timer? = null
    private val MIN_SCALE = 0.9f // độ thu nhỏ lại 0.9
    private val MIN_ALPHA = 0.6f // độ trong suốt 0.6

    override fun getCount(): Int {
        if (filmList != null)
            return 6
        return 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = LayoutInflater.from(container.context).inflate(R.layout.item_slider, container, false)
        val imgPhoto = view.findViewById<ImageView>(R.id.imageSlider)
        val btnWatch = view.findViewById<Button>(R.id.btnWatchBySlide)
        val btnFavorite = view.findViewById<Button>(R.id.btnFavoriteBySlide)
        val tvDescribeBySlide = view.findViewById<TextView>(R.id.tvDescribeBySlide)
        if (filmList[position] != null) {
            tvDescribeBySlide.text = filmList[position].movieName
            Glide.with(container.context).load(filmList[position].movieImage).into(imgPhoto)
        }
//      kiểm tra có nếu đang đăng nhập bằng 1 tài khoản thì thiết lập nút yêu thích và xóa yêu thích
        if (userId != "") {
            for (child in favoriteMoviesList) {
                if (filmList[position].movieId == child.movieId) {
                    btnFavorite.text = "Xóa yêu thích"
                }
            }
        }

        //      Xử lý sự kiện khi click vào yêu thích
        btnFavorite.setOnClickListener {
            if (userId != "") {
                if(btnFavorite.text == "Yêu thích") {   // nếu đang hiển thị yêu thích (tức là bộ phim đó không được yêu thích) mà click thì sẽ thêm yêu thích
                    btnFavorite.text = "Xóa yêu thích"
                    Toast.makeText(this.context, position.toString(), Toast.LENGTH_SHORT).show()
                    click.addFavoriteMovies(position)
                }else {                                 // còn nếu đang hiển thị xóa yêu thích (tức là bộ phim đó đang được yêu thích) mà click thì sẽ xóa yêu thích
                    btnFavorite.text = "Yêu thích"
                    for (i in favoriteMoviesList) {
                        if (filmList[position].movieId == i.movieId) {
                            click.removeFavoriteMovies(i.favoriteMoviesId.toString())
                        }
                    }
                }
            }else {
                click.notifyAddOrRemove()
            }
        }


//      Xử lý sự kiện click vào xem ngay
        btnWatch.setOnClickListener {
            click.watchMovies(position)
        }

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
        timer?.cancel()
    }

//    Thiết lập hiệu ứng lúc chuyển slider cho ViewPager
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> page.alpha = 0f
            position <= filmList.size -> {
                val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
                page.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
            }
            else -> page.alpha = 0f
        }
    }
}