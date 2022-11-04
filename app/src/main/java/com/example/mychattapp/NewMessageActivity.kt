package com.example.mychattapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class NewMessageActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"

        val recyclerview_newmessage = findViewById<RecyclerView>(R.id.recyclerview_new_message)

        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerview_newmessage.adapter = adapter

        fetchUsers()

    }

    companion object {
        val USER_KEY ="USER_KEY"
    }
    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                dataSnapshot.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener{item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)

                    //intent.putExtra(USER_KEY, userItem.user.username)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }
                val recyclerview_newmessage = findViewById<RecyclerView>(R.id.recyclerview_new_message)
                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        ref.addListenerForSingleValueEvent(postListener)


    }

    class UserItem(val user: User) : Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.findViewById<TextView>(R.id.username_tv_new_message).text = user.username
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView_new_message_row))
        }

        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }
    }
}

