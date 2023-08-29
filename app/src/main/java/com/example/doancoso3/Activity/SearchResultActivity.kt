package com.example.doancoso3.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doancoso3.Adapter.SearchResultAdapter
import com.example.doancoso3.Adapter.SpinnerSearchTypeAdapter
import com.example.doancoso3.Data.Category
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.ListMovieChapter
import com.example.doancoso3.Data.SearchHistory
import com.example.doancoso3.Interface.DatabaseCallback
import com.example.doancoso3.Interface.RcInterface
import com.example.doancoso3.R
import com.example.doancoso3.databinding.ActivitySearchResultBinding
import com.google.firebase.database.*
import java.util.*

class SearchResultActivity : AppCompatActivity() , DatabaseCallback {
    lateinit var binding: ActivitySearchResultBinding
    lateinit var filmList: MutableList<Film>
    lateinit var filmListTemp: MutableList<Film>
    lateinit var categoryList: MutableList<Category>
    private lateinit var listAll: ArrayList<Film>
    private lateinit var dbRef: DatabaseReference
    lateinit var arrayList: ArrayList<Film>
    lateinit var filmListMovie: ArrayList<ListMovieChapter>
    private lateinit var listCategoryFilm: ArrayList<Film>
    private lateinit var nameCategory: ArrayList<Category>
    private lateinit var userId: String
    private lateinit var searchContent: String
    private val RQ_SPEECH_REC = 102
    lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      Nhận thông tin từ SearchFilmActivity chuyển qua
        listAll = arrayListOf<Film>()
        val i = intent.extras
        val filmListAll = intent.getSerializableExtra("filmListAll") as ArrayList<Film>
        listAll = filmListAll
//        val aaa = intent.getSerializableExtra("filmListAll") as ArrayList<Film>
        userId = ""
        if (i != null) {
            userId = i.getString("userId").toString()
            searchContent = i.getString("searchContent").toString()
            binding.edtSearchContent.setText(searchContent) // thiết lập nội dung vừa tìm kiếm
        }
//      Xử lý nút trở về màn hình chính
        binding.btnBackHome.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("userId",userId)
//            startActivity(intent)
            finish()
        }

//      Xử lý sự kiện tìm kiếm bằng giọng nói
        binding.btnMicroPhone.setOnClickListener {
            askSpeechInput()
        }

//      Duyệt cơ sở dữ liệu
        getDatabase(this)

    }

//  Hàm xử lý giọng nói
    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        }else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Tìm kiếm phim bằng giọng nói!")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK) {
            val result: ArrayList<String> = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
            binding.edtSearchContent.setText(result.get(0))

//          Thực hiện thêm lịch sử tìm kiếm vào firebase
            dbRef = FirebaseDatabase.getInstance().reference.child("SearchHistory")
            val historyId = dbRef.push().key!!
            val userId = userId
            val historyContent = binding.edtSearchContent.text.toString()
            val searchHistory = SearchHistory(historyId, userId, historyContent)
            dbRef.child(historyId).setValue(searchHistory)

//          sau khi đã thêm dữ liệu tìm kiếm vào firebase -> chuyển qua SearchResultActivity để hiển thị kếtquả
            val intent = Intent(this@SearchResultActivity, SearchResultActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("searchContent", binding.edtSearchContent.text.toString())
            startActivity(intent)
        }
    }

    override fun onDatabaseLoaded() {
//      Hiển thị ra kết quả tìm kiếm ra màn hình
        filmListTemp = mutableListOf() // khai báo dữ liệu rỗng cho filmListTemp
        for (child in filmList) {
            val strParent = regexText(child.movieName.toString())
            val strChild = regexText((searchContent))
            if (strParent.contains(strChild)) {
                filmListTemp.add(child)
            }
        }

//      Xử lý sự kiện tìm kiếm film
        filmListMovie = arrayListOf<ListMovieChapter>()
        arrayList = arrayListOf<Film>()
        listCategoryFilm = arrayListOf<Film>()
        nameCategory = arrayListOf<Category>()

        binding.edtSearchContent.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.edtSearchContent.text?.length ?: 0 > 0) {

//              Thực hiện thêm lịch sử tìm kiếm vào firebase
                dbRef = FirebaseDatabase.getInstance().reference.child("SearchHistory")
                val historyId = dbRef.push().key!!
                val userId = userId
                val historyContent = binding.edtSearchContent.text.toString()
                val searchHistory = SearchHistory(historyId, userId, historyContent)
                dbRef.child(historyId).setValue(searchHistory)

//              Thực hiện xử lý tương ứng ở đây
                filmListTemp = mutableListOf() // khai báo dữ liệu rỗng cho filmListTemp
                searchContent = binding.edtSearchContent.text.toString() // thiết lập nội dung tìm kiếm mới
                for (child in filmList) {
                    val strParent = regexText(child.movieName.toString())
                    val strChild = regexText((searchContent))
                    if (strParent.contains(strChild)) {
                        filmListTemp.add(child)
                    }
                }



//              Hiển thị kết quả tìm kiếm ra màn hình
              adapterSearch()
                binding.rvMoviesResult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }

