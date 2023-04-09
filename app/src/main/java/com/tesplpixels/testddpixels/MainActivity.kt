package com.tesplpixels.testddpixels

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.tesplpixels.testddpixels.databinding.ActivityMainBinding

class MainActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.extras?.let {
            binding.lastTestingText.text =
                resources.getString(R.string.last_text1) + it.getInt("last") +
                        resources.getString(R.string.last_text2) +
                        resources.getString(R.string.last_text3) + "${(80 * 80) * 4}\n"+
                        it.getString("time") +
                        resources.getString(R.string.last_text4)


        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }

        })

        binding.startTestButton.setOnClickListener {
            startActivity(Intent(this, TestingActivity::class.java))
        }

    }
}