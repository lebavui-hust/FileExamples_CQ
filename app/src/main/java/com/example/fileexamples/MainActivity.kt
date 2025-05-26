package com.example.fileexamples

import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fileexamples.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val SETTINGS = "settings"
    val BACKGROUND_COLOR = "background_color"

    lateinit var binding: ActivityMainBinding
    var color: Int = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRed.setOnClickListener {
            color = Color.RED
            binding.main.setBackgroundColor(color)
        }

        binding.buttonGreen.setOnClickListener {
            color = Color.GREEN
            binding.main.setBackgroundColor(color)
        }

        binding.buttonBlue.setOnClickListener {
            color = Color.BLUE
            binding.main.setBackgroundColor(color)
        }

        val prefs = getSharedPreferences(SETTINGS, MODE_PRIVATE)
        color = prefs.getInt(BACKGROUND_COLOR, Color.WHITE)
        binding.main.setBackgroundColor(color)

        binding.buttonReadRaw.setOnClickListener {
            val inputStream = resources.openRawResource(R.raw.test)
            val reader = inputStream.reader()
            val content = reader.readText()
            reader.close()

            Log.v("TAG", content)
        }
    }

    override fun onStop() {
        super.onStop()

        val prefs = getSharedPreferences(SETTINGS, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(BACKGROUND_COLOR, color)
        editor.apply()
    }
}