package com.example.foodrunner.Activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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

class RegistrationActivity : AppCompatActivity() {

    private lateinit var tvBackToLogin: TextView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var stName: String
    private lateinit var stEmail: String
    private lateinit var stNumber: String
    private lateinit var stAddress: String
    private lateinit var stPassword: String
    private lateinit var stConfirmPassword: String
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    val url = "http://13.235.250.119/v2/register/fetch_result"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registartion)
        title = "Register Yourself"

        // HOOKS
        tvBackToLogin = findViewById(R.id.tv_backToLogin)
        etName = findViewById(R.id.et_name)
        etAddress = findViewById(R.id.et_address)
        etConfirmPassword = findViewById(R.id.et_cnfrm_password)
        etPassword = findViewById(R.id.et_password)
        etEmail = findViewById(R.id.et_email)
        etNumber = findViewById(R.id.et_number)
        btnRegister = findViewById(R.id.register_btn)
        toolbar = findViewById(R.id.toolbar)
        configureToolBar(toolbar)

        btnRegister.setOnClickListener {
            stName = etName.text.toString()
            stEmail = etEmail.text.toString()
            stNumber = etNumber.text.toString()
            stAddress = etAddress.text.toString()
            stPassword = etPassword.text.toString()
            stConfirmPassword = etConfirmPassword.text.toString()
            if (AppHelper().checkConnectivity(this)) {
                if (checkForValidation()) {
                    val params = JSONObject()
                    params.put("name", stName)
                    params.put("mobile_number", stNumber)
                    params.put("password", stPassword)
                    params.put("address", stAddress)
                    params.put("email", stEmail)
                    Log.e("Params", params.toString())
                    val queue = Volley.newRequestQueue(this)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, params, Response.Listener {
                            Log.i("RegisterActivity", it.toString())
                            try {
                                val dataObj = it.getJSONObject("data")
                                val success = dataObj.getBoolean("success")
                                if (success) {
                                    val itemObj = dataObj.getJSONObject("data")
                                    val userID = itemObj.getString("user_id")
                                    val name = itemObj.getString("name")
                                    val email = itemObj.getString("email")
                                    val number = itemObj.getString("mobile_number")
                                    val address = itemObj.getString("address")

                                    Toast.makeText(
                                        this,
                                        "User Registered Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else
                                    Toast.makeText(this, "$success", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(this, "${it.networkResponse}", Toast.LENGTH_SHORT).show()
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

        // Intent to Login Activity
        tvBackToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configureToolBar(toolbar: androidx.appcompat.widget.Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Your Self"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun checkForValidation(): Boolean {
        if (etName.equals("") || etName.length() == 0 || etName.length() < 4) {
            Toast.makeText(this, "Name Required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etEmail.equals("") || etEmail.length() == 0) {
            Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etNumber.equals("") || etNumber.length() == 0 || etNumber.length() < 10) {
            Toast.makeText(this, "Number Required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etAddress.equals("") || etAddress.length() == 0) {
            Toast.makeText(this, "Address Required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!etPassword.equals("") || etPassword.length() > 4) {
            if (etConfirmPassword.text.toString() != etPassword.text.toString()) {
                Toast.makeText(this, "Password Mismatch", Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            Toast.makeText(this, "Password Required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}