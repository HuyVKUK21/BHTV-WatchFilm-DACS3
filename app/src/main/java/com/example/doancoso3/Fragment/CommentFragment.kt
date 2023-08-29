package com.example.doancoso3.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doancoso3.Adapter.CommentAdapter
import com.example.doancoso3.Data.Comment
import com.example.doancoso3.Data.User
import com.example.doancoso3.R
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_comment.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class CommentFragment(val movieId: String, val userId: String) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var ds: ArrayList<Comment>
        lateinit var dbRef: DatabaseReference
        var userList = arrayListOf<User>()
        val dbRef_1 = FirebaseDatabase.getInstance().getReference("User")
        val query_1 = dbRef_1.orderByChild("userId").equalTo(userId)
        val view = inflater.inflate(R.layout.fragment_comment, container, false)
        val ryc_Comment = view.findViewById<RecyclerView>(R.id.ryc_Comment)
        val comment_Content = view.findViewById<EditText>(R.id.edt_Content)
        val image_Account = view.findViewById<CircleImageView>(R.id.image_Account)
        val comment_Layout = view.findViewById<LinearLayout>(R.id.comment)
        val btn_Sent = view.findViewById<ImageView>(R.id.btn_Sent)

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = today.format(formatter)



        ryc_Comment.layoutManager = LinearLayoutManager(requireContext())
        ryc_Comment.setHasFixedSize(true)


        if (userId != "") {
            comment_Layout.visibility = View.VISIBLE
            query_1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    if(snapshot.exists()) {
                        for (userSnap in snapshot.children) {
                            val userData = userSnap.getValue(User::class.java)
                            userData?.let {
                                if(!userList.contains(it)) {
                                    userList.add(it)

                                }
                            }
                        }
                    }
                    Glide.with(requireContext()).load(userList[0].imgUser).into(image_Account)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


//
//
        }

        // xử lý sự kiện khi bấm nút gửi Comment
        dbRef = FirebaseDatabase.getInstance().getReference("Comment")
        btn_Sent.setOnClickListener {
            if(comment_Content.text.toString() != "") {
                val commentId = dbRef.push().key!!
                val comment = Comment(commentId, movieId, userId, edt_Content.text.toString(), formattedDate, 0, 0)
                dbRef.child(commentId).setValue(comment).addOnCompleteListener {
                    Toast.makeText(requireContext(), "Bình luận thành công", Toast.LENGTH_SHORT).show()
                    comment_Content.setText("")
                }
                    .addOnFailureListener { err ->
                        Toast.makeText(
                            requireContext(),
                            "Đẩy dữ liệu lên Database không thành công, mã lỗi: ${err.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            else {
                Toast.makeText(requireContext(), "Vui lòng nhập Bình luận", Toast.LENGTH_SHORT).show()
            }

        }

        // hiển thị list comment khi bình luận theo movieId
        ds = arrayListOf<Comment>()
        dbRef = FirebaseDatabase.getInstance().getReference("Comment")
        val query = dbRef.orderByChild("movieId").equalTo(movieId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ds.clear()
                if (snapshot.exists()) {
                    for (commentSnap in snapshot.children) {
                        val commentData = commentSnap.getValue(Comment::class.java)
                        commentData?.let {
                            if (!ds.contains(it)) {
                                ds.add(it)
                            }
                        }
                    }
                    val mAdapter = CommentAdapter(ds, movieId, userId)
                    ryc_Comment.adapter = mAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CommentAdapter", "Failed to read value.", error.toException())
            }
        })
        return view
    }
}