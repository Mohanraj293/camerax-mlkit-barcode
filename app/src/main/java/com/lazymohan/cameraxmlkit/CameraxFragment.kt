package com.lazymohan.cameraxmlkit

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff.Mode.CLEAR
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lazymohan.cameraxmlkit.BarcodeAnalyser.ResultArray
import com.lazymohan.cameraxmlkit.databinding.BottomSheetDialogBinding
import com.lazymohan.cameraxmlkit.databinding.FragmentCameraxBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraxFragment : Fragment(), ResultArray {

  private lateinit var binding: FragmentCameraxBinding
  private lateinit var cameraExecutor: ExecutorService
  private lateinit var barcodeAnalyser: BarcodeAnalyser
  private var bottomSheetDialog: BottomSheet? = null
  private var countListener: CountListener? = null
  private var left: Int = 0
  private var right: Int = 0
  private var top: Int = 0
  private var bottom: Int = 0
  private var diameter: Int = 0
  private var offset: Int = 0
  private lateinit var canvas: Canvas
  private var results: ArrayList<ScanResultData> = arrayListOf()
  var count: Int = 0

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
    binding.tvMultiScan.text = getString(R.string.scanned_items, count)
    binding.clMultiScan.setOnClickListener {
      if (results.isNotEmpty()) {
        bottomSheetDialog = BottomSheet.newInstance()
        bottomSheetDialog?.show(parentFragmentManager,"barcode")
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
        val previewUseCase = buildPreviewUseCase()
        val imageAnalysis = buildImageAnalysisUseCase()
        val useCaseGroup = UseCaseGroup.Builder()
          .addUseCase(previewUseCase)
          .addUseCase(imageAnalysis)
          .build()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, useCaseGroup)
      }, ContextCompat.getMainExecutor(requireContext())
    )
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
        "Permissions not granted by the user.",
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
    @JvmStatic fun newInstance() = CameraxFragment()
  }

  override fun setResult(
    resultArray: ArrayList<ScanResultData>,
    count: Int,
  ) {
    requireActivity().runOnUiThread{
      bottomSheetDialog?.updateResult(resultArray)
      this.results = resultArray
      this.count = count
    }
  }
}