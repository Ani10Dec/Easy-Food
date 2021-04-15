package com.example.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.Utils.AppHelper
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var loginBtn: Button
    lateinit var userNumber: EditText
    lateinit var userPassword: EditText
    lateinit var tvForgetPass: TextView
    lateinit var tvNewSignUp: TextView
    lateinit var stNumber: String
    lateinit var stPassword: String
    lateinit var sharedPreferences: SharedPreferences
    lateinit var loginPreferences: SharedPreferences
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("credentials", Context.MODE_PRIVATE)
        loginPreferences = getSharedPreferences("isLoggedIn", Context.MODE_PRIVATE)
        val isLoggedIn = loginPreferences.getBoolean("isLoggedIn", false)
        setContentView(R.layout.activity_login)
        if (isLoggedIn) {
            val intent = Intent(this, WelcomeScreen::class.java)
            startActivity(intent)
            finish()
        }

        // HOOKS:
        loginBtn = findViewById(R.id.login_btn)
        userNumber = findViewById(R.id.et_number)
        userPassword = findViewById(R.id.et_password)
        tvForgetPass = findViewById(R.id.tv_forget_password)
        tvNewSignUp = findViewById(R.id.tv_new_signUp)
        toolbar = findViewById(R.id.toolbar)
        configureToolBar(toolbar)

        // Intent to Register Activity
        tvNewSignUp.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        // Intent to Forget Password Activity
        tvForgetPass.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        // Intent to Welcome Activity
        loginBtn.setOnClickListener {
            stNumber = userNumber.text.toString()
            stPassword = userPassword.text.toString()
            if (AppHelper().checkConnectivity(this)) {
                if (checkForValidation()) {
                    val url = "http://13.235.250.119/v2/login/fetch_result"
                    val queue = Volley.newRequestQueue(this)
                    val params = JSONObject()
                    params.put("mobile_number", stNumber)
                    params.put("password", stPassword)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, params, Response.Listener {
                            val dataObj = it.getJSONObject("data")
                            val success = dataObj.getBoolean("success")
                            if (success) {
                                val itemObj = dataObj.getJSONObject("data")
                                // Saving Data in local variables:
                                loginPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                sharedPreferences.edit()
                                    .putString("userID", itemObj.getString("user_id")).apply()
                                sharedPreferences.edit()
                                    .putString("userName", itemObj.getString("name")).apply()
                                sharedPreferences.edit()
                                    .putString("userEmail", itemObj.getString("email")).apply()
                                sharedPreferences.edit()
                                    .putString("userNumber", itemObj.getString("mobile_number"))
                                    .apply()
                                sharedPreferences.edit()
                                    .putString("userAddress", itemObj.getString("address")).apply()
                                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, WelcomeScreen::class.java)
                                startActivity(intent)
                                finish()
                            } else
                                Toast.makeText(this, "Password Incorrect!", Toast.LENGTH_SHORT)
                                    .show()

                        }, Response.ErrorListener {
                            Toast.makeText(this, "${it.toString()}", Toast.LENGTH_SHORT).show()
                        }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "b887e3a84c9b09"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                }
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }
        }
    }


    private fun configureToolBar(toolbar: androidx.appcompat.widget.Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login Screen"
    }

    override fun onResume() {
        super.onResume()
        userNumber.setText("")
        userPassword.setText("")
    }

    private fun checkForValidation(): Boolean {
        if (userNumber.equals("") || userNumber.length() < 4) {
            Toast.makeText(this, "Number Required!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (userPassword.equals("") || userPassword.length() < 4) {
            return false
        }
        return true
    }


}