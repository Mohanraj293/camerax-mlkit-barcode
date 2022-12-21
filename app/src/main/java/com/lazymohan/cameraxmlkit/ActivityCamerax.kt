package com.lazymohan.cameraxmlkit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lazymohan.cameraxmlkit.databinding.ActivityCameraxBinding

class ActivityCamerax : AppCompatActivity() {

  private lateinit var binding: ActivityCameraxBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityCameraxBinding.inflate(layoutInflater)
    setContentView(binding.root)
    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .replace(R.id.frame_layout, CameraxFragment.newInstance())
        .commitNow()
    }
  }
}