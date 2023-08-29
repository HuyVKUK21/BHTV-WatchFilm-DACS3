package com.example.doancoso3.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.doancoso3.MainActivity
import com.example.doancoso3.databinding.ActivityViewHistoryBinding

class ViewHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityViewHistoryBinding
    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      Nhận thông tin của người dùng khi đăng nhập chuyển từ ViewHistoryActivity
        val i = intent.extras
        userId = ""
        if (i != null) {
            userId = i.getString("userId").toString()
        }

//      xử lý sự kiện trở về
        binding.imgBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

//      xử lý sự kiện trở lại trang chủ
        binding.imgHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)

        }

    }
}
