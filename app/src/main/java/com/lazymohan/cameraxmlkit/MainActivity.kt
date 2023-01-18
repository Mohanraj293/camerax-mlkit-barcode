package com.lazymohan.cameraxmlkit

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.lazymohan.cameraxmlkit.barcode.ActivityCamerax
import com.lazymohan.cameraxmlkit.bottom_sheet.ScanResultData
import com.lazymohan.cameraxmlkit.databinding.ActivityMainBinding

private const val RESULT_STRING_ARRAY = "resultvalues"

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private val list = mutableListOf<ScanResultData>()
  private val homeList = mutableListOf(
    ScanResultData("hello 1"),
    ScanResultData("hello 2"),
    ScanResultData("hello 3"),
    ScanResultData("hello 4"),
    ScanResultData("hello 5"),
    ScanResultData("hello 6"),
    ScanResultData("hello 7"),
    ScanResultData("hello 8"),
  )
  private lateinit var adapter: MainActivityAdapter

  @RequiresApi(VERSION_CODES.TIRAMISU)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    adapter = MainActivityAdapter()
    binding.rvHomeItems.adapter = adapter
    binding.rvHomeItems.layoutManager = LinearLayoutManager(this)
    val resultLauncher = registerForActivityResult(StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        if (it.data != null) {
          val result = it.data!!.getParcelableArrayListExtra<ScanResultData>(RESULT_STRING_ARRAY)
          if (result != null) {
            list.clear()
            list.addAll(result)
            adapter.setResultData(list)
          }
        }
        println("Barcode value -> $list")
      }
    }
    binding.scanButton.setOnClickListener {
      resultLauncher.launch(Intent(this, ActivityCamerax::class.java))
    }
    adapter.setData(homeList)
  }
}