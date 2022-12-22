package com.lazymohan.cameraxmlkit.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment

/**
 * Created by Mohanraj R on 21/12/22.
 */
fun Fragment.vibratePhone() {
  val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
  if (Build.VERSION.SDK_INT >= 26) {
    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
  } else {
    vibrator.vibrate(100)
  }
}