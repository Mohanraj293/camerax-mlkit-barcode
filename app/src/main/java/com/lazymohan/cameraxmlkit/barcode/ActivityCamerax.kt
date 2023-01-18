package com.lazymohan.cameraxmlkit.barcode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lazymohan.cameraxmlkit.R.id
import com.lazymohan.cameraxmlkit.databinding.ActivityCameraxBinding

class ActivityCamerax : AppCompatActivity() {
  private lateinit var binding: ActivityCameraxBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityCameraxBinding.inflate(layoutInflater)
    setContentView(binding.root)
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .replace(id.frame_layout, CameraxFragment.newInstance())
        .commitNow()
    }
  }
}