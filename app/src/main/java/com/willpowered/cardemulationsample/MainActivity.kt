package com.willpowered.cardemulationsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            /**
             * Replace the element in [R.layout.activity_main] with the id of 'sample_content_fragment'
             * with our [CardEmulationFragment].
             */
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = CardEmulationFragment()
            transaction.replace(R.id.sample_content_fragment, fragment)
            transaction.commit()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}