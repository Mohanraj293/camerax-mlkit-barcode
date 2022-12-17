package com.lazymohan.cameraxmlkit

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.processing.SurfaceProcessorNode.In
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream

/**
 * Created by Mohanraj R on 17/12/22.
 */
class BarcodeAnalyser : ImageAnalysis.Analyzer {
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
    barcode.process(inputImage).addOnSuccessListener { barcodeValue ->
      barcodeValue.forEach {
        println("Barcode value -> ${it.rawValue}")
      }
      image.close()
    }.addOnFailureListener {
      println("Error Occurred ${it.message}")
    }
  }
}
