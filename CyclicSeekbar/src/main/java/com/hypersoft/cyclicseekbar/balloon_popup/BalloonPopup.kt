package com.hypersoft.cyclicseekbar.balloon_popup

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hypersoft.cyclicseekbar.R

class BalloonPopup(
    private val ctx: Context,
    private val attachView: View,
    private var gravity: BalloonGravity,
    private val dismissOnTap: Boolean,
    private val stayWithinScreenBounds: Boolean,
    private var offsetX: Int,
    private var offsetY: Int,
    private var bgColor: Int,
    private var fgColor: Int,
    private val layoutRes: Int,
    private val customView: View?,
    private var text: String?,
    private var textSize: Int,
    private val drawable: Drawable?,
    private val balloonAnimation: BalloonAnimation,
    private var timeToLive: Int
) {
    private var popupWindow: PopupWindow? = null
    private var textView: TextView? = null
    private var bDelay: BDelay? = null
    private var hostedView: View? = null

    enum class BalloonShape {
        oval, rounded_square, little_rounded_square, square
    }

    enum class BalloonAnimation {
        pop, scale, fade, fade75, fade_and_pop, fade_and_scale, fade75_and_pop, fade75_and_scale, instantin_popout, instantin_scaleout, instantin_fadeout, instantin_fade75out, instantin_fade_and_popout, instantin_fade_and_scaleout, instantin_fade75_and_popout, instantin_fade75_and_scaleout
    }

    enum class BalloonGravity {
        alltop_allleft, alltop_halfleft, alltop_center, alltop_halfright, alltop_allright, halftop_allleft, halftop_halfleft, halftop_center, halftop_halfright, halftop_allright, center_allleft, center_halfleft, center, center_halfright, center_allright, halfbottom_allleft, halfbottom_halfleft, halfbottom_center, halfbottom_halfright, halfbottom_allright, allbottom_allleft, allbottom_halfleft, allbottom_center, allbottom_halfright, allbottom_allright
    }

    fun findScrollViewParent(v: View?): ScrollView? {
        return if (v == null) null else if (v is ScrollView) v else findScrollViewParent(
            v.parent as ViewGroup
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun show() {
        hostedView = customView
            ?: (ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                layoutRes, null
            )
        if (text != null) {
            textView = hostedView!!.findViewById<View>(R.id.text_view) as TextView
            textView!!.text = text
            textView!!.setTextColor(fgColor)
            textView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        }
        if (popupWindow == null) { // || !popupWindow.isShowing())
            popupWindow = PopupWindow(
                hostedView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            if (Build.VERSION.SDK_INT >= 21) popupWindow!!.elevation = 5.0f
            popupWindow!!.isFocusable = false
            popupWindow!!.isOutsideTouchable = false
            popupWindow!!.isTouchable = true
            popupWindow!!.isClippingEnabled = false
            if (drawable != null) {
                drawable.alpha = drawableAlpha
                popupWindow!!.setBackgroundDrawable(drawable)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable.setTint(bgColor)
                }
            }
            when (balloonAnimation) {
                BalloonAnimation.instantin_fadeout -> popupWindow!!.animationStyle =
                    R.style.instantin_fadeout

                BalloonAnimation.instantin_popout -> popupWindow!!.animationStyle =
                    R.style.instantin_popout

                BalloonAnimation.instantin_scaleout -> popupWindow!!.animationStyle =
                    R.style.instantin_scaleout

                BalloonAnimation.instantin_fade_and_popout -> popupWindow!!.animationStyle =
                    R.style.instantin_fade_and_popout

                BalloonAnimation.instantin_fade_and_scaleout -> popupWindow!!.animationStyle =
                    R.style.instantin_fade_and_scaleout

                BalloonAnimation.pop -> popupWindow!!.animationStyle = R.style.pop
                BalloonAnimation.scale -> popupWindow!!.animationStyle = R.style.scale
                BalloonAnimation.fade -> popupWindow!!.animationStyle = R.style.fade
                BalloonAnimation.fade_and_pop -> popupWindow!!.animationStyle = R.style.fade_and_pop
                BalloonAnimation.fade_and_scale -> popupWindow!!.animationStyle =
                    R.style.fade_and_scale

                BalloonAnimation.fade75 -> popupWindow!!.animationStyle = R.style.fade75
                BalloonAnimation.fade75_and_pop -> popupWindow!!.animationStyle =
                    R.style.fade75_and_pop

                BalloonAnimation.fade75_and_scale -> popupWindow!!.animationStyle =
                    R.style.fade75_and_scale

                BalloonAnimation.instantin_fade75out -> popupWindow!!.animationStyle =
                    R.style.instantin_fade75out

                BalloonAnimation.instantin_fade75_and_popout -> popupWindow!!.animationStyle =
                    R.style.instantin_fade75_and_popout

                BalloonAnimation.instantin_fade75_and_scaleout -> popupWindow!!.animationStyle =
                    R.style.instantin_fade75_and_scaleout
            }
        }
        if (timeToLive > 0) {
            if (bDelay == null) bDelay = BDelay(timeToLive.toLong()) { kill() } else {
                bDelay!!.updateInterval(timeToLive.toLong())
                bDelay!!.setOnTickHandler { kill() }
            }
        }
        if (dismissOnTap) {
            popupWindow!!.setTouchInterceptor { view, motionEvent ->
                kill()
                false
            }
        }
        draw(true)
    }

    val drawableAlpha: Int
        get() = if (balloonAnimation == BalloonAnimation.fade75 || balloonAnimation == BalloonAnimation.fade75_and_pop || balloonAnimation == BalloonAnimation.fade75_and_scale || balloonAnimation == BalloonAnimation.instantin_fade75_and_popout || balloonAnimation == BalloonAnimation.instantin_fade75_and_scaleout || balloonAnimation == BalloonAnimation.instantin_fade75out
        ) 192 else 255

    fun dismiss() {
        kill()
    }

    private fun kill() {
        try {  // window could not be attached anymore
            if (popupWindow != null) popupWindow!!.dismiss()
        } catch (ignored: Exception) {
        }
        if (bDelay != null) bDelay!!.clear()
    }

    private fun draw(restartLifeTime: Boolean) {
        // calc position and size, then show
        val loc = IntArray(2)
        attachView.getLocationOnScreen(loc)

//        attachView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        attachView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val widthAttachView = attachView.measuredWidth
        val heightAttachView = attachView.measuredHeight
        if (hostedView == null) {
            BDelay(50) { draw(restartLifeTime) }
            return
        }
        //        hostedView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        hostedView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val widthHostedView = hostedView!!.measuredWidth
        val heightHostedView = hostedView!!.measuredHeight
        var posX = loc[0] + offsetX
        when (gravity) {
            BalloonGravity.alltop_allleft, BalloonGravity.halftop_allleft, BalloonGravity.center_allleft, BalloonGravity.halfbottom_allleft, BalloonGravity.allbottom_allleft -> posX -= widthHostedView
            BalloonGravity.alltop_halfleft, BalloonGravity.halftop_halfleft, BalloonGravity.center_halfleft, BalloonGravity.halfbottom_halfleft, BalloonGravity.allbottom_halfleft -> posX -= widthHostedView / 2
            BalloonGravity.alltop_center, BalloonGravity.halftop_center, BalloonGravity.center, BalloonGravity.halfbottom_center, BalloonGravity.allbottom_center -> posX += widthAttachView / 2 - widthHostedView / 2
            BalloonGravity.alltop_halfright, BalloonGravity.halftop_halfright, BalloonGravity.center_halfright, BalloonGravity.halfbottom_halfright, BalloonGravity.allbottom_halfright -> posX += widthAttachView - widthHostedView / 2
            BalloonGravity.alltop_allright, BalloonGravity.halftop_allright, BalloonGravity.center_allright, BalloonGravity.halfbottom_allright, BalloonGravity.allbottom_allright -> posX += widthAttachView
        }
        var posY = loc[1] + offsetY
        when (gravity) {
            BalloonGravity.alltop_allleft, BalloonGravity.alltop_halfleft, BalloonGravity.alltop_center, BalloonGravity.alltop_halfright, BalloonGravity.alltop_allright -> posY -= heightHostedView
            BalloonGravity.halftop_allleft, BalloonGravity.halftop_halfleft, BalloonGravity.halftop_center, BalloonGravity.halftop_halfright, BalloonGravity.halftop_allright -> posY -= heightHostedView / 2
            BalloonGravity.center_allleft, BalloonGravity.center_halfleft, BalloonGravity.center, BalloonGravity.center_halfright, BalloonGravity.center_allright -> posY += heightAttachView / 2 - heightHostedView / 2
            BalloonGravity.halfbottom_allleft, BalloonGravity.halfbottom_halfleft, BalloonGravity.halfbottom_center, BalloonGravity.halfbottom_halfright, BalloonGravity.halfbottom_allright -> posY += heightAttachView - heightHostedView / 2
            BalloonGravity.allbottom_allleft, BalloonGravity.allbottom_halfleft, BalloonGravity.allbottom_center, BalloonGravity.allbottom_halfright, BalloonGravity.allbottom_allright -> posY += heightAttachView
        }
        if (stayWithinScreenBounds) {
            posX = Math.max(posX, 0)
            posY = Math.max(posY, 0)
            val metrics = Resources.getSystem().displayMetrics
            posX = Math.min(metrics.widthPixels - widthHostedView, posX)
            posY = Math.min(metrics.heightPixels - heightHostedView, posY)
        }
        if (restartLifeTime && popupWindow!!.isShowing) {
            popupWindow!!.update(posX, posY, popupWindow!!.width, popupWindow!!.height)
            if (bDelay != null) {
                if (timeToLive == 0) bDelay!!.clear() else bDelay!!.updateInterval(timeToLive.toLong())
            } else bDelay = BDelay(timeToLive.toLong()) { kill() }
        } else {
            attachView.addOnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
                // shows the popup when the window is ready or when the layout changes when made in the onCreate() method
                // managing the life cycle so that after a ROTATION of the screen the Builder is not run again is left to the application
                if (popupWindow!!.isShowing) draw(true)
            }
            val x = posX
            val y = posY
            attachView.post { popupWindow!!.showAtLocation(attachView, Gravity.NO_GRAVITY, x, y) }
        }
    }

    val isShowing: Boolean
        get() = if (popupWindow == null) false else popupWindow!!.isShowing

    @JvmOverloads
    fun updateOffset(newOffsetX: Int, newOffsetY: Int, restartLifeTime: Boolean = true) {
        offsetX = newOffsetX
        offsetY = newOffsetY
        draw(restartLifeTime)
    }

    @JvmOverloads
    fun updateGravity(gravity: BalloonGravity, restartLifeTime: Boolean = true) {
        this.gravity = gravity
        draw(restartLifeTime)
    }

    @JvmOverloads
    fun updateText(newText: String?, restartLifeTime: Boolean = true) {
        text = newText
        textView!!.text = text
        draw(restartLifeTime)
    }

    fun updateText(newTextRes: Int, restartLifeTime: Boolean) {
        updateText(ctx.resources.getString(newTextRes), restartLifeTime)
    }

    fun updateText(newTextRes: Int) {
        updateText(ctx.resources.getString(newTextRes), true)
    }

    @JvmOverloads
    fun updateTextSize(textSize: Int, restartLifeTime: Boolean = true) {
        this.textSize = textSize
        textView!!.textSize = textSize.toFloat()
        draw(restartLifeTime)
    }

    @JvmOverloads
    fun updateFgColor(fgColor: Int, restartLifeTime: Boolean = true) {
        this.fgColor = fgColor
        textView!!.setTextColor(fgColor)
        draw(restartLifeTime)
    }

    @JvmOverloads
    fun updateBgColor(bgColor: Int, restartLifeTime: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) if (drawable != null) {
            this.bgColor = bgColor
            drawable.setTint(bgColor)
            draw(restartLifeTime)
        }
    }

    @JvmOverloads
    fun updateLifeTimeToLive(milliseconds: Int, restartLifeTime: Boolean = true) {
        timeToLive = milliseconds
        draw(restartLifeTime)
    }

    fun restartLifeTime() {
        if (popupWindow!!.isShowing) {
            if (bDelay != null) {
                if (timeToLive == 0) bDelay!!.clear() else bDelay!!.updateInterval(timeToLive.toLong())
            } else bDelay = BDelay(timeToLive.toLong()) { kill() }
        }
    }

    fun showAgain() {
        if (popupWindow!!.isShowing) restartLifeTime() else draw(true)
    }

    class Builder internal constructor(private val ctx: Context, private val attachView: View) {
        private var gravity = BalloonGravity.halftop_halfright
        private var dismissOnTap = true
        private var stayWithinScreenBounds = true
        private var offsetX = 0
        private var offsetY = 0
        private var bgColor = Color.WHITE
        private var fgColor = Color.BLACK
        private var layoutRes = R.layout.text_balloon
        private var customView: View? = null
        private var text: String? = null
        private var textSize = 12
        private var drawable: Drawable?
        private var balloonAnimation = BalloonAnimation.pop
        private var timeToLive = 1500

        init {
            drawable = ContextCompat.getDrawable(ctx, R.drawable.bg_circle)
        }

        fun gravity(gravity: BalloonGravity): Builder {
            this.gravity = gravity
            return this
        }

        fun dismissOnTap(dismissOnTap: Boolean): Builder {
            this.dismissOnTap = dismissOnTap
            return this
        }

        fun stayWithinScreenBounds(stayWithinScreenBounds: Boolean): Builder {
            this.stayWithinScreenBounds = stayWithinScreenBounds
            return this
        }

        fun offsetX(offsetX: Int): Builder {
            this.offsetX = offsetX
            return this
        }

        fun offsetY(offsetY: Int): Builder {
            this.offsetY = offsetY
            return this
        }

        fun positionOffset(offsetX: Int, offsetY: Int): Builder {
            this.offsetX = offsetX
            this.offsetY = offsetY
            return this
        }

        fun bgColor(bgColor: Int): Builder {
            this.bgColor = bgColor
            return this
        }

        fun fgColor(fgColor: Int): Builder {
            this.fgColor = fgColor
            return this
        }

        fun layoutRes(layoutRes: Int): Builder {
            this.layoutRes = layoutRes
            return this
        }

        fun customView(customView: View?): Builder {
            this.customView = customView
            return this
        }

        fun text(text: String?): Builder {
            this.text = text
            return this
        }

        fun text(textRes: Int): Builder {
            text = ctx.resources.getString(textRes)
            return this
        }

        fun textSize(textSize: Int): Builder {
            this.textSize = textSize
            return this
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun shape(balloonShape: BalloonShape?): Builder {
            when (balloonShape) {
                BalloonShape.oval -> drawable = ContextCompat.getDrawable(ctx, R.drawable.bg_circle)
                BalloonShape.rounded_square -> drawable =
                    ContextCompat.getDrawable(ctx, R.drawable.bg_rounded_square)

                BalloonShape.little_rounded_square -> drawable =
                    ContextCompat.getDrawable(ctx, R.drawable.bg_little_rounded_square)


                BalloonShape.square -> drawable =
                    ContextCompat.getDrawable(ctx, R.drawable.bg_square)

                else -> {}
            }
            return this
        }

        fun drawable(drawable: Drawable): Builder {
            this.drawable = drawable
            return this
        }

        fun drawable(drawableRes: Int): Builder {
            drawable =   ContextCompat.getDrawable(ctx,drawableRes)
            return this
        }

        fun animation(balloonAnimation: BalloonAnimation): Builder {
            this.balloonAnimation = balloonAnimation
            return this
        }

        fun timeToLive(milliseconds: Int): Builder {
            timeToLive = milliseconds
            return this
        }

        fun show(): BalloonPopup {
            val bp = BalloonPopup(
                ctx,
                attachView,
                gravity,
                dismissOnTap,
                stayWithinScreenBounds,
                offsetX,
                offsetY,
                bgColor,
                fgColor,
                layoutRes,
                customView,
                text,
                textSize,
                drawable,
                balloonAnimation,
                timeToLive
            )
            bp.show()
            return bp
        }
    }

    companion object {
        fun Builder(ctx: Context, anchorView: View): Builder {
            return BalloonPopup.Builder(ctx, anchorView)
        }
    }
}