package com.example.travelapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.MessageItem
import com.example.travelapp.ui.util.SendBy

class MessageAdapter(val messageList: MutableList<MessageItem>) : RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val leftChatLayout = itemView.findViewById<LinearLayout>(R.id.left_chat_layout)
        val rightChatLayout = itemView.findViewById<LinearLayout>(R.id.right_chat_layout)
        val leftChatText = itemView.findViewById<TextView>(R.id.left_chat_text_view)
        val rightChatText = itemView.findViewById<TextView>(R.id.right_chat_text_view)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = messageList[position]
        if(currentItem.sender == SendBy.SEND_BY_USER){
            holder.rightChatLayout.visibility = View.VISIBLE
            holder.leftChatLayout.visibility = View.GONE
            holder.rightChatText.text = currentItem.message
        } else {
            holder.leftChatLayout.visibility = View.VISIBLE
            holder.rightChatLayout.visibility = View.GONE
            holder.leftChatText.text = currentItem.message
        }
    }


}