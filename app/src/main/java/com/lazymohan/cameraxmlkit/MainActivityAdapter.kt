package com.lazymohan.cameraxmlkit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lazymohan.cameraxmlkit.bottom_sheet.ScanResultData
import com.lazymohan.cameraxmlkit.databinding.LayoutMainItemBinding

/**
 * Created by Mohanraj R on 22/12/22.
 */
class MainActivityAdapter : Adapter<MainActivityAdapter.ViewHolder>() {

  private val homeLists = mutableListOf<ScanResultData>()
  private val lists = mutableListOf<ScanResultData>()

  class ViewHolder(private val binding: LayoutMainItemBinding) : RecyclerView.ViewHolder(
    binding.root) {
    fun setData(item: ScanResultData) {
      binding.textView.text = item.item
    }

    private fun checkIfMatch() {

    }
  }

  @SuppressLint("NotifyDataSetChanged")
  fun setData(homeList: List<ScanResultData>) {
    homeLists.clear()
    homeLists.addAll(homeList)
    notifyDataSetChanged()
  }

  fun setResultData(list: List<ScanResultData>) {
    lists.clear()
    lists.addAll(list)
  }

  override fun getItemCount() = homeLists.size

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int,
  ): ViewHolder {
    val binding = LayoutMainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ViewHolder(binding)
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int,
  ) {
    holder.setData(homeLists[position])
  }
}