package com.einthefrog.historyquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.einthefrog.historyquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
