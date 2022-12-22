package com.lazymohan.cameraxmlkit.barcode

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode.CLEAR
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.lazymohan.cameraxmlkit.utils.CountListener
import com.lazymohan.cameraxmlkit.R.string
import com.lazymohan.cameraxmlkit.bottom_sheet.ScanResultData
import com.lazymohan.cameraxmlkit.barcode.BarcodeAnalyser.ResultArray
import com.lazymohan.cameraxmlkit.bottom_sheet.ScannedResultBottomSheet
import com.lazymohan.cameraxmlkit.databinding.FragmentCameraxBinding
import com.lazymohan.cameraxmlkit.utils.vibratePhone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraxFragment : Fragment(), ResultArray {

  private lateinit var binding: FragmentCameraxBinding
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var barcodeAnalyser: BarcodeAnalyser
  private var bottomSheetDialog: ScannedResultBottomSheet? = null
  private var countListener: CountListener? = null
  private var left: Int = 0
  private var right: Int = 0
  private var top: Int = 0
  private var bottom: Int = 0
  private var diameter: Int = 0
  private var offset: Int = 0
  private lateinit var canvas: Canvas
  private var results: ArrayList<ScanResultData> = arrayListOf()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    binding = FragmentCameraxBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      if (context is CountListener)
        countListener = context
    } catch (_: Exception) {

    }
  }

  override fun onDetach() {
    super.onDetach()
    countListener = null
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)
    cameraExecutor = Executors.newSingleThreadExecutor()
    barcodeAnalyser = BarcodeAnalyser(CoroutineScope(Dispatchers.Default), this)
    canvas = Canvas()
    requestPermission()
    binding.overlay.apply {
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
    binding.tvMultiScan.text = getString(string.scanned_items, 0)
    binding.clMultiScan.setOnClickListener {
      if (results.isNotEmpty()) {
        bottomSheetDialog = ScannedResultBottomSheet.newInstance()
        bottomSheetDialog?.updateResult(results)
        bottomSheetDialog?.show(parentFragmentManager, "barcodeBottomSheet")
      } else {
        Toast.makeText(requireContext(), "No scanned items found", Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun startCamera() {
    val processCameraProvider = ProcessCameraProvider.getInstance(requireContext())
    processCameraProvider.addListener(
      {
        val cameraProvider = processCameraProvider.get()
        val useCaseGroup = useCaseGroupBuilder()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, useCaseGroup)
      }, ContextCompat.getMainExecutor(requireContext())
    )
  }

  private fun useCaseGroupBuilder(): UseCaseGroup {
    val previewUseCase = buildPreviewUseCase()
    val imageAnalysis = buildImageAnalysisUseCase()
    return UseCaseGroup.Builder()
      .addUseCase(previewUseCase)
      .addUseCase(imageAnalysis)
      .build()
  }

  private fun buildImageAnalysisUseCase(): ImageAnalysis {
    return ImageAnalysis.Builder()
      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
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
        requireContext(),
        "Permissions not granted by the user",
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  private fun requestCameraPermissionIfNeeded(onResult: ((Boolean) -> Unit)) {
    if (ContextCompat.checkSelfPermission(requireContext(),
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
    paint.color = Color.RED
    paint.strokeWidth = 5F

    left = width / 2 - diameter / 3
    top = height / 2 - diameter / 3
    right = width / 2 + diameter / 3
    bottom = height / 2 + diameter / 3

    canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
    holder.unlockCanvasAndPost(canvas)
  }

  companion object {
    fun newInstance() = CameraxFragment()
  }

  override fun setResult(
    resultArray: ArrayList<ScanResultData>,
    count: Int,
  ) {
    requireActivity().runOnUiThread {
      binding.tvMultiScan.text = getString(string.scanned_items, count)
      vibratePhone()
      this.results = resultArray
    }
  }
}