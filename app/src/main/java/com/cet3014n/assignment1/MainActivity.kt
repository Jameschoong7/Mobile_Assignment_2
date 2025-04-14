package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.room.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var productRepository: ProductRepository
    private lateinit var bottomNavigationView: BottomNavigationView
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

        val db = CoffeeShopDatabase.getDatabase(applicationContext)
        val productDao = db.productDao()
        productRepository = ProductRepository(productDao)
        lifecycleScope.launch {
            if (productDao.getAllProducts().isEmpty()) {
                productRepository.insertSampleProducts()
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_menu -> {
                    loadFragment(CafeMenuFragment())
                    true
                }
                R.id.rewardsFragment-> {
                    loadFragment(RewardsFragment())
                    true
                }
                R.id.navigation_account -> {
                    // Show account menu as dropdown
                    val popup = android.widget.PopupMenu(this, findViewById(R.id.navigation_account))
                    popup.menuInflater.inflate(R.menu.account_menu, popup.menu)
                    
                    popup.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_view_account -> {
                                loadFragment(AccountFragment())
                                true
                            }
                            R.id.action_view_favorites -> {
                                loadFragment(FavoriteOrdersFragment())
                                true
                            }
                            R.id.action_customer_support -> {
                                loadFragment(CustomerSupportFragment())
                                true
                            }
                            else -> false
                        }
                    }
                    popup.show()
                    false // Don't select the account item in bottom nav
                }
                else -> false
            }
        }

        // Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Handle back press to prevent exiting the app unless on HomeFragment
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is HomeFragment) {
            super.onBackPressed() // Exit the app if on HomeFragment
        } else {
            loadFragment(HomeFragment()) // Go back to HomeFragment
        }
    }
}
