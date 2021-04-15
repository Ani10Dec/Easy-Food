package com.example.foodrunner.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query(value = "SELECT * FROM restaurants")
    fun getAllRestaurant(): List<RestaurantEntity>

    @Query(value = "SELECT * FROM restaurants WHERE restaurant_id = :restaurantID")
    fun getRestaurantByID(restaurantID: String): RestaurantEntity
}