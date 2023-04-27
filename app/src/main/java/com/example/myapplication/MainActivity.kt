package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val registerButton = findViewById<Button>(R.id.registerbutton)
        preferences = getSharedPreferences("login", MODE_PRIVATE)
        if(preferences.getInt("loggedInUser",0) > 0){
            goToMainActivity()
        }
        registerButton.setOnClickListener {
            val sUserName = username.text
            val sEmail = email.text
            if(this.checkEnteredData(sUserName, sEmail)){
                if (this.loginUser(sUserName, sEmail)) {
                    this.goToMainActivity()
                }
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this@MainActivity, UserListActivity::class.java)
        Log.i("main-tag", preferences.getInt("loggedInUser",0).toString())
        intent.putExtra("loggedUserId", preferences.getInt("loggedInUser",0));
        startActivity(intent)
    }

    private fun checkEnteredData(username: Editable, email: Editable): Boolean {
        if (isEmpty(username) || isEmpty(email)) {
            val t = Toast.makeText(this, "You must enter first name to register!", Toast.LENGTH_SHORT)
            t.show()
            return false
        }
        return true
    }

    private fun loginUser(username: Editable, email: Editable): Boolean{
        if (!isEmpty(username) && !isEmpty(email)) {
            try {
                val data = JSONArray()
                data.put(username)
                data.put(email) //first in last out
                val mJsonObject = JSONObject()
                mJsonObject.put("hook", "signal-protocol-client-cog")
                mJsonObject.put("action", "login_or_register")
                mJsonObject.put("function", "login_or_register")
                mJsonObject.put("params", data)
                RequestJSON.instance().setURL("asgard").setMethod("POST").setData(mJsonObject).send(this, this::responseApiSuccess, this::responseApiError)
            } catch (error: Exception) {
                error.printStackTrace()
                return false
            }
            val t = Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT)
            t.show()
            return true
        }
        return false
    }
    private fun responseApiSuccess(response: JSONObject) {
        var newresponse = response.getJSONObject("data") as JSONObject;
        Log.i("request-success", newresponse.toString());

        var newresponse1 = JSONObject(newresponse.getString("msg"))
        Log.i("request-success", newresponse1.getString("id"));

        preferences.edit().putInt("loggedInUser", newresponse1.getInt("id")).apply()
        Log.i("request-success", response.toString())
    }

    private fun responseApiError(error: Exception) {
        Log.e("request-error", error.toString())
    }
}



