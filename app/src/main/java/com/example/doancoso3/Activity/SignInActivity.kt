package com.example.doancoso3

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.doancoso3.Activity.ForgotPasswordActivity
import com.example.doancoso3.Data.User
import com.example.doancoso3.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var dbRef: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var userList: ArrayList<User>
    private lateinit var auth : FirebaseAuth
    private lateinit var client: GoogleSignInClient
    private lateinit var googleSignInClient : GoogleSignInClient

    var userName: String? = null
    var userId: String? = null
    var imgUser: String? = null
    var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

//      Khai báo phương thức kết nối google
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.imgBack.setOnClickListener {
            checkSession()
            val i = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(i)
        }

        binding.tvSignUpAccount.setOnClickListener {
            val i = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(i)
        }

        binding.tvForgotPassword.setOnClickListener {
            val i = Intent(this@SignInActivity, ForgotPasswordActivity::class.java)
            startActivity(i)
        }

        binding.btnSignin.setOnClickListener {
            checkSession()
            checkAccount()
        }

        binding.btnSignByGoogle.setOnClickListener {
            checkSession()
            signInWithGoogle()
        }

    }



    //  Đăng nhập với google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful) {
            val account : GoogleSignInAccount? = task.result
            if(account != null) {
                updateUI(account)
            }
        }
        else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    //  Xử lý sự kiện khi đăng nhập google thành công
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful) {
                userId = account.id.toString()
                userName = account.displayName.toString()
                imgUser = account.photoUrl.toString()
                email = account.email.toString()
                val userGoogle = User(userId,userName,imgUser,email)
                dbRef.child(userId.toString()).setValue(userGoogle)
                Log.d("testidtoken", account.id.toString())
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("userId", account.id.toString())
                intent.putExtra("userName", account.displayName)
                intent.putExtra("imgUser", account.photoUrl.toString())
                Log.d("googleIntent", account.photoUrl.toString())

                startActivity(intent)
            }
            else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //  Xử lý khi cảm ứng vuốt để trở về
    override fun onBackPressed() {
        checkSession()
        super.onBackPressed()
        val i = Intent(this@SignInActivity, MainActivity::class.java)
        startActivity(i)
    }

    private fun checkSession() {
        sharedPreferences = this.getSharedPreferences("saveData", Context.MODE_PRIVATE)
        if(sharedPreferences.contains("key_userId")) {
            sharedPreferences = this.getSharedPreferences("saveData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
        }
    }

    //  Kiểm tra đăng nhập
    private fun checkAccount() {
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        val email = binding.tietEmail.text.toString()
        val password = binding.tietPassword.text.toString()

//      kiểm tra email và password lúc đăng nhập có đúng ở trong authentication
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
//              Duyệt dữ liệu trong firebase từ authentication
                val user_1 = FirebaseAuth.getInstance().currentUser // lấy thông tin người dùng thông qua FirebaseAuth.getInstance().currentUser
                val userId = user_1?.uid.toString()
                val userName = user_1?.displayName.toString()
                val imgUser = user_1?.photoUrl.toString()
                val email = user_1?.email.toString()

//              Truyền dữ liệu vừa duyệt qua ActivityMain khi đăng nhập thành công
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                intent.putExtra("userName", userName)
                intent.putExtra("imgUser", imgUser)
                intent.putExtra("userId", userId)
                intent.putExtra("email", email)
                startActivity(intent)
            }
        }
    }
}