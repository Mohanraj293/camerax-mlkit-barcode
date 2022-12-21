package com.lazymohan.cameraxmlkit.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lazymohan.cameraxmlkit.R.string
import com.lazymohan.cameraxmlkit.databinding.BottomSheetDialogBinding

/**
 * Created by Mohanraj R on 21/12/22.
 */
class ScannedResultBottomSheet : BottomSheetDialogFragment() {
  private lateinit var binding: BottomSheetDialogBinding
  private lateinit var adapter: ScannedResultAdapter
  private var results = arrayListOf<ScanResultData>()
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
    adapter.setScanResults(results)
    binding.closeButton.setOnClickListener {
      dialog?.dismiss()
    }
    binding.headerTitle.text = getString(string.scanned_items, results.size)
  }

  companion object {
    fun newInstance() = ScannedResultBottomSheet()
  }

  fun updateResult(result: ArrayList<ScanResultData>) {
    this.results = result
  }
}