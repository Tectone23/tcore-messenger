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
        if(preferences.getBoolean("loggedi",false)){
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
                data.put(email) //first in last out
                data.put(username)
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
        preferences.edit().putBoolean("loggedi", true).apply()
        Log.i("request-success", response.toString())
    }

    private fun responseApiError(error: Exception) {
        Log.e("request-error", error.toString())
    }
}



