package com.mapbox.dash.example.relaxedmode

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.dash.example.databinding.ActivityRelaxedModeBinding

internal class RelaxedModeActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityRelaxedModeBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.close.setOnClickListener { finish() }
        setContentView(binding.root)
    }
}
