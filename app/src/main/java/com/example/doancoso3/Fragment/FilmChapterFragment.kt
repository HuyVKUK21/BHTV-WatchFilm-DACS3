package com.example.doancoso3.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancoso3.Adapter.FilmChapterAdapter
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.ListMovieChapter
import com.example.doancoso3.Interface.ChapterInterface
import com.example.doancoso3.R


class FilmChapterFragment(val movieId : String) : Fragment() {

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_film_chapter, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // nhận giá trị mảng từ MovieDetailsFragment
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        val filmListMovie = bundle?.getSerializable("filmListMovie") as? ArrayList<ListMovieChapter>
        val chapter_Movie = bundle?.getString("movieEpisodes")?.toInt() ?:0
        val list_dataActor = mutableListOf<Film>()
        val ryc_chapter = view.findViewById<RecyclerView>(R.id.ryc_Chapter)
        // lặp giá trị của chapter_Movie để lặp giá trị hiển thị tập phim
        for(i in 1..chapter_Movie) {
            list_dataActor.add(Film("", "", "", "", i, 0, ""))
        }
        val fragmentManager = activity?.supportFragmentManager
        val currentFragment = fragmentManager?.findFragmentById(R.id.container)
        // truyền giá trị filmListmovie vào FilmChapterAdapter

        val adapters = filmListMovie?.let {
            FilmChapterAdapter(list_dataActor, movieId, object : ChapterInterface {
                override fun goiSukien(pos: Int, list: MutableList<ListMovieChapter>) {
                    val Fragment1 = VideoPlayerFragment(list[pos].movieLink.toString())
                    Toast.makeText(requireContext(), "Bạn đang click vào link: " + list[pos].movieLink.toString(), Toast.LENGTH_SHORT).show()
                    fragmentManager?.beginTransaction()?.replace(R.id.video_player, Fragment1)?.commit()
                    fragmentManager?.popBackStack()
                }
            }, it)
        }

        ryc_chapter.adapter = adapters
        ryc_chapter.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
    }
}