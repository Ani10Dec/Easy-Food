package com.example.foodrunner.Adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.Database.RestaurantDatabase
import com.example.foodrunner.Database.RestaurantEntity
import com.example.foodrunner.R
import com.squareup.picasso.Picasso

class FavRestAdapter(val context: Context, private val restaurantList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavRestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_fav_restaurant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = restaurantList[position]
        holder.name.text = item.restaurantName
        holder.cost.text = "${item.restaurantCost}/person"
        holder.rating.text = item.restaurantRating
        Picasso.get().load(item.restaurantImg).error(R.drawable.app_logo).into(holder.img)

        val restaurantEntity = RestaurantEntity(
            item.restaurant_id.toInt(),
            item.restaurantName,
            item.restaurantRating,
            item.restaurantCost,
            item.restaurantImg
        )
        holder.favImg.setOnClickListener {
            if (DBRestaurantAsync(context, restaurantEntity, 3).execute().get()) {
                Toast.makeText(context, "Removed From Favourites", Toast.LENGTH_SHORT).show()
                holder.favImg.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_non_fav_img
                    )
                )
            } else
                Toast.makeText(context, "Error in removing from favourites", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val cost: TextView = view.findViewById(R.id.cost)
        val rating: TextView = view.findViewById(R.id.rating)
        val img: ImageView = view.findViewById(R.id.restaurant_img)
        val favImg: ImageView = view.findViewById(R.id.add_fav_img)
    }

    class DBRestaurantAsync(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant_db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            if (mode == 3) {
                db.restaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                return true
            }
            return false
        }
    }
}