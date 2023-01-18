package com.lazymohan.cameraxmlkit.utils

import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

/**
 * Created by Mohanraj R on 21/12/22.
 */
@RequiresApi(VERSION_CODES.O)
fun Fragment.vibratePhone() {
  val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
}