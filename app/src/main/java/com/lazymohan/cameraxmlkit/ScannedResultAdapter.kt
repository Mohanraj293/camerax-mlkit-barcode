package com.lazymohan.cameraxmlkit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lazymohan.cameraxmlkit.ScannedResultAdapter.ViewHolder
import com.lazymohan.cameraxmlkit.databinding.ItemBarcodeResultBinding

/**
 * Created by Mohanraj R on 21/12/22.
 */
class ScannedResultAdapter: RecyclerView.Adapter<ViewHolder>() {

  private var scanResults = mutableListOf<ScanResultData>()

  fun setScanResults(scanResultData: MutableList<ScanResultData>) {
    this.scanResults = scanResultData
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ): ViewHolder {
    return ViewHolder(ItemBarcodeResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(
    holder:ViewHolder,
    position: Int,
  ) {
    holder.setData(scanResults[position])
  }

  override fun getItemCount() = scanResults.size

  inner class ViewHolder(private val binding: ItemBarcodeResultBinding): RecyclerView.ViewHolder(binding.root) {
    fun setData(scanResultData: ScanResultData) {
      binding.tvItem.text = scanResultData.item
    }
  }
}