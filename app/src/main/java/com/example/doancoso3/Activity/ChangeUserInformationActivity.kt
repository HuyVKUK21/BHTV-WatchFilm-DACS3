package com.example.doancoso3.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.doancoso3.Data.User
import com.example.doancoso3.MainActivity
import com.example.doancoso3.databinding.ActivityChangeUserInformationBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ChangeUserInformationActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangeUserInformationBinding
    lateinit var userId: String
    lateinit var userName: String
    lateinit var email: String
    lateinit var imgUser: String
    lateinit var userList: MutableList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeUserInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      Nhận dữ liệu từ AccountInformationActivity gửi qua
        val i = intent.extras
        if (i != null) {
            userId = i.getString("userId").toString()
            email = i.getString("email").toString()
            userName = i.getString("userName").toString()
            imgUser = i.getString("imgUser").toString()
        }

//      thiết lập dữ liệu cho các TextInputEditText
        binding.tietEmail.setText(email)
        binding.tietUserName.setText(userName)

//      xử lý sự kiện trở về AccountInformationActivity(quản lý tài khoản)
        binding.imgBack.setOnClickListener {
            val intent = Intent(this, AccountInformationActivity::class.java)
            intent.putExtra("userId",userId)
            intent.putExtra("userName", binding.tietUserName.text.toString())
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

//      xử lý sự kiện trở về trang chủ
        binding.imgHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

//      xử lý sự kiển đổi mật khẩu
        binding.btnComplete.setOnClickListener {
            changeInformation()
        }
    }

    private fun changeInformation() {
        val dbRef = FirebaseDatabase.getInstance().getReference("User")
        val userName = binding.tietUserName.text.toString()
        val user = User(userId, userName, imgUser, email)
        dbRef.child(userId).setValue(user)
        Toast.makeText(this, "Thay đổi thành công", Toast.LENGTH_SHORT).show()
    }
}