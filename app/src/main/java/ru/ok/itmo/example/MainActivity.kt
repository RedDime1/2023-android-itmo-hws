package ru.ok.itmo.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

const val START = "start_view"

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, HomeFragment(), START)
                .commit()
        }
    }
}