package com.example.doancoso3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.doancoso3.Data.User
import com.example.doancoso3.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var dbRef: DatabaseReference
    lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
//      Xử lý sự kiện chuyển qua form đăng nhập
        binding.tvSignUpAccount.setOnClickListener {
            val i = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(i)
        }
//      Xử lý sự kiện quay trở về form đăng nhập
        binding.imgBack.setOnClickListener {
            val i = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(i)
        }
//      Xử lý sự kiện đăng ký tài khoản bởi button btnSignUp
        binding.btnSignUp.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        val mAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        val userName = binding.tietUserName.text.toString()
        val email = binding.tietEmail.text.toString()
        val password = binding.tietPassword.text.toString()
        val repassword = binding.tietRepassword.text.toString()
//      Kiểm tra và báo lỗi lúc điền dữ liệu
        if (userName.isEmpty()) {
            binding.tietUserName.error = "Không được để trống tên đăng nhập!"
            return
        }
        if (email.isEmpty()) {
            binding.tietEmail.error = "Không được để trống Email!"
            return
        }
        if (password.isEmpty()) {
            binding.tietPassword.error = "Không được để trống mật khẩu!"
            return
        }
        if (repassword.isEmpty()) {
            binding.tietRepassword.error = "Không được để trống nhập lại mật khẩu!"
            return
        }
        if (password == repassword) {
            val image = "https://cdn.pixabay.com/photo/2021/08/02/05/17/sunflower-6515860__340.jpg"
            val imageUrl = Uri.parse(image)

//          Tạo tài khoản mới ở trong authentication
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val firebaseUser = FirebaseAuth.getInstance().currentUser
//                  Thiết lập ảnh đại diện cho người dùng lúc mới tạo tài khoản
                    val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(userName).setPhotoUri(imageUrl).build()
//                  Kiểm tra có thêm tài khoản thành công hay không
                    firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if(profileTask.isSuccessful) {
                            val userId = firebaseUser?.uid.toString()
                            val userName = firebaseUser?.displayName.toString()
                            val imgUser = firebaseUser?.photoUrl.toString()
                            val email = firebaseUser?.email.toString()
                            val user = User(userId, userName, imgUser, email)

//                          Thêm dữ liệu người dùng vào Node User trên firebase
                            dbRef.child(userId).setValue(user).addOnCompleteListener {
//                              Tạo 1 AlertDialog đăng ký thành công
                                val build = AlertDialog.Builder(this)
                                val view = layoutInflater.inflate(R.layout.dialog_register_notify, null)
                                build.setView(view)
                                val btnReturnLogin = view.findViewById<Button>(R.id.btnReturnLogin)
                                btnReturnLogin.setOnClickListener {
                                    val intent = Intent(this, SignInActivity::class.java)
                                    startActivity(intent)
                                }
                                dialog = build.create()
                                dialog.show() // hiển thì AlertDialog
                            }
                        }
                    }
//                  Xóa hết dữ liệu trên TextInputEditText
                    binding.tietUserName.text?.clear()
                    binding.tietEmail.text?.clear()
                    binding.tietPassword.text?.clear()
                    binding.tietRepassword.text?.clear()
                }
            }
//              xử lý sự kiện khi đăng ký thất bại
                .addOnFailureListener { err ->
                    Toast.makeText(this@SignUpActivity, "Không thành công, ${err.message}", Toast.LENGTH_SHORT).show()
                }
        }else {
            binding.tietRepassword.error = "Nhập lại mật khẩu chưa chính xác!"
        }
    }
}