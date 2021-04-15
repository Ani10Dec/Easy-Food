package com.example.foodrunner.Fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.Adapter.RestaurantAdapter
import com.example.foodrunner.POJO.AllRestaurantPOJO
import com.example.foodrunner.R
import com.example.foodrunner.Utils.AppHelper
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class AllRestaurantFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var progressBarLayout: RelativeLayout
    lateinit var adapter: RestaurantAdapter
    val allRestaurantList = arrayListOf<AllRestaurantPOJO>()
    private val TAG: String = "AllRestaurantFragment"
    private val ratingComparator = Comparator<AllRestaurantPOJO> { item1, item2 ->
        if (item1.rating.compareTo(item2.rating) == 0) {
            item1.name.compareTo(item2.name, true)
        } else {
            item1.rating.compareTo(item2.rating, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_restaurant, container, false)
        progressBarLayout = view.findViewById(R.id.progress_bar_layout)
        recyclerView = view.findViewById(R.id.allRestaurantRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        progressBarLayout.visibility = View.VISIBLE
        setHasOptionsMenu(true)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result"

        if (AppHelper().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        progressBarLayout.visibility = View.GONE
                        Log.i(TAG, it.toString())
                        val dataObj = it.getJSONObject("data")
                        val success = dataObj.getBoolean("success")
                        if (success) {
                            val dataArray = dataObj.getJSONArray("data")
                            for (i in 0 until dataArray.length()) {
                                val itemObj = dataArray.getJSONObject(i)
                                val id = itemObj.getString("id")
                                val name = itemObj.getString("name")
                                val rating = itemObj.getString("rating")
                                val costForOne = itemObj.getString("cost_for_one")
                                val imageUrl = itemObj.getString("image_url")

                                val item = AllRestaurantPOJO(
                                    id,
                                    name,
                                    rating,
                                    costForOne,
                                    imageUrl
                                )
                                allRestaurantList.add(item)
                                adapter = RestaurantAdapter(activity as Context, allRestaurantList)
                                recyclerView.adapter = adapter
                            }
                        } else
                            Toast.makeText(
                                activity as Context,
                                "No Data to Show",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                    } catch (e: Exception) {
                        Log.e(TAG, e.localizedMessage)
                        Toast.makeText(activity as Context, e.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }, Response.ErrorListener {
                    progressBarLayout.visibility = View.GONE
                    Log.e(TAG, it.toString())
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "b887e3a84c9b09"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        inflater.inflate(R.menu.menu_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.search_bar) {
            Toast.makeText(activity as Context, "Search Clicked", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.filter) {
            Toast.makeText(
                activity as Context,
                "Rating Arranged In Decreasing Order",
                Toast.LENGTH_SHORT
            ).show()
            Collections.sort(allRestaurantList, ratingComparator)
            allRestaurantList.reverse()
        }
        return super.onOptionsItemSelected(item)
    }
}

