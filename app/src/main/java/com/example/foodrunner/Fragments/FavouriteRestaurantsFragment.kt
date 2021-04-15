package com.example.foodrunner.Fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.Adapter.FavRestAdapter
import com.example.foodrunner.Database.RestaurantDatabase
import com.example.foodrunner.Database.RestaurantEntity
import com.example.foodrunner.R


class FavouriteRestaurantsFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var progressLayout: RelativeLayout
    var dbRestaurantList = listOf<RestaurantEntity>()
    lateinit var adapter: FavRestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)
        setHasOptionsMenu(true)
        recyclerView = view.findViewById(R.id.fav_recycler_view)
        progressLayout = view.findViewById(R.id.progress_bar_layout)
        recyclerView.layoutManager = LinearLayoutManager(activity as Context)
        dbRestaurantList = GetFavRestaurantList(activity as Context).execute().get()

        if (activity != null) {
            progressLayout.visibility = View.GONE
            adapter = FavRestAdapter(activity as Context, dbRestaurantList)
            recyclerView.adapter = adapter
        } else {
            Toast.makeText(activity as Context, "Error", Toast.LENGTH_SHORT).show()
        }

        if (dbRestaurantList.isEmpty()) {
            Toast.makeText(activity as Context, "Data is Empty", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.search_bar) {
            Toast.makeText(activity as Context, "Search Clicked", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    class GetFavRestaurantList(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db =
                Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant_db").build()
            return db.restaurantDao().getAllRestaurant()
        }
    }
}