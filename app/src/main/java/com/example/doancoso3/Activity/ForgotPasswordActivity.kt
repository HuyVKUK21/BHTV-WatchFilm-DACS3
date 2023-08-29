package com.example.doancoso3.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.doancoso3.SignInActivity
import com.example.doancoso3.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBackActivity.setOnClickListener {
            val i = Intent(this, SignInActivity::class.java)
            startActivity(i)
        }

        binding.tvSignin.setOnClickListener {
            val i = Intent(this, SignInActivity::class.java)
            startActivity(i)
        }

        // xử lý sự kiện nút Quên mật khẩu
        binding.btnForgotpsw.setOnClickListener {
            val email = binding.tietEmailForgotpsw.text.toString()
            if(email.isEmpty()) {
                binding.tietEmailForgotpsw.error = "Vui lòng nhập Email"
            }
            // xử lý sự kiện khi gửi link reset password thành công
            else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Gửi mật khẩu reset thành công", Toast.LENGTH_SHORT).show()
                        binding.tietEmailForgotpsw.setText("")
                    }
                }

                    .addOnFailureListener { err ->
                        Toast.makeText(this@ForgotPasswordActivity, "Có lỗi trong quá trình, ${err.message}", Toast.LENGTH_SHORT).show()
                        Log.d("err123", err.message.toString())
                    }
            }
        }
    }
}