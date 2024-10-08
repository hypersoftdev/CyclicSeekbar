package com.hypersoft.cseekbar

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hypersoft.cyclicseekbar.CyclicSeekbar
import com.hypersoft.cseekbar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vibrator: Vibrator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vibrator = ContextCompat.getSystemService(this, Vibrator::class.java) as Vibrator


        binding.cyclicSeekbar.setOnStateChanged(object : CyclicSeekbar.OnStateChanged {
            override fun onState(state: Int) {
                Log.d("VALUEDATA", "Value : $state")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrationEffect =
                        VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(vibrationEffect)
                } else {
                    vibrator.vibrate(100)
                }
            }
        })
    }
}