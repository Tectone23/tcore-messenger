package com.example.myapplication

import RequestJSON
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class UserListActivity : AppCompatActivity() {
    var userList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        // use arrayadapter and define an array
        val arrayAdapter: ArrayAdapter<*>
//        val users = arrayOf(
//            "Virat Kohli", "Rohit Sharma", "Steve Smith",
//            "Kane Williamson", "Ross Taylor"
//        )
        try {
            val jsonObject = JSONObject()
            jsonObject.put("hook", "signal-protocol-client-cog");
            jsonObject.put("action", "get_users");
            jsonObject.put("function", "get_users");
            jsonObject.put("params", JSONArray());
            RequestJSON.instance().setURL("asgard").setMethod("POST").setData(jsonObject).send(this, this::responseApiSuccess, this::responseApiError);

        } catch (error: Exception) {
            error.printStackTrace();
        }
        var mListView = findViewById<ListView>(R.id.userlist)
        mListView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            val intent = Intent(this@UserListActivity, UserListItemActivity::class.java)
            intent.putExtra("username", selectedItem);
            startActivity(intent)
        }
    }
    private fun responseApiSuccess(response: JSONObject) {
        Log.i("request-success", response.toString());
        // access the listView from xml file
        var mListView = findViewById<ListView>(R.id.userlist)
        val userArray = response.optJSONArray("results") as JSONArray;
        for (i in 0 until userArray.length()) {
            // create a JSONObject for fetching single user data
            val userDetail = userArray.getJSONObject(i);
            this.userList?.add(userDetail.getString("username"));

        }
        val arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, userList)
        mListView.adapter = arrayAdapter
    }

    private fun responseApiError(error: Exception) {
        Log.e("request-error", error.toString());
    }
}
