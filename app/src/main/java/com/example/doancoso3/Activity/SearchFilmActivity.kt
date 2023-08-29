package com.example.doancoso3.Activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doancoso3.Adapter.SearchHistoryAdapter
import com.example.doancoso3.Adapter.SearchResultAdapter
import com.example.doancoso3.Adapter.SpinnerSearchTypeAdapter
import com.example.doancoso3.Data.Film
import com.example.doancoso3.Data.SearchHistory
import com.example.doancoso3.Interface.ClickSearchHistory
import com.example.doancoso3.Interface.DatabaseCallback
import com.example.doancoso3.MainActivity
import com.example.doancoso3.R
import com.example.doancoso3.databinding.ActivitySearchFilmBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class SearchFilmActivity : AppCompatActivity(), DatabaseCallback {
    lateinit var binding: ActivitySearchFilmBinding
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var userId: String
    private lateinit var dialog: Dialog
    private lateinit var dbRef: DatabaseReference
    private lateinit var searchHistoryList: MutableList<SearchHistory>
    private val RQ_SPEECH_REC = 102
    private lateinit var userName: String
    private lateinit var imgUser: String
    private lateinit var email: String
    private lateinit var listAll: ArrayList<Film>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFilmBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      Nhận thông tin của người dùng khi đăng nhập chuyển từ MainActivity
        listAll = arrayListOf<Film>()
        val i = intent.extras
        userName = "Đăng nhập"
        imgUser = ""
        email = ""
        userId = ""
        val filmListAll = intent.getSerializableExtra("filmListAll") as ArrayList<Film>
        listAll = filmListAll
        if (i != null) {
            userId = i.getString("userId").toString()
        }

//      Xử lý sự kiện trở về trang chủ
        binding.btnBackHome.setOnClickListener {
//            testSession()
//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("userId",userId)
//            intent.putExtra("imgUser", imgUser)
//            startActivity(intent)
            finish()
        }

//      Xử lý sự kiện tìm kiếm bằng giọng nói
        binding.btnMicroPhone.setOnClickListener {
            askSpeechInput()
        }

//      Xử lý sự kiện tìm kiếm film
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
                val intent = Intent(this@SearchFilmActivity, SearchResultActivity::class.java)
                intent.putExtra("userId",userId)
                intent.putExtra("searchContent", binding.edtSearchContent.text.toString())
                intent.putExtra("filmListAll", listAll)
                startActivity(intent)
            }
        }

//      Duyệt database trong firebase
        getDatabase(this)
    }

    private fun testSession() {
        sharedPreferences = this.getSharedPreferences("saveData", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("key_userId")) {
            var userIdSP = sharedPreferences.getString("key_userId", null)
            var userNameSP = sharedPreferences.getString("key_userName", null)
            var imageUserSP = sharedPreferences.getString("key_imageUser", null)
            var emailSP = sharedPreferences.getString("key_email", null)
            if (sharedPreferences.contains("key_userId")) {
                userId = userIdSP.toString()
                userName = userNameSP.toString()
                imgUser = imageUserSP.toString()
                email = emailSP.toString()
            }
        }
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

//          sau khi đã thêm dữ liệu tìm kiếm vào firebase -> chuyển qua SearchResultActivity để hiển thị kết quả
            val intent = Intent(this@SearchFilmActivity, SearchResultActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("searchContent", binding.edtSearchContent.text.toString())
            intent.putExtra("filmListAll", listAll)
            startActivity(intent)
        }
    }

    override fun onDatabaseLoaded() {
//      Khai báo một danh sách lịch sử tìm kiếm tạm
        val searchHistoryListTemp: MutableList<SearchHistory> = mutableListOf()
        for (child1 in searchHistoryList) {
            if (child1.userId == userId) {
                searchHistoryListTemp.add(child1) // thêm dữ liệu vào danh sách đó
            }
        }
        searchHistoryListTemp.reverse()
        binding.rvSearchHistory.adapter = SearchHistoryAdapter(searchHistoryListTemp, object : ClickSearchHistory {
//          xử lý sự kiện khi click vào nút mũi tên hướng tây bắc thì thiết lập lại EditText tìm kiếm và focus
            override fun onClickNorthWest(position: Int) {
                binding.edtSearchContent.setText(searchHistoryListTemp[position].historyContent)
                binding.edtSearchContent.requestFocus() // sau khi click thì focus vào EditText
                binding.edtSearchContent.setSelection(binding.edtSearchContent.text.length) // thiết lập vị trí focus là cuối chuỗi
            }

//          xử lý sự kiện khi click vào lịch sử của tìm kiếm thì trả lại kết quả của lịch sử tìm kiếm -> chuyển qua SearchResultActivity
            override fun onClickHistory(position: Int) {
                val intent = Intent(this@SearchFilmActivity, SearchResultActivity::class.java)
                intent.putExtra("userId",userId)
                intent.putExtra("searchContent", searchHistoryListTemp[position].historyContent)
                intent.putExtra("filmListAll", listAll)
                startActivity(intent)
            }

//          xử lý sự kiện khi nhấn và giữ vào lích sử tìm kiếm thì hiện bạn có muốn xóa lịch sử tìm kiếm đó hay không
            override fun onHoldTV(position: Int) {
                val build = AlertDialog.Builder(this@SearchFilmActivity)
                val view = layoutInflater.inflate(R.layout.dialog_search_history_remove, null)
                build.setView(view)

//              Ánh xạ đến các element trong dialog_search_history_remove
                val btnDialogCancel = view.findViewById<Button>(R.id.btnDialogCancel)
                val btnDialogRemove = view.findViewById<Button>(R.id.btnDialogRemove)
                val tvDialogRemoveTitle = view.findViewById<TextView>(R.id.tvDialogRemoveTitle)
                tvDialogRemoveTitle.setText(searchHistoryListTemp[position].historyContent)

//              Xử lý các sự kiện của các button
                btnDialogCancel.setOnClickListener {
                    dialog.dismiss()
                }
                btnDialogRemove.setOnClickListener {
                    dbRef = FirebaseDatabase.getInstance().getReference("SearchHistory").child(searchHistoryListTemp[position].historyId.toString())
                    dbRef.removeValue()
                    getDatabase(this@SearchFilmActivity)
                    dialog.dismiss()
                }
                dialog = build.create()
                dialog.show()
            }
        } )
        binding.rvSearchHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun getDatabase(callback: DatabaseCallback) {
//      Duyệt dữ liệu lịch sử tìm kiếm và thêm vào searchHistoryList
        val dbRef2 = FirebaseDatabase.getInstance().getReference("SearchHistory")
        searchHistoryList = mutableListOf()
        dbRef2.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (searchHistorySnap in snapshot.children) {
                        val searchHistoryData = searchHistorySnap.getValue(SearchHistory::class.java)
                        searchHistoryList.add(searchHistoryData!!)
                    }
                }
            }
            callback.onDatabaseLoaded()
        }
    }
}