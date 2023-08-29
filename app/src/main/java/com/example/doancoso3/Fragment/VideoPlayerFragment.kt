package com.example.doancoso3.Fragment

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.doancoso3.Activity.MovieDetailsActivity
import com.example.doancoso3.Data.ListMovieChapter
import com.example.doancoso3.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.fragment_video_player.*

class VideoPlayerFragment(var movieLink: String) : Fragment() {
    lateinit var filmListMovie: ArrayList<ListMovieChapter>
    private lateinit var rootView: View
    lateinit var handler: Handler
    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var bt_fullscreen: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // nhận giá trị nếu chưa có tập phim thì sẽ tự động phát tập 1
        filmListMovie = arrayListOf()
        val bundle = this.arguments
        if (movieLink == "null") {
            filmListMovie = (bundle?.getSerializable("filmListMovie") as? ArrayList<ListMovieChapter>)!!
            movieLink = filmListMovie[0].movieLink.toString()
        }

        // gọi view để gọi findviewbyid
        val view = inflater.inflate(R.layout.fragment_video_player, container, false)
        rootView = inflater.inflate(R.layout.custom_controller_player, container, false)
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL

        root.addView(view)
        root.addView(rootView)
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        bt_fullscreen = view.findViewById<ImageView>(R.id.bt_fullscreen)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val bt_lockscreen = view.findViewById<ImageView>(R.id.exo_lock)
        val playerView = view.findViewById<PlayerView>(R.id.player)
        var isFullScreen = false

        // set chiều ngang khi có sự kiện fullScreen
        val parentView = requireView().parent as View
        bt_fullscreen.setOnClickListener {
            if (!MovieDetailsActivity.isFullScreen) {
                val params_1 = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )

                bt_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_fullscreen_exit_vp
                    )
                )
                requireActivity().requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                playerView.layoutParams = params
                parentView.layoutParams = params_1
            }

            // set kích cỡ chiều dọc cho VIDEO
            else {
                val dpValue = 200
                val density = resources.displayMetrics.density
                val pixelValue = (dpValue * density).toInt()

                val params_1 = LinearLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    pixelValue
                )
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    pixelValue
                )
                bt_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_fullscreen_vp
                    )
                )
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                playerView.layoutParams = params
                parentView.layoutParams = params_1
            }
            MovieDetailsActivity.isFullScreen = !MovieDetailsActivity.isFullScreen
        }

        // xử lý sự kiện khi bấm nút lock trên video
        bt_lockscreen.setOnClickListener {
            if (!MovieDetailsActivity.isLock) {
                bt_lockscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_lock_vp
                    )
                )
            } else {
                bt_lockscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_lock_open_vp
                    )
                )
            }
            MovieDetailsActivity.isLock = !MovieDetailsActivity.isLock
            lockScreen(MovieDetailsActivity.isLock)
        }

        // xử lý chỗ tua video
        simpleExoPlayer = SimpleExoPlayer.Builder(requireContext())
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()
        playerView.player = simpleExoPlayer
        playerView.keepScreenOn = true
        simpleExoPlayer.addListener(object : Player.Listener {
            // bắt sự kiện khi video đang load để visible cái progress_bar(loading)
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progress_bar.visibility = View.VISIBLE

                } else if (playbackState == Player.STATE_READY) {
                    progress_bar.visibility = View.GONE
                }

                if (!simpleExoPlayer.playWhenReady) {
                    handler.removeCallbacks(updateProgressAction)
                } else {
                    onProgress()
                }
            }
        })

        // set giá trị URL cho videoView
        val videoSource = Uri.parse(movieLink)
        val mediaItem = MediaItem.fromUri(videoSource)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
    }

    var check = false
    fun onProgress() {
        val player = simpleExoPlayer
        val position: Long = if (player == null) 0 else player.currentPosition
        handler.removeCallbacks(updateProgressAction)
        val playbackState = if (player == null) Player.STATE_IDLE else player.playbackState
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            var delayMs: Long
            if (player.playWhenReady && playbackState == Player.STATE_READY) {
                delayMs = 1000 - position % 1000
                if (delayMs < 200) {
                    delayMs += 1000
                }
            } else {
                delayMs = 1000
            }
            handler.postDelayed(updateProgressAction, delayMs)
        }

    }

    private val updateProgressAction = Runnable { onProgress() }
    private fun lockScreen(lock: Boolean) {
        val sec_mid = view?.findViewById<LinearLayout>(R.id.sec_controlvid1)
        val sec_bottom = view?.findViewById<LinearLayout>(R.id.sec_controlvid2)
        if (lock) {
            sec_mid?.visibility = View.INVISIBLE
            sec_bottom?.visibility = View.INVISIBLE
        } else {
            sec_mid?.visibility = View.VISIBLE
            sec_bottom?.visibility = View.VISIBLE
        }
    }


    override fun onStop() {
        super.onStop()
        simpleExoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.pause()
    }
}