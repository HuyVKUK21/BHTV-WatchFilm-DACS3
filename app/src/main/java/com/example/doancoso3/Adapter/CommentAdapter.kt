package com.example.doancoso3.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doancoso3.Data.Comment
import com.example.doancoso3.Data.DataComment
import com.example.doancoso3.Data.User
import com.example.doancoso3.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.comment_layout.view.*

class CommentAdapter(private val ds: MutableList<Comment>, private val movieId: String, val userId : String) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commentList = mutableListOf<Comment>()
        var userList = arrayListOf<User>()
        val dbRef = FirebaseDatabase.getInstance().getReference("User")
        val dbRef_1 = FirebaseDatabase.getInstance().getReference("Comment")
        // Gọi lấy dữ liệu từ Database theo movieId
        val query = dbRef_1.orderByChild("movieId").equalTo(movieId)


        // xử lý sự kiện khi gọi từ Database về của bảng Comment
        query.addValueEventListener(object : ValueEventListener {
            // xử lý sự kiện khi get dữ liệu thành công
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                if (snapshot.exists()) {
                    for (commentSnap in snapshot.children) {
                        val commentData = commentSnap.getValue(Comment::class.java)
                        commentData?.let {
                            if (!commentList.contains(it)) {
                                commentList.add(it)
                            }
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CommentAdapter", "Failed to read value.", error.toException())
            }
        })

        // xử lý sự kiện khi gọi từ Database về của bảng User
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                if (snapshot.exists()) {
                    for (userSnap in snapshot.children) {
                        val userData = userSnap.getValue(User::class.java)
                        userData?.let {
                            if (!userList.contains(it)) {
                                userList.add(it)
                            }
                        }
                    }
                }

                // lặp giá trị của userList nếu trong bảng comment có userId trùng với bảng User thì hiển thị ra
                val dbRef_2 = FirebaseDatabase.getInstance()
                    .getReference("Comment/${commentList[position].commentId}")
                for (i in userList) {
                    if ("${ds[position].userId.toString()}" == i.userId.toString()) {
                        holder.itemView.apply {
                            Glide.with(this).load(i.imgUser).into(profile_comment)
                            tvName.text = i.userName.toString()
                            if (position < commentList.size) {
                                comment_Content.text =
                                    commentList[position].comment_Content.toString()
                                date_Comment.text = commentList[position].date_Comment.toString()
                                valueLike.text = commentList[position].valueLike.toString()
                                valueDislike.text = commentList[position].valueDislike.toString()
                                button_Like.setOnClickListener {
                                    val updateLike = HashMap<String, Any>()
                                    updateLike["valueLike"] = commentList[position].valueLike.toString().toInt() + 1
                                    dbRef_2.updateChildren(updateLike)
                                    Toast.makeText(this.context, "$updateLike", Toast.LENGTH_SHORT).show()
                                }


                                button_Dislike.setOnClickListener {
                                    val updateDislike = HashMap<String, Any>()
                                    updateDislike["valueDislike"] = commentList[position].valueDislike.toString().toInt() + 1
                                    dbRef_2.updateChildren(updateDislike)
                                    Toast.makeText(this.context, "OK", Toast.LENGTH_SHORT).show()
                                }

//                                valueDislike.text = commentList[position].valueDislike.toString()
//                                holder.itemView.visibility = View.VISIBLE

                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    override fun getItemCount(): Int {
        return ds.size
    }
}