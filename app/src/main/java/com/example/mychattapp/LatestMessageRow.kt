package com.example.mychattapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>(){
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.latest_message_tv_latest_message.text = chatMessage.text

        val chatterId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatterId = chatMessage.toId
        }else{
            chatterId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatterId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.username_tv_latest_message.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.imageview_latest_message
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}