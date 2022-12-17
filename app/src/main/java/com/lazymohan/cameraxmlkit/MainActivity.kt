package com.lazymohan.cameraxmlkit

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode.CLEAR
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.processing.SurfaceProcessorNode.In
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.lazymohan.cameraxmlkit.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var barcodeAnalyser: BarcodeAnalyser
  private var left: Int = 0
  private var right: Int = 0
  private var top: Int = 0
  private var bottom: Int = 0
  private var diameter: Int = 0
  private var offset: Int = 0
  private lateinit var canvas: Canvas

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    cameraExecutor = Executors.newSingleThreadExecutor()
    barcodeAnalyser = BarcodeAnalyser()
    canvas = Canvas()
    requestPermission()
    binding.apply {
      overlay.apply {
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(object : SurfaceHolder.Callback {
          override fun surfaceCreated(p0: SurfaceHolder) {
            drawOverLay(p0)
          }

          override fun surfaceChanged(
            p0: SurfaceHolder,
            p1: Int,
            p2: Int,
            p3: Int,
          ) {
          }

          override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
      }
    }
  }

  private fun startCamera() {
    val processCameraProvider = ProcessCameraProvider.getInstance(this)
    processCameraProvider.addListener(
      {
        val cameraProvider = processCameraProvider.get()
        val previewUseCase = buildPreviewUseCase()
        val imageAnalysis = buildImageAnalysisUseCase()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, previewUseCase,
          imageAnalysis)
      }, ContextCompat.getMainExecutor(this)
    )
  }

  private fun buildImageAnalysisUseCase(): ImageAnalysis {
    return ImageAnalysis.Builder()
      .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
      .build()
      .also {
        it.setAnalyzer(cameraExecutor, barcodeAnalyser)
      }
  }

  private fun buildPreviewUseCase(): Preview {
    return Preview.Builder()
      .build()
      .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }
  }

  private fun requestPermission() {
    requestCameraPermissionIfNeeded {
      if (it) startCamera()
      else Toast.makeText(
        this,
        "Permissions not granted by the user.",
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  private fun requestCameraPermissionIfNeeded(onResult: ((Boolean) -> Unit)) {
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    ) {
      onResult(true)
    } else {
      registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        onResult(it)
      }.launch(Manifest.permission.CAMERA)
    }
  }

  private fun drawOverLay(
    holder: SurfaceHolder,
  ) {
    val height = binding.previewView.height
    val width = binding.previewView.width

    diameter = width
    if (height < width) {
      diameter = height
    }
    offset = (0.05 * diameter).toInt()
    diameter -= offset

    canvas = holder.lockCanvas()
    canvas.drawColor(0, CLEAR)

    val paint = Paint()
    paint.style = Paint.Style.STROKE
    paint.color = Color.YELLOW
    paint.strokeWidth = 5F

    left = width / 2 - diameter / 3
    top = height / 2 - diameter / 3
    right = width / 2 + diameter / 3
    bottom = height / 2 + diameter / 3

    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
    holder.unlockCanvasAndPost(canvas)
  }
}