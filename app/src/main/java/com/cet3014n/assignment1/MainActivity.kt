package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.room.*

class MainActivity : AppCompatActivity() {
    companion object {
        var INSTANCE: CoffeeShopDatabase? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Initialize the Room database
        INSTANCE = Room.databaseBuilder(
            applicationContext,
            CoffeeShopDatabase::class.java,
            "coffee_shop_database"
        ).build()

        setContentView(R.layout.activity_main)



        // Find the BottomNavigationView
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        // Find the NavHostFragment and get its NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // Set up the bottom navigation with the NavController
        navView.setupWithNavController(navController)

        // Set the title of the action bar based on the current fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
        }

        // Handle intent to navigate to a specific fragment (if needed)
        val fragment = intent.getStringExtra("fragment")
        if (fragment == "cafeMenuFragment") {
            navController.navigate(R.id.cafeMenuFragment)
        }
    }

    // Handle back press to prevent exiting the app unless on HomeFragment
    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        if (navController.currentDestination?.id == R.id.homeFragment) {
            super.onBackPressed() // Exit the app if on HomeFragment
        } else {
            navController.navigate(R.id.homeFragment) // Go back to HomeFragment
        }
    }
}