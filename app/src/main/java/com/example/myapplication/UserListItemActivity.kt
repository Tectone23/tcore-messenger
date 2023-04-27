package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject


class UserListItemActivity : AppCompatActivity() {
    private val chatMessages = ArrayList<MessageViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list_item)
        val intentData = intent
        val username = intentData.getStringExtra("username")
        val loggedInUserId = intentData.getIntExtra("loggedUserId", 0)

        try {
            val sData = JSONArray()
            sData.put(username)
            sData.put(loggedInUserId.toString())
            Log.i("request-data", sData.toString())
            val jsonObject = JSONObject()
            jsonObject.put("hook", "signal-protocol-client-cog")
            jsonObject.put("action", "receive_message")
            jsonObject.put("function", "receive_message")
            jsonObject.put("params", sData)
            Log.i("request-data", jsonObject.toString())
            val sendButton = findViewById<ImageButton>(R.id.sendButton)
            val chatEditText = findViewById<EditText>(R.id.chatEditText)
            sendButton.setOnClickListener {
                this.chatMessages.add(MessageViewModel(chatEditText.text.toString()))
                val data = JSONArray()
                data.put(loggedInUserId.toString())
                data.put(username) //first in last out
                data.put(chatEditText.text)
                Log.i("request-data", data.toString())

                val mJsonObject = JSONObject()
                mJsonObject.put("hook", "signal-protocol-client-cog")
                mJsonObject.put("action", "send_message")
                mJsonObject.put("function", "send_message")
                mJsonObject.put("params", data)
                Log.i("request-data", mJsonObject.toString())

                RequestJSON.instance().setURL("asgard").setMethod("POST").setData(mJsonObject).send(this, this::responseApiMessageSent, this::responseApiError)
                chatEditText.text.clear()
            }
            RequestJSON.instance().setURL("asgard").setMethod("POST").setData(jsonObject).send(this, this::responseApiSuccess, this::responseApiError)

        } catch (error: Exception) {
            error.printStackTrace()
        }
    }
    private fun responseApiMessageSent(response: JSONObject){
        Log.i("request-success", response.toString())
    }
    private fun responseApiSuccess(response: JSONObject) {
        Log.i("request-success", response.toString())
        var newresponse = response.getJSONObject("data") as JSONObject;
//        var newlist = newresponse.keys() as JSONArray
        Log.i("request-success", newresponse.toString());

        var newresponse1 = JSONObject(newresponse.getString("msg"))
        val messageList = newresponse1.optJSONArray("results") as JSONArray;

        val chatRecyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        val manager = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = manager
        chatRecyclerView.setHasFixedSize(true)
        // ArrayList of class ItemsViewModel
//        val chatMessages = ArrayList<MessageViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 0 until messageList.length()) {
            // create a JSONObject for fetching single user data
            val messageDetail = messageList.getJSONObject(i)
            this.chatMessages.add(MessageViewModel(messageDetail.getString("message")))
        }
        val chatAdapter = ChatAdapter(this.chatMessages)
        chatRecyclerView.adapter = chatAdapter

    }
    private fun responseApiError(error: Exception) {
        Log.e("request-error", error.toString())
    }
}

