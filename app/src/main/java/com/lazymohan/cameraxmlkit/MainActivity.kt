package com.lazymohan.cameraxmlkit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.lazymohan.cameraxmlkit.databinding.ActivityMainBinding

private const val RESULT_STRING_ARRAY = "resultvalues"

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    val resultLauncher = registerForActivityResult(StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        if (it.data != null) {
          val result = it.data!!.getStringArrayExtra(RESULT_STRING_ARRAY)
          if (result?.isNotEmpty() == true) {
            result.forEach { string ->
              binding.apply {
                resultView.text = string.toString()
              }
            }
          }
        }
      }
    }
    binding.scanButton.setOnClickListener {
      resultLauncher.launch(Intent(this, ActivityCamerax::class.java))
    }
  }
}