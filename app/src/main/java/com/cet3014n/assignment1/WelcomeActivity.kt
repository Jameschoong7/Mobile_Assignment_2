package com.cet3014n.assignment1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Setting the visual layout for the splash screen
        setContentView(R.layout.activity_welcome)

        //Loading the graphic (logo) for the splash screen
        val logo = findViewById<ImageView>(R.id.logo)

        //Applying animation which is fading in the logo over 2 seconds
        logo.alpha = 0f
        logo.animate().alpha(1f).setDuration(2000).start()

        //Use a timer to wait for 3 seconds before transitioning
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java)) //Transition to LoginActivity
            finish() //close the splash screen
        }
    }
}