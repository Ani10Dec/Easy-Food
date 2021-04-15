package com.example.foodrunner.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val restaurant_id: Int,
    @ColumnInfo(name = "restaurant_name") val restaurantName: String,
    @ColumnInfo(name = "restaurant_rating") val restaurantRating: String,
    @ColumnInfo(name = "restaurant_cost") val restaurantCost: String,
    @ColumnInfo(name = "restaurant_img") val restaurantImg: String
)