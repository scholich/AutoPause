package com.example.autopause

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // finding progressbar by its id
        progressBar = findViewById<ProgressBar>(R.id.progressBarTest) as ProgressBar
        progressBar!!.progress = 10
        Thread(Runnable {
            // this loop will run until the value of i becomes 99
            while (progressBar!!.progress < 100) {
                // Update the progress bar and display the current value
                progressBar!!.progress += 5
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            // reset to 5 in the end
            progressBar!!.progress = 5
        }).start()
    }
}