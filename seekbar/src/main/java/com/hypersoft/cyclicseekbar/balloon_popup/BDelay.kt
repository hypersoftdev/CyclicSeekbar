package com.hypersoft.cyclicseekbar.balloon_popup

import android.os.Handler
import android.os.Looper

class BDelay(
    interv: Long,
    onTickHandler: Runnable?
) {
    private val handler = Handler(Looper.getMainLooper())
    private var tickHandler: Runnable? = null
    private var delegate: Runnable? = null

    var interval: Long = interv
        private set

    init {
        setOnTickHandler(onTickHandler)
        delegate?.let { handler.postDelayed(it, interval) }
    }

    fun updateInterval(delay: Long) {
        interval = delay
        handler.removeCallbacksAndMessages(null)
        delegate?.let { handler.postDelayed(it, interval) }
    }

    fun setOnTickHandler(onTickHandler: Runnable?) {
        if (onTickHandler == null) return

        tickHandler = onTickHandler

        delegate = Runnable {
            if (tickHandler == null) return@Runnable
            handler.removeCallbacksAndMessages(null)
            tickHandler?.run()
        }
    }

    fun clear() {
        handler.removeCallbacksAndMessages(null)
    }
}