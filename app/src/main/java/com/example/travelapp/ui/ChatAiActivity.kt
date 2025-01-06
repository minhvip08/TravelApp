package com.example.travelapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.MessageItem
import com.example.travelapp.ui.adapters.MessageAdapter
import com.example.travelapp.ui.util.SendBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class ChatAiActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private var messageList = mutableListOf<MessageItem>()
    private lateinit var messageAdapter: MessageAdapter
    private var client = OkHttpClient()
    private lateinit var introduceLayout: LinearLayout

    private lateinit var mActionBarToolbar: Toolbar
    private lateinit var titleToolBar: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_ai)

        setupToolBar()

        recyclerView = findViewById(R.id.chat_ai_recycler_view)
        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_message_button)

        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        introduceLayout = findViewById(R.id.introduce_layout)
        introduceLayout.visibility = LinearLayout.VISIBLE


        sendButton.setOnClickListener {
            if (messageEditText.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                introduceLayout.visibility = LinearLayout.GONE
                val message = messageEditText.text.toString().trim()
                // Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                addToChat(message)
                messageEditText.text.clear()
                callAPI(message)
            }

        }
    }

    private fun addToChat(message: String){
        lifecycleScope.launch(Dispatchers.Main){
            messageList.add(MessageItem(message, SendBy.SEND_BY_USER))
            messageAdapter.notifyItemInserted(messageList.size - 1)
            recyclerView.scrollToPosition(messageList.size - 1)
        }

    }

    fun addResponseToChat(message: String){
        lifecycleScope.launch(Dispatchers.Main){
            messageList.add(MessageItem(message, SendBy.SEND_BY_BOT))
            messageAdapter.notifyItemInserted(messageList.size - 1)
            recyclerView.scrollToPosition(messageList.size - 1)
        }
    }

    private fun callAPI(message: String){


        val jsonBody = JSONObject()
        try {
            jsonBody.put("model", "gpt-4o-mini")

                val messages = JSONArray()
            val systemMessage = JSONObject()
            systemMessage.put("role", "system")
            systemMessage.put("content", "You will be asked for travel recommendations by a tourist. You are tour guide. Answer as you were a travel guide and give no more than 3 recommendation options per answer. Just answer with the options and don't give any introduction.")
            val userMessage = JSONObject()
            userMessage.put("role", "user")
            userMessage.put("content", message)
            messages.put(systemMessage)
            messages.put(userMessage)
            jsonBody.put("messages", messages)
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0.9)
        }
        catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, "Error connect to server", Toast.LENGTH_SHORT).show()
        }

        val body =
            jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer " + getString(R.string.api_key_openai))
            .post(body)
            .build()

        Log.d("ChatAiActivity", "callAPI: $request")

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                addResponseToChat("Error connecting to server due to " + e.message)

            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response){
                if (response.isSuccessful) {
                    val responseString = response.body?.string()
                    val responseObject = responseString?.let { JSONObject(it) }

                val result = responseObject?.getJSONArray("choices")
                    ?.getJSONObject(0)?.getJSONObject("message")
                    val resultString = result?.getString("content")
                    if (resultString != null) {
                        addResponseToChat(resultString)
                    }

                }
                else{
                    addResponseToChat("Failed connecting to server due to " + response.body?.string())
                }
            }
        })

    }

    private fun setupToolBar(){
        mActionBarToolbar = findViewById(R.id.toolbar_layout)
        setSupportActionBar(mActionBarToolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleToolBar = findViewById(R.id.toolbar_title)
        titleToolBar.text = getString(R.string.chat_ai)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}