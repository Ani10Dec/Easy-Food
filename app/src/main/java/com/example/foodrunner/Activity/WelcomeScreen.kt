package com.example.foodrunner.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodrunner.Fragments.*
import com.example.foodrunner.R
import com.google.android.material.navigation.NavigationView

class WelcomeScreen : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loginPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var navigationView: NavigationView
    lateinit var frameLayout: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)
        // HOOKS
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigation_view)
        frameLayout = findViewById(R.id.frame_layout)
        configureToolBar(toolbar)

        // Setting Hamburger on toolBar
        val actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        openAllRestaurantList()

        // Setting Navigation Item Clicked
        var lastMenuItem: MenuItem? = null
        navigationView.setNavigationItemSelectedListener {
            if (lastMenuItem != null) {
                lastMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            lastMenuItem = it

            when (it.itemId) {
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.dashboard -> {
                    openAllRestaurantList()
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, FavouriteRestaurantsFragment())
                        .commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, FAQFragment())
                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, HistoryFragment())
                        .commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.log_out -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Log Out")
                    dialog.setMessage("Are you sure you want to logout?")
                    dialog.setPositiveButton("Yes") { _, _ ->
                        loginPreferences = getSharedPreferences("isLoggedIn", Context.MODE_PRIVATE)
                        sharedPreferences = getSharedPreferences("credentials", Context.MODE_PRIVATE)
                        loginPreferences.edit().putBoolean("isLoggedIn", false).apply()
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this, LoginActivity::class.java)
                        Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No") { _, _ -> }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun configureToolBar(toolbar: androidx.appcompat.widget.Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Working on Hamburger Click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAllRestaurantList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, AllRestaurantFragment())
            .commit()
        navigationView.setCheckedItem(R.id.dashboard)
        supportActionBar?.title = "All Restaurants"
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frame_layout)) {
            !is AllRestaurantFragment -> openAllRestaurantList()
            else -> super.onBackPressed()
        }
    }
}