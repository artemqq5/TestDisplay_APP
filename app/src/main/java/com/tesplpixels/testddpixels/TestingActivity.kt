package com.tesplpixels.testddpixels

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.tesplpixels.testddpixels.databinding.ActivityTestingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.system.measureTimeMillis


class TestingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestingBinding

    private val colorList = arrayOf(
        R.color.test1,
        R.color.test2,
        R.color.test3,
        R.color.test4,
    )

    private var brokenPXALL = 0
    private var ALLPX = (80*80) * 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.testBox.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.testBox.viewTreeObserver.removeOnGlobalLayoutListener(this)
                // set state and minHeight to BottomSheet

                binding.progressBar.max = (80*80) * 4
                binding.progressBar.progress = 0

                Log.i("myLog", "${(80*80) * 4}")
                lifecycleScope.launch(Dispatchers.Main) {
                    val time = measureTimeMillis {
                        startTesting()
                    }

                    Log.i("myLog", "$time")
                    Intent(this@TestingActivity, MainActivity::class.java).apply {
                        putExtra("last", brokenPXALL)
                        putExtra("time", (String.format("%.2f", (time/1000.0)/60.0)))
                        startActivity(this)
                    }
                    finish()
                }
            }
        })

    }

    private suspend fun startTesting() {
        lifecycleScope.launch(Dispatchers.Main) {
            (colorList.indices).forEach { i ->
                val color = ContextCompat.getColor(
                    this@TestingActivity,
                    colorList[i]
                )

                binding.testBox.setBackgroundColor(
                    color
                )

                launch(Dispatchers.Default) {
                    testBrokenPixels(color)
                }.join()
            }
        }.join()
    }

    private suspend fun testBrokenPixels(colorCheck: Int) {
        var brokenPX = 0

        val randomListWidth = mutableSetOf<Int>().apply {
            while (size < 80) {

                var element = Random.nextInt(0, binding.testBox.width)
                while (element in this) {
                    element = Random.nextInt(0, binding.testBox.width)
                }

                add(element)
            }

        }
        val randomListHeight = mutableSetOf<Int>().apply {
            while (size < 80) {

                var element = Random.nextInt(0, binding.testBox.height)
                while (element in this) {
                    element = Random.nextInt(0, binding.testBox.height)
                }

                add(element)
            }

        }

        val allPX = randomListWidth.size * randomListHeight.size

        for (x in randomListWidth) {
            for (y in randomListHeight) {
                // Check if the pixel is broken
                val color = getPixelColor(binding.testBox, x, y)
                if (color != colorCheck) brokenPX++

                withContext(Dispatchers.Main) {
                    binding.progressBar.progress += 1
                }
            }
        }

        brokenPXALL += brokenPX
        Log.i("myLog", "broken=$brokenPX, all=$allPX")


    }

    private fun getPixelColor(view: View, x: Int, y: Int): Int {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap.getPixel(x, y)
    }
}