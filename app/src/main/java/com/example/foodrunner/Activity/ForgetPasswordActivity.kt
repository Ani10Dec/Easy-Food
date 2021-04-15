package com.example.foodrunner.Activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
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
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.regex.Pattern

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var etNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnNext: Button
    private lateinit var stEmail: String
    private lateinit var tvEnterOTP: TextView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var stNumber = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        title = "Forget Password?"

        // HOOKS
        etEmail = findViewById(R.id.et_email)
        etNumber = findViewById(R.id.et_number)
        btnNext = findViewById(R.id.forget_password_btn)
        toolbar = findViewById(R.id.toolbar)
        tvEnterOTP = findViewById(R.id.tv_enter_otp)
        configureToolBar(toolbar)
        btnNext.setOnClickListener {
            stNumber = etNumber.text.toString().trim()
            stEmail = etEmail.text.toString().trim()
            if (AppHelper().checkConnectivity(this@ForgetPasswordActivity)) {
                if (checkValidation()) {
                    val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                    val queue = Volley.newRequestQueue(this)
                    val params = JSONObject()
                    params.put("mobile_number", stNumber)
                    params.put("email", stEmail)
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Method.POST, url, params, Response.Listener {
                            val dataObj = it.getJSONObject("data")
                            if (dataObj.getBoolean("success")) {
                                if (dataObj.getBoolean("first_try")) {
                                    Toast.makeText(
                                        this@ForgetPasswordActivity,
                                        "OTP sent to provided email",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else
                                    Toast.makeText(
                                        this@ForgetPasswordActivity,
                                        "OTP already sent to provided email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                            } else
                                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT)
                                    .show()
                        }, Response.ErrorListener {
                            Log.e("ForgetPasswordActivity", "Error in OTP Sent $it")
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
                val dialog = AlertDialog.Builder(this@ForgetPasswordActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(intent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(this@ForgetPasswordActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
        tvEnterOTP.setOnClickListener {
            showOtpDialog(stNumber)
        }
    }


    private fun configureToolBar(toolbar: androidx.appcompat.widget.Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forget Password!"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showOtpDialog(number: String) {
        val dialog = Dialog(this@ForgetPasswordActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.reset_password_popup)
        val btnCancel = dialog.findViewById(R.id.btn_cancel) as Button
        val btnConfirm = dialog.findViewById(R.id.btn_confirm) as Button
        val etNumber = dialog.findViewById(R.id.et_number) as TextInputEditText
        val etOtp = dialog.findViewById(R.id.et_otp) as TextInputEditText
        val etNewPassword = dialog.findViewById(R.id.et_newPassword) as TextInputEditText
        val etConfirmPassword = dialog.findViewById(R.id.et_confirmPassword) as TextInputEditText
        etNumber.setText(number)

        // Confirm Button
        btnConfirm.setOnClickListener {
            if (etNumber.text.isNullOrBlank() || etNumber.text.isNullOrEmpty() || etNumber.text.toString() == "0" || etNumber.text.toString().length < 10) {
                Toast.makeText(this@ForgetPasswordActivity, "Enter Number", Toast.LENGTH_SHORT).show()
            }
            if (etOtp.text.isNullOrEmpty() || etOtp.text.isNullOrBlank()) {
                Toast.makeText(this@ForgetPasswordActivity, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
            if (etNewPassword.text.isNullOrEmpty() || etNewPassword.text.isNullOrBlank()) {
                Toast.makeText(this@ForgetPasswordActivity, "Enter New Password", Toast.LENGTH_SHORT).show()
            }
            if (etConfirmPassword.text.isNullOrBlank() || etConfirmPassword.text.isNullOrEmpty()) {
                Toast.makeText(this@ForgetPasswordActivity, "Password Mismatch", Toast.LENGTH_SHORT).show()
            }
            if (etNewPassword.text.toString() != etConfirmPassword.text.toString())
                Toast.makeText(this@ForgetPasswordActivity, "Password Mismatch", Toast.LENGTH_SHORT).show()
            else {
                resetPassword(etOtp.text.toString(), etConfirmPassword.text.toString(), number)
                dialog.dismiss()
            }
        }
        // Cancel Button
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun resetPassword(stOTP: String, stPassword: String, stNumber: String) {
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
        val queue = Volley.newRequestQueue(this)
        val params = JSONObject()
        params.put("mobile_number", stNumber)
        params.put("password", stPassword)
        params.put("otp", stOTP)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, params, Response.Listener {
                val dataObj = it.getJSONObject("data")
                val success = dataObj.getBoolean("success")
                if (success) {
                    val successMsg = dataObj.getString("successMessage")
                    Toast.makeText(this@ForgetPasswordActivity, "$successMsg", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else
                    Toast.makeText(this@ForgetPasswordActivity, "Password not changed", Toast.LENGTH_SHORT).show()
            }, Response.ErrorListener {
                it.printStackTrace()
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

    private fun checkValidation(): Boolean {
        if (etNumber.equals("") || etNumber.length() < 10) {
            Toast.makeText(this@ForgetPasswordActivity, "Required Number", Toast.LENGTH_SHORT).show()
            return false
        }
        return if (!etEmail.equals("") || etEmail.length() != 0) {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(etEmail.text.toString())
            matcher.matches()
        } else {
            Toast.makeText(this@ForgetPasswordActivity, "Required Email", Toast.LENGTH_SHORT).show()
            false
        }

    }

}