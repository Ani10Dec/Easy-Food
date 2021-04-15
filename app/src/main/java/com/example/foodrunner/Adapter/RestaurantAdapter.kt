package com.example.foodrunner.Adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.Activity.MenuActivity
import com.example.foodrunner.Database.RestaurantDatabase
import com.example.foodrunner.Database.RestaurantEntity
import com.example.foodrunner.POJO.AllRestaurantPOJO
import com.example.foodrunner.R
import com.squareup.picasso.Picasso

class RestaurantAdapter(
    val context: Context,
    private val restaurantList: ArrayList<AllRestaurantPOJO>
) :
    RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_resturant_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = restaurantList[position]
        holder.restaurantName.text = item.name
        holder.restaurantCost.text = "${item.cost}/person"
        holder.restaurantRating.text = item.rating
        Picasso.get().load(item.image).error(R.drawable.app_logo).into(holder.restaurantImg)

        holder.restaurantItem.setOnClickListener {
            val int = Intent(context, MenuActivity::class.java )
            int.putExtra("restaurantName", item.name)
            int.putExtra("restaurantID", item.id)
            context.startActivity(int)
        }

        val restaurantEntity = RestaurantEntity(
            item.id.toInt(),
            item.name,
            item.rating,
            item.cost,
            item.image
        )
        if (DBRestaurantAsync(context, restaurantEntity, 1).execute().get()) {
            holder.favAddImg.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_fav_img
                )
            )
        } else {
            holder.favAddImg.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_non_fav_img
                )
            )
        }
        holder.favAddImg.setOnClickListener {
            if (!DBRestaurantAsync(context, restaurantEntity, 1).execute().get()) {
                val status = DBRestaurantAsync(context, restaurantEntity, 2).execute().get()
                if (status) {
                    Toast.makeText(context, "Added To Favourites", Toast.LENGTH_SHORT).show()
                    holder.favAddImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_fav_img
                        )
                    )
                } else {
                    holder.favAddImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_non_fav_img
                        )
                    )
                    Toast.makeText(context, "Removed From Favourites", Toast.LENGTH_SHORT).show()
                }
            } else {
                val status = DBRestaurantAsync(
                    context,
                    restaurantEntity,
                    3
                ).execute().get()
                if (status) {
                    Toast.makeText(context, "Removed From Favourites", Toast.LENGTH_SHORT).show()
                    holder.favAddImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_non_fav_img
                        )
                    )
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantItem: CardView = view.findViewById(R.id.restaurant_item)
        val restaurantName: TextView = view.findViewById(R.id.name)
        val restaurantCost: TextView = view.findViewById(R.id.cost)
        val restaurantRating: TextView = view.findViewById(R.id.rating)
        val favAddImg: ImageView = view.findViewById(R.id.add_fav_img)
        val restaurantImg: ImageView = view.findViewById(R.id.restaurant_img)
    }

    class DBRestaurantAsync(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant_db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantByID(restaurantEntity.restaurant_id.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}