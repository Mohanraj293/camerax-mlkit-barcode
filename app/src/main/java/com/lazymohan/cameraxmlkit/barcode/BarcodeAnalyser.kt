package com.lazymohan.cameraxmlkit.barcode

import android.annotation.SuppressLint
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.lazymohan.cameraxmlkit.bottom_sheet.ScanResultData
import com.lazymohan.cameraxmlkit.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Created by Mohanraj R on 17/12/22.
 */
class BarcodeAnalyser(
  private val coroutineScope: CoroutineScope,
  private val resultListener: ResultArray,
) : ImageAnalysis.Analyzer {
  private val options = BarcodeScannerOptions.Builder()
    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
    .build()
  private val barcode = BarcodeScanning
    .getClient(options)
  private var left: Int = 0
  private var right: Int = 0
  private var top: Int = 0
  private var bottom: Int = 0
  private var diameter: Int = 0
  private var offset: Int = 0
  private val results: ArrayList<ScanResultData> = arrayListOf()

  @SuppressLint("UnsafeOptInUsageError")
  override fun analyze(image: ImageProxy) {
    val bit = ImageUtils.convertYuv420888ImageToBitmap(image.image!!)
    val height = bit.height
    val width = bit.width
    diameter = width
    if (height < width) {
      diameter = height
    }
    offset = (0.05 * diameter).toInt()
    diameter -= offset

    left = width / 2 - diameter / 3
    top = height / 2 - diameter / 3
    right = width / 2 + diameter / 3
    bottom = height / 2 + diameter / 3
    val cropRect = Rect(left, top, right, bottom)
    val cropped = ImageUtils.rotateAndCrop(bit, image.imageInfo.rotationDegrees, cropRect)
    val inputImage = InputImage.fromBitmap(cropped, image.imageInfo.rotationDegrees)
    coroutineScope.launch {
      val resultString = barcode.process(inputImage).await()
      resultString.forEach {
        if (!results.contains(ScanResultData(it.rawValue.toString()))) {
          results.add(ScanResultData(it.rawValue.toString()))
          resultListener.setResult(results, results.size)
        }
      }
      image.close()
    }
  }

  interface ResultArray {
    fun setResult(resultArray: ArrayList<ScanResultData>, count: Int)
  }
}