//      Hiển thị kết quả tìm kiếm ra màn hình
        adapterSearch()
        binding.rvMoviesResult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

//      Xử lý lọc tìm kiếm
        binding.btnSearchFilter.setOnClickListener {
            val build = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_search_type, null)
            build.setView(view)

//          Ánh xạ đến các element trong layout_search_type
            val btnCancel = view.findViewById<Button>(R.id.btnCancel)
            val btnApply = view.findViewById<Button>(R.id.btnApply)
            val spinner = view.findViewById<Spinner>(R.id.spinner)

//          Truyền dữ liệu cho list để thông qua adapter truyền vào spinner
            var list: MutableList<String> = mutableListOf()
            list.add("Tên phim")
            list.add("Tên danh mục")

//          Thiết lập dữ liệu cho spinner
            spinner.adapter = SpinnerSearchTypeAdapter(this, list)
            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val filterSearchText = list[position]

//                  Xử lý sự kiện khi áp dụng tìm kiếm theo tùy chọn tại spinner
                    btnApply.setOnClickListener {
                        filmListTemp = mutableListOf()
                        if (filterSearchText == "Tên danh mục") {
                            for (child1 in categoryList) {
                                val strParent = regexText(child1.categoryName.toString())
                                val strChild = regexText((searchContent))
                                if (strParent.contains(strChild)) {
                                    for (child2 in filmList) {
                                        if (child1.categoryId == child2.categoryId) {
                                            filmListTemp.add(child2)
                                        }
                                    }
                                }
                            }
                        }else if (filterSearchText == "Tên phim") {
                            for (child in filmList) {
                                val strParent = regexText(child.movieName.toString())
                                val strChild = regexText((searchContent))
                                if (strParent.contains(strChild)) {
                                    filmListTemp.add(child)
                                }
                            }
                        }
                        dialog.dismiss()
                        //Hiển thị kết quả tìm kiếm ra màn hình
                        adapterSearch()
                        binding.rvMoviesResult.layoutManager = LinearLayoutManager(this@SearchResultActivity, LinearLayoutManager.VERTICAL, false)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

//          Xử lý sự kiện khi hủy tìm kiếm theo tùy chọn tại spinner
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog = build.create()
            dialog.show()
        }

    }

    private fun adapterSearch() {
        val dbRef = FirebaseDatabase.getInstance().getReference("ListOfEpisodes")
        val dbRef_1 = FirebaseDatabase.getInstance().getReference("Film")
        val dbRef_2 = FirebaseDatabase.getInstance().getReference("Category")
        binding.rvMoviesResult.adapter = SearchResultAdapter(filmListTemp, object : RcInterface {
            override fun goiSukien(pos: Int) {
                val movieId = filmListTemp[pos].movieId
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
                                for (i in listAll) {
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
                                            this@SearchResultActivity,
                                            MovieDetailsActivity::class.java
                                        )
                                        i.putExtra("movieId", filmListTemp[pos].movieId)
                                        i.putExtra(
                                            "nameCategory",
                                            nameCategory[0].categoryName.toString()
                                        )
                                        i.putExtra("categoryId", filmListTemp[pos].categoryId)
                                        i.putExtra("movieName", filmListTemp[pos].movieName)
                                        i.putExtra(
                                            "movieEpisodes",
                                            filmListTemp[pos].movieEpisodes
                                        )
                                        i.putExtra("movieTime", filmListTemp[pos].movieTime)
                                        i.putExtra(
                                            "movieDescription",
                                            filmListTemp[pos].movieDescription
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
    }

    private fun getDatabase(callback: DatabaseCallback) {
//      Duyệt dữ liệu danh mục và thêm vào categoryList
        val dbRef1 = FirebaseDatabase.getInstance().getReference("Category")
        categoryList = mutableListOf()
        dbRef1.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (categorySnap in snapshot.children) {
                        val categoryData = categorySnap.getValue(Category::class.java)
                        categoryList.add(categoryData!!)
                    }
                }
            }
            callback.onDatabaseLoaded()
        }
//      Duyệt dữ liệu film và thêm vào filmList
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

//  Hàm đổi thành chữ không dấu và về chữ thường
    fun regexText(text: String) : String {
        var text = text.toLowerCase()
        text = text.replace("á|à|ạ|ã|ả|â|ấ|ầ|ậ|ẫ|ẩ|ă|ắ|ằ|ặ|ẳ|ẵ".toRegex(), "a")
        text = text.replace("ó|ò|ọ|õ|ỏ|ô|ố|ồ|ộ|ỗ|ổ|ơ|ớ|ờ|ợ|ở|ỡ".toRegex(), "o")
        text = text.replace("é|è|ẹ|ẽ|ẻ|ê|ế|ề|ệ|ễ|ể".toRegex(), "e")
        text = text.replace("ú|ù|ụ|ũ|ủ|ư|ừ|ứ|ự|ữ|ử".toRegex(), "u")
        text = text.replace("í|ì|ị|ĩ|ỉ".toRegex(), "i")
        text = text.replace("đ".toRegex(), "d")
        return text
    }
}