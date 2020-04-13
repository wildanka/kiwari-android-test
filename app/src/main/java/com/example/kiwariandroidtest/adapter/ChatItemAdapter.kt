package com.example.kiwariandroidtest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kiwariandroidtest.R
import com.example.kiwariandroidtest.model.Chat

class ChatItemAdapter(private val selfUserId: String) : RecyclerView.Adapter<ChatItemAdapter.ViewHolder>() {
    companion object {
        val VIEW_TYPE_SELF = 1
        val VIEW_TYPE_OPPONENT = 2
    }

    private var chats: MutableList<Chat>? = null
    fun setupMessages(chat: MutableList<Chat>) {
        this.chats = chat
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SELF -> {
                val view = layoutInflater.inflate(R.layout.item_chat_send, parent, false)
                SentChatViewHolder(view)
            }
            else -> {
                val view = layoutInflater.inflate(R.layout.item_chat_received, parent, false)
                ReceivedChatViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return chats?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats?.get(position)
        when(chat?.senderId){
            selfUserId->{
                val sentChatViewHolder = holder as SentChatViewHolder
                sentChatViewHolder.bind(chat)
            }
            else ->{
                val receivedChatViewHolder = holder as ReceivedChatViewHolder
                chat?.let { receivedChatViewHolder.bind(it) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chat: Chat = chats?.get(position) as Chat
        return if (chat.senderId == selfUserId) {
            VIEW_TYPE_SELF
        }else{
            VIEW_TYPE_OPPONENT
        }
    }

    //region viewHolder
    open inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class SentChatViewHolder constructor(itemView: View) : ViewHolder(itemView) {
        private val tvMessages = itemView.findViewById<TextView>(R.id.tv_chat_message)
        private val tvTimestamp = itemView.findViewById<TextView>(R.id.tv_chat_sent_time)
        fun bind(chat: Chat) {
            tvMessages.text = chat.messages
            tvTimestamp.text = chat.timestamp
        }
    }

    inner class ReceivedChatViewHolder(itemView: View) : ViewHolder(itemView) {
        private val tvMessages = itemView.findViewById<TextView>(R.id.tv_chat_message)
        private val tvTimestamp = itemView.findViewById<TextView>(R.id.tv_chat_sent_time)
        fun bind(chat: Chat) {
            tvMessages.text = chat.messages
            tvTimestamp.text = chat.timestamp
        }
    }
    //endregion viewHolder
}