package com.example.foodrunner.Activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.Adapter.MenuAdapter
import com.example.foodrunner.POJO.MenuPOJO
import com.example.foodrunner.R
import com.example.foodrunner.Utils.AppHelper

class MenuActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var stName: String
    lateinit var stID: String
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MenuAdapter
    val menuList = arrayListOf<MenuPOJO>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Getting Name and ID from Intent
        stName = intent.getStringExtra("restaurantName").toString()
        stID = intent.getStringExtra("restaurantID").toString()

        //HOOKS
        recyclerView = findViewById(R.id.menu_recyclerView)
        toolbar = findViewById(R.id.toolbar)
        recyclerView.layoutManager = LinearLayoutManager(this)
        configureToolBar(toolbar)
        if (AppHelper().checkConnectivity(this)) {
            fetchMenuItems()
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

    private fun configureToolBar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = stName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun fetchMenuItems() {
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$stID"
        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    val dataObj = it.getJSONObject("data")
                    if (dataObj.getBoolean("success")) {
                        val itemArray = dataObj.getJSONArray("data")
                        for (i in 0 until itemArray.length()) {
                            val menuObj = itemArray.getJSONObject(i)
                            val item = MenuPOJO(
                                menuObj.getString("id"),
                                menuObj.getString("name"),
                                menuObj.getString("cost_for_one"),
                                menuObj.getString("restaurant_id")
                            )
                            menuList.add(item)
                            adapter = MenuAdapter(this, menuList)
                        }
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(
                            this,
                            "No Data to Show",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {

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

}