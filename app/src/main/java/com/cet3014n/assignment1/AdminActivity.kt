package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Set up bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.admin_bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_menu_management -> {
                    loadFragment(MenuManagementFragment())
                    true
                }
                R.id.nav_order_management -> {
                    loadFragment(OrderManagementFragment())
                    true
                }
                R.id.nav_customer_support -> {
                    loadFragment(CustomerSupportManagementFragment())
                    true
                }
                else -> false
            }
        }

        // Load MenuManagementFragment by default
        if (savedInstanceState == null) {
            loadFragment(MenuManagementFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Clear admin session and return to login
                getSharedPreferences("AdminPrefs", MODE_PRIVATE).edit().clear().apply()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.admin_fragment_container, fragment)
            .commit()
    }
} 