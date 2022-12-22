package com.lazymohan.cameraxmlkit

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.lazymohan.cameraxmlkit.barcode.ActivityCamerax
import com.lazymohan.cameraxmlkit.bottom_sheet.ScanResultData
import com.lazymohan.cameraxmlkit.databinding.ActivityMainBinding

private const val RESULT_STRING_ARRAY = "resultvalues"

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private val list = mutableListOf<ScanResultData>()
  @RequiresApi(VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    val resultLauncher = registerForActivityResult(StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        if (it.data != null) {
          val result = it.data!!.getParcelableArrayListExtra<ScanResultData>("value")
          if (result != null) {
            list.addAll(result)
          }
          binding.resultView.text = list.toString()
        }
      }
    }
    binding.scanButton.setOnClickListener {
      resultLauncher.launch(Intent(this, ActivityCamerax::class.java))
    }
  }
}