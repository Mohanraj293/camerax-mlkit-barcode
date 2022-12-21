package com.lazymohan.cameraxmlkit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazymohan.cameraxmlkit.BarcodeAnalyser.ResultArray
import com.lazymohan.cameraxmlkit.databinding.BottomSheetDialogBinding

/**
 * Created by Mohanraj R on 21/12/22.
 */
class BottomSheet : BottomSheetDialogFragment(), ResultArray {
  private lateinit var binding: BottomSheetDialogBinding
  private lateinit var adapter: ScannedResultAdapter


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    binding = BottomSheetDialogBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?,
  ) {
    super.onViewCreated(view, savedInstanceState)
    adapter = ScannedResultAdapter()
    binding.recyclerView.adapter = adapter
    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    binding.closeButton.setOnClickListener {
      dialog?.dismiss()
    }
    binding.headerTitle.text = getString(R.string.scanned_items, adapter.itemCount)
  }

  companion object {
    fun newInstance() = BottomSheet()
  }

  fun updateResult(result: ArrayList<ScanResultData>) {
    adapter.setScanResults(result)
  }
  override fun setResult(
    resultArray: ArrayList<ScanResultData>,
    count: Int,
  ) {

  }
}