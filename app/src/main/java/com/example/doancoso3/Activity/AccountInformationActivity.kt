package com.example.doancoso3.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.doancoso3.MainActivity
import com.example.doancoso3.databinding.ActivityAccountInformationBinding

class AccountInformationActivity : AppCompatActivity() {
    lateinit var binding: ActivityAccountInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      Nhận dữ liệu từ MenuFragment gửi qua
        val i = intent.extras
        var userId = ""
        var userName = ""
        var email = ""
        var imgUser = ""
        if (i != null) {
            userId = i.getString("userId").toString()
            userName = i.getString("userName").toString()
            email = i.getString("email").toString()
            imgUser = i.getString("imgUser").toString()
        }
        binding.tvUserName.text = userName // thiết lập username cho textview

        Glide.with(this).load(imgUser).into(binding.imgUserImage)
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

//      xử lý sự kiện chuyển qua ChangeUserPasswordActivity (để đổi mật khẩu tài khoản)
        binding.btnChangePass.setOnClickListener {
            val intent = Intent(this, ChangeUserPasswordActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("userName", userName)
            intent.putExtra("email",email)
            intent.putExtra("imgUser", imgUser)
            startActivity(intent)
        }

//      xử lý sự kiện chuyển qua ChangeUserInformationActivity (để đổi thông tin tài khoản)
        binding.btnEditInf.setOnClickListener {
            Toast.makeText(this, email, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChangeUserInformationActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("userName", userName)
            intent.putExtra("email",email)
            intent.putExtra("imgUser",imgUser)
            startActivity(intent)
        }
    }
}