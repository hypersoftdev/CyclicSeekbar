package com.hypersoft.cyclicseekbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringConfig
import com.facebook.rebound.SpringSystem
import com.hypersoft.cyclicseekbar.balloon_popup.BalloonPopup
import com.hypersoft.cyclicseekbar.balloon_popup.BalloonPopup.BalloonAnimation

class CyclicSeekbar : View {
    // constructors
    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    // overrides
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onMeasure(widthMeasure: Int, heightMeasure: Int) {
        var widthMeasureSpec = widthMeasure
        var heightMeasureSpec = heightMeasure
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val r = Resources.getSystem()
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, r.displayMetrics)
                    .toInt()
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        }
        if (heightMode == MeasureSpec.UNSPECIFIED || heightSize == MeasureSpec.AT_MOST) {
            heightSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, r.displayMetrics)
                    .toInt()
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val width = width
        val height = height
        externalRadius = Math.min(width, height) * 0.5f
        knobRadius = externalRadius * knobRelativeRadius
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paintKnob(canvas)
        paintMarkers(canvas)
        paintIndicator(canvas)
        paintCircularIndicator(canvas)
        paintKnobCenter(canvas)
        paintKnobBorder(canvas)
        displayBalloons()
    }

    fun paintKnob(canvas: Canvas) {
        if (knobDrawableRes != 0 && knobDrawable != null) {
            knobDrawable?.setBounds(
                (centerX - knobRadius).toInt(),
                (centerY - knobRadius).toInt(),
                (centerX + knobRadius).toInt(),
                (centerY + knobRadius).toInt()
            )
            if (knobDrawableRotates) {
                canvas.save()
                canvas.rotate(-Math.toDegrees(Math.PI + currentAngle).toFloat(), centerX, centerY)
                knobDrawable?.draw(canvas)
                canvas.restore()
            } else knobDrawable?.draw(canvas)
        } else {
            paint?.color = knobColor
            paint?.style = Paint.Style.FILL
            paint?.let { canvas.drawCircle(centerX, centerY, knobRadius, it) }
        }
    }

    fun paintKnobBorder(canvas: Canvas) {
        if (borderWidth == 0) return
        paint?.color = borderColor
        paint?.style = Paint.Style.STROKE
        paint?.strokeWidth = borderWidth.toFloat()
        paint?.let { canvas.drawCircle(centerX, centerY, knobRadius, it) }
    }

    fun paintKnobCenter(canvas: Canvas) {
        if (knobDrawableRes != 0 && knobDrawable != null) return
        if (knobCenterRelativeRadius == 0f) return
        paint?.color = knobCenterColor
        paint?.style = Paint.Style.FILL
        paint?.let {
            canvas.drawCircle(centerX, centerY, knobCenterRelativeRadius * knobRadius,
                it
            )
        }
    }

    fun normalizeAngle(angleD: Double): Double {
        var angle = angleD
        while (angle < 0) angle += Math.PI * 2
        while (angle >= Math.PI * 2) angle -= Math.PI * 2
        return angle
    }

    fun calcAngle(position: Int): Double {
        val min = Math.toRadians(minAngle.toDouble())
        val max = Math.toRadians(maxAngle.toDouble() - 0.0001)
        val range = max - min

        if (numberOfStates <= 1) {
            return 0.0
        }

        var singleStepAngle = range / (numberOfStates - 1)
        if (Math.PI * 2 - range < singleStepAngle) {
            singleStepAngle = range / numberOfStates
        }

        return normalizeAngle(Math.PI - min - position * singleStepAngle)
    }

    fun setIndicatorAngleWithDirection() {
        spring?.let {spring->

            val angleCurr = normalizeAngle(spring.currentValue)
            var angleNew = calcAngle(actualState)
            if (isFreeRotation) {
                if (angleCurr > angleNew && angleCurr - angleNew > Math.PI) angleNew += Math.PI * 2 else if (angleCurr < angleNew && angleNew - angleCurr > Math.PI) angleNew -= Math.PI * 2
            }
            spring.currentValue = angleCurr
            spring.endValue = angleNew

        }

    }

    fun paintIndicator(canvas: Canvas) {
        if (indicatorWidth == 0) return
        if (indicatorRelativeLength == 0.0f) return
        paint?.color = indicatorColor
        paint?.strokeWidth = indicatorWidth.toFloat()
        val startX =
            centerX + (knobRadius * (1 - indicatorRelativeLength) * Math.sin(currentAngle)).toFloat()
        val startY =
            centerY + (knobRadius * (1 - indicatorRelativeLength) * Math.cos(currentAngle)).toFloat()
        val endX = centerX + (knobRadius * Math.sin(currentAngle)).toFloat()
        val endY = centerY + (knobRadius * Math.cos(currentAngle)).toFloat()
        paint?.let { canvas.drawLine(startX, startY, endX, endY, it) }
    }

    fun paintCircularIndicator(canvas: Canvas) {
        if (circularIndicatorRelativeRadius == 0.0f) return
        paint?.color = circularIndicatorColor
        paint?.strokeWidth = 0f
        paint?.style = Paint.Style.FILL
        val posX =
            centerX + (externalRadius * circularIndicatorRelativePosition * Math.sin(currentAngle)).toFloat()
        val posY =
            centerY + (externalRadius * circularIndicatorRelativePosition * Math.cos(currentAngle)).toFloat()
        paint?.let {
            canvas.drawCircle(posX, posY, externalRadius * circularIndicatorRelativeRadius,it)
        }

    }

    fun paintMarkers(canvas: Canvas) {
        if ((stateMarkersRelativeLength == 0f || stateMarkersWidth == 0) && (stateMarkersAccentRelativeLength == 0f || stateMarkersAccentWidth == 0)) return
        for (w in 0 until numberOfStates) {
            var big = false
            var selected = false
            if (stateMarkersAccentPeriodicity != 0) big = w % stateMarkersAccentPeriodicity == 0
            selected = w == actualState || w <= actualState && selectedStateMarkerContinuous
            paint?.strokeWidth =
                (if (big) stateMarkersAccentWidth else stateMarkersWidth).toFloat()
            val angle = calcAngle(w)
            val startX =
                centerX + (externalRadius * (1 - if (big) stateMarkersAccentRelativeLength else stateMarkersRelativeLength) * Math.sin(
                    angle
                )).toFloat()
            val startY =
                centerY + (externalRadius * (1 - if (big) stateMarkersAccentRelativeLength else stateMarkersRelativeLength) * Math.cos(
                    angle
                )).toFloat()
            val endX = centerX + (externalRadius * Math.sin(angle)).toFloat()
            val endY = centerY + (externalRadius * Math.cos(angle)).toFloat()
            paint?.color =
                if (selected) selectedStateMarkerColor else if (big) stateMarkersAccentColor else stateMarkersColor
            paint?.let { canvas.drawLine(startX, startY, endX, endY, it) }
        }
    }

    fun balloonsX(): Int {
        return (centerX + (externalRadius * balloonValuesRelativePosition * Math.sin(currentAngle)).toFloat()).toInt()
    }

    fun balloonsY(): Int {
        return (centerY + (externalRadius * balloonValuesRelativePosition * Math.cos(currentAngle)).toFloat()).toInt()
    }

    fun balloonText(): String {
        return if (balloonValuesArray == null) Integer.toString(actualState) else balloonValuesArray!![actualState].toString()
    }

    fun displayBalloons() {
        if (!isShowBalloonValues) return

        balloonPopup?.let {
            if (!it.isShowing) balloonPopup =
                ctx?.let { it1 ->
                    BalloonPopup.Builder(it1, this)
                        .text(balloonText())
                        .gravity(BalloonPopup.BalloonGravity.halftop_halfleft)
                        .offsetX(balloonsX())
                        .offsetY(balloonsY())
                        .textSize(balloonValuesTextSize.toInt())
                        .shape(BalloonPopup.BalloonShape.rounded_square)
                        .timeToLive(balloonValuesTimeToLive)
                        .animation(balloonAnimation)
                        .stayWithinScreenBounds(true)
                        .show()
                } else {
                balloonPopup?.updateOffset(balloonsX(), balloonsY(), true)
                balloonPopup?.updateText(balloonText(), true)
                balloonPopup?.updateTextSize(
                    balloonValuesTextSize.toInt(),
                    true
                )
            }
        }


    }

    val balloonAnimation: BalloonAnimation
        get() = if (balloonValuesAnimation == 0 && isBalloonValuesSlightlyTransparent) BalloonAnimation.fade75_and_pop else if (balloonValuesAnimation == 0) BalloonAnimation.fade_and_pop else if (balloonValuesAnimation == 1 && isBalloonValuesSlightlyTransparent) BalloonAnimation.fade75_and_scale else if (balloonValuesAnimation == 1) BalloonAnimation.fade_and_scale else if (balloonValuesAnimation == 2 && isBalloonValuesSlightlyTransparent) BalloonAnimation.fade75 else BalloonAnimation.fade

    // default values
    private var numberOfStates = 6
    var defaultState = 0
    private var borderWidth = 2
    private var borderColor = Color.BLACK
    private var indicatorWidth = 6
    private var indicatorColor = Color.BLACK
    private var indicatorRelativeLength = 0.35f
    private var circularIndicatorRelativeRadius = 0.0f
    private var circularIndicatorRelativePosition = 0.7f
    private var circularIndicatorColor = Color.BLACK
    private var knobColor = Color.LTGRAY
    private var knobRelativeRadius = 0.8f
    private var knobCenterRelativeRadius = 0.45f
    private var knobCenterColor = Color.DKGRAY
    private var enabled = true
    private var currentState = defaultState // can be negative and override expected limits
    private var actualState = currentState // currentState, modded to the expected limits
    var isAnimation = true
    var animationSpeed = 10f
    var animationBounciness = 40f
    private var stateMarkersWidth = 2
    private var stateMarkersColor = Color.BLACK
    private var selectedStateMarkerColor = Color.YELLOW
    private var selectedStateMarkerContinuous = false
    private var stateMarkersRelativeLength = 0.06f
    var swipeDirection = 4 // circular  (before it was horizontal)
    var swipeSensibilityPixels = 100
    private var swipeX = 0
    private var swipeY = 0 // used for swipe management
    var swipeing = false // used for swipe / click management
    var isFreeRotation = true
    private var minAngle = 0f
    private var maxAngle = 360f
    private var stateMarkersAccentWidth = 3
    private var stateMarkersAccentColor = Color.BLACK
    private var stateMarkersAccentRelativeLength = 0.11f
    private var stateMarkersAccentPeriodicity = 0 // 0 = off
    private var knobDrawableRes = 0
    private var knobDrawableRotates = true
    var isShowBalloonValues = false
    var balloonValuesTimeToLive = 400
    var balloonValuesRelativePosition = 1.3f
    var balloonValuesTextSize = 9f
    private var balloonValuesAnimation =
        BALLONANIMATION_POP
    private var balloonValuesArray: Array<CharSequence>? = null
    var isBalloonValuesSlightlyTransparent = true
    var clickBehaviour = ONCLICK_NEXT // next
    private var userRunnable: Runnable? = null

    // initialize
    fun init(attrs: AttributeSet?) {
        ctx = context
        loadAttributes(attrs)
        initTools()
        initDrawables()
        initBalloons()
        initListeners()
        initStatus()
    }

    private var paint: Paint? = null
    private var ctx: Context? = null
    private var externalRadius = 0f
    private var knobRadius = 0f
    private var centerX = 0f
    private var centerY = 0f

    var spring: Spring? = null
    private var currentAngle = 0.0
    private var previousState = defaultState
    private var knobDrawable: Drawable? = null
    private var balloonPopup: BalloonPopup? = null
    private fun initTools() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeCap = Paint.Cap.ROUND
        }

        // Ensure springSystem is initialized before use
       val springSystem = SpringSystem.create()
        spring = springSystem.createSpring().apply {
            springConfig = SpringConfig.fromBouncinessAndSpeed(animationSpeed.toDouble(), animationBounciness.toDouble())
            isOvershootClampingEnabled = false
        }
    }
    fun initDrawables() {
        if (knobDrawableRes != 0) {
            knobDrawable= ContextCompat.getDrawable(context,knobDrawableRes)
        }
    }

    fun loadAttributes(attrs: AttributeSet?) {
        if (attrs == null) return
        ctx?.let {mContext->
            val typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.Cyclic)
            numberOfStates = typedArray.getInt(R.styleable.Cyclic_cNumberOfStates, numberOfStates)
            defaultState = typedArray.getInt(R.styleable.Cyclic_cDefaultState, defaultState)
            borderWidth = typedArray.getDimensionPixelSize(R.styleable.Cyclic_cBorderWidth, borderWidth)
            borderColor = typedArray.getColor(R.styleable.Cyclic_cBorderColor, borderColor)
            indicatorWidth =
                typedArray.getDimensionPixelSize(R.styleable.Cyclic_cIndicatorWidth, indicatorWidth)
            indicatorColor = typedArray.getColor(R.styleable.Cyclic_cIndicatorColor, indicatorColor)
            indicatorRelativeLength =
                typedArray.getFloat(R.styleable.Cyclic_cIndicatorRelativeLength, indicatorRelativeLength)
            circularIndicatorRelativeRadius = typedArray.getFloat(
                R.styleable.Cyclic_cCircularIndicatorRelativeRadius,
                circularIndicatorRelativeRadius
            )
            circularIndicatorRelativePosition = typedArray.getFloat(
                R.styleable.Cyclic_cCircularIndicatorRelativePosition,
                circularIndicatorRelativePosition
            )
            circularIndicatorColor =
                typedArray.getColor(R.styleable.Cyclic_cCircularIndicatorColor, circularIndicatorColor)
            knobColor = typedArray.getColor(R.styleable.Cyclic_cKnobColor, knobColor)
            knobRelativeRadius =
                typedArray.getFloat(R.styleable.Cyclic_cKnobRelativeRadius, knobRelativeRadius)
            knobCenterRelativeRadius = typedArray.getFloat(
                R.styleable.Cyclic_cKnobCenterRelativeRadius,
                knobCenterRelativeRadius
            )
            knobCenterColor = typedArray.getColor(R.styleable.Cyclic_cKnobCenterColor, knobCenterColor)
            knobDrawableRes = typedArray.getResourceId(R.styleable.Cyclic_cKnobDrawable, knobDrawableRes)
            knobDrawableRotates =
                typedArray.getBoolean(R.styleable.Cyclic_cKnobDrawableRotates, knobDrawableRotates)
            stateMarkersWidth =
                typedArray.getDimensionPixelSize(R.styleable.Cyclic_cStateMarkersWidth, stateMarkersWidth)
            stateMarkersColor =
                typedArray.getColor(R.styleable.Cyclic_cStateMarkersColor, stateMarkersColor)
            selectedStateMarkerColor = typedArray.getColor(
                R.styleable.Cyclic_cSelectedStateMarkerColor,
                selectedStateMarkerColor
            )
            stateMarkersRelativeLength = typedArray.getFloat(
                R.styleable.Cyclic_cStateMarkersRelativeLength,
                stateMarkersRelativeLength
            )
            selectedStateMarkerContinuous = typedArray.getBoolean(
                R.styleable.Cyclic_cSelectedStateMarkerContinuous,
                selectedStateMarkerContinuous
            )
            isAnimation = typedArray.getBoolean(R.styleable.Cyclic_cAnimation, isAnimation)
            animationSpeed = typedArray.getFloat(R.styleable.Cyclic_cAnimationSpeed, animationSpeed)
            animationBounciness =
                typedArray.getFloat(R.styleable.Cyclic_cAnimationBounciness, animationBounciness)
            swipeDirection = swipeAttrToInt(typedArray.getString(R.styleable.Cyclic_cSwipe))
            swipeSensibilityPixels =
                typedArray.getInt(R.styleable.Cyclic_cSwipeSensitivityPixels, swipeSensibilityPixels)
            isFreeRotation = typedArray.getBoolean(R.styleable.Cyclic_cFreeRotation, isFreeRotation)
            minAngle = typedArray.getFloat(R.styleable.Cyclic_cMinAngle, minAngle)
            maxAngle = typedArray.getFloat(R.styleable.Cyclic_cMaxAngle, maxAngle)
            stateMarkersAccentWidth = typedArray.getDimensionPixelSize(
                R.styleable.Cyclic_cStateMarkersAccentWidth,
                stateMarkersAccentWidth
            )
            stateMarkersAccentColor =
                typedArray.getColor(R.styleable.Cyclic_cStateMarkersAccentColor, stateMarkersAccentColor)
            stateMarkersAccentRelativeLength = typedArray.getFloat(
                R.styleable.Cyclic_cStateMarkersAccentRelativeLength,
                stateMarkersAccentRelativeLength
            )
            stateMarkersAccentPeriodicity = typedArray.getInt(
                R.styleable.Cyclic_cStateMarkersAccentPeriodicity,
                stateMarkersAccentPeriodicity
            )
            isShowBalloonValues =
                typedArray.getBoolean(R.styleable.Cyclic_cShowBalloonValues, isShowBalloonValues)
            balloonValuesTimeToLive =
                typedArray.getInt(R.styleable.Cyclic_cBalloonValuesTimeToLive, balloonValuesTimeToLive)
            balloonValuesRelativePosition = typedArray.getFloat(
                R.styleable.Cyclic_cBalloonValuesRelativePosition,
                balloonValuesRelativePosition
            )
            balloonValuesTextSize =
                typedArray.getDimension(R.styleable.Cyclic_cBalloonValuesTextSize, balloonValuesTextSize)
            balloonValuesAnimation =
                balloonAnimationAttrToInt(typedArray.getString(R.styleable.Cyclic_cBalloonValuesAnimation))
            balloonValuesArray = typedArray.getTextArray(R.styleable.Cyclic_cBalloonValuesArray)
            isBalloonValuesSlightlyTransparent = typedArray.getBoolean(
                R.styleable.Cyclic_cBalloonValuesSlightlyTransparent,
                isBalloonValuesSlightlyTransparent
            )
            clickBehaviour = clickAttrToInt(typedArray.getString(R.styleable.Cyclic_cClickBehaviour))
            enabled = typedArray.getBoolean(R.styleable.Cyclic_cEnabled, enabled)
            typedArray.recycle()
        }


    }

    fun swipeAttrToInt(s: String?): Int {
        if (s == null) return SWIPEDIRECTION_CIRCULAR
        return if (s == "0") SWIPEDIRECTION_NONE else if (s == "1") com.hypersoft.cyclicseekbar.CyclicSeekbar.Companion.SWIPEDIRECTION_VERTICAL // vertical
        else if (s == "2") SWIPEDIRECTION_HORIZONTAL // horizontal
        else if (s == "3") SWIPEDIRECTION_HORIZONTALVERTICAL // both
        else if (s == "4") SWIPEDIRECTION_CIRCULAR // default  - circular
        else SWIPEDIRECTION_CIRCULAR
    }

    fun clickAttrToInt(s: String?): Int {
        if (s == null) return ONCLICK_NEXT
        return if (s == "0") ONCLICK_NONE else if (s == "1") ONCLICK_NEXT // default - next
        else if (s == "2") ONCLICK_PREV // prev
        else if (s == "3") ONCLICK_RESET // reset
        else if (s == "4") ONCLICK_MENU // menu
        else if (s == "5") ONCLICK_USER // menu
        else ONCLICK_NEXT
    }

    fun balloonAnimationAttrToInt(s: String?): Int {
        if (s == null) return BALLONANIMATION_POP
        return if (s == "0") BALLONANIMATION_POP // pop
        else if (s == "1") BALLONANIMATION_SCALE // scale
        else if (s == "2") BALLONANIMATION_FADE // fade
        else BALLONANIMATION_POP
    }

    private fun disallowParentToHandleTouchEvents() {
        val parent = parent
        parent?.requestDisallowInterceptTouchEvent(true)
    }

    fun clickMe(view: View?) {
        when (clickBehaviour) {
            ONCLICK_NONE -> {}
            ONCLICK_NEXT -> toggle(isAnimation)
            ONCLICK_PREV -> inverseToggle(isAnimation)
            ONCLICK_RESET -> revertToDefault(isAnimation)
            ONCLICK_MENU -> createPopupMenu(view)
            ONCLICK_USER -> runUserBehaviour()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initListeners() {
        setOnClickListener(OnClickListener { view ->
            if (!enabled) return@OnClickListener
            clickMe(view)
        })
        setOnTouchListener(object : OnTouchListener {
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                if (!enabled) return false
                if (swipeDirection == SWIPEDIRECTION_NONE) {
                    toggle(isAnimation)
                    return false
                }
                val action = motionEvent.action
                if (swipeDirection == com.hypersoft.cyclicseekbar.CyclicSeekbar.Companion.SWIPEDIRECTION_VERTICAL) {  // vertical
                    val y = motionEvent.y.toInt()
                    if (action == MotionEvent.ACTION_DOWN) {
                        swipeY = y
                        swipeing = false
                        disallowParentToHandleTouchEvents() // needed when Knob's parent is a ScrollView
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        if (y - swipeY > swipeSensibilityPixels) {
                            swipeY = y
                            swipeing = true
                            decreaseValue()
                            return true
                        } else if (swipeY - y > swipeSensibilityPixels) {
                            swipeY = y
                            swipeing = true
                            increaseValue()
                            return true
                        }
                    } else if (action == MotionEvent.ACTION_UP) {
                        if (!swipeing) clickMe(view) // click
                        return true
                    }
                    return false
                } else if (swipeDirection == SWIPEDIRECTION_HORIZONTAL) {  // horizontal
                    val x = motionEvent.x.toInt()
                    if (action == MotionEvent.ACTION_DOWN) {
                        swipeX = x
                        swipeing = false
                        disallowParentToHandleTouchEvents() // needed when Knob's parent is a ScrollView
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        if (x - swipeX > swipeSensibilityPixels) {
                            swipeX = x
                            swipeing = true
                            increaseValue()
                            return true
                        } else if (swipeX - x > swipeSensibilityPixels) {
                            swipeX = x
                            swipeing = true
                            decreaseValue()
                            return true
                        }
                    } else if (action == MotionEvent.ACTION_UP) {
                        if (!swipeing) clickMe(view) // click
                        return true
                    }
                    return false
                } else if (swipeDirection == Companion.SWIPEDIRECTION_HORIZONTALVERTICAL) {  // both
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    if (action == MotionEvent.ACTION_DOWN) {
                        swipeX = x
                        swipeY = y
                        swipeing = false
                        disallowParentToHandleTouchEvents() // needed when Knob's parent is a ScrollView
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        if (x - swipeX > swipeSensibilityPixels || swipeY - y > swipeSensibilityPixels) {
                            swipeX = x
                            swipeY = y
                            swipeing = true
                            increaseValue()
                            return true
                        } else if (swipeX - x > swipeSensibilityPixels || y - swipeY > swipeSensibilityPixels) {
                            swipeX = x
                            swipeY = y
                            swipeing = true
                            decreaseValue()
                            return true
                        }
                    } else if (action == MotionEvent.ACTION_UP) {
                        if (!swipeing) clickMe(view) // click
                        return true
                    }
                    return false
                } else if (swipeDirection == SWIPEDIRECTION_CIRCULAR) { // circular
                    val x = motionEvent.x.toInt()
                    val y = motionEvent.y.toInt()
                    if (action == MotionEvent.ACTION_DOWN) {
                        swipeing = false
                        disallowParentToHandleTouchEvents() // needed when Knob's parent is a ScrollView
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        val angle = Math.atan2((y - centerY).toDouble(), (x - centerX).toDouble())
                        swipeing = true
                        setValueByAngle(angle, isAnimation)
                        return true
                    } else if (action == MotionEvent.ACTION_UP) {
                        if (!swipeing) clickMe(view) // click
                        return true
                    }
                    return false
                }
                return false
            }
        })
        spring?.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                currentAngle = spring.currentValue
                postInvalidate()
            }
        })
    }

    fun createPopupMenu(view: View?) {
        val mPopupMenu = PopupMenu(context, view)
        if (balloonValuesArray == null) for (w in 0 until numberOfStates) mPopupMenu.menu.add(
            Menu.NONE,
            w + 1,
            w + 1,
            Integer.toString(w)
        ) else for (w in 0 until numberOfStates) mPopupMenu.menu.add(
            Menu.NONE, w + 1, w + 1, balloonValuesArray!![w].toString()
        )
        mPopupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                val i = item.itemId - 1
                state = i
                return true
            }
        })
        mPopupMenu.show()
    }

    fun initStatus() {
        currentState = defaultState
        previousState = defaultState
        calcActualState()
        currentAngle = calcAngle(currentState)
        spring?.currentValue = currentAngle
    }

    fun initBalloons() {}

    // behaviour
    @JvmOverloads
    fun toggle(animate: Boolean = isAnimation) {
        increaseValue(animate)
    }

    @JvmOverloads
    fun inverseToggle(animate: Boolean = isAnimation) {
        decreaseValue(animate)
    }

    @JvmOverloads
    fun revertToDefault(animate: Boolean = isAnimation) {
        setState(defaultState, animate)
    }

    private fun calcActualState() {
        actualState = currentState % numberOfStates
        if (actualState < 0) actualState += numberOfStates
    }

    @JvmOverloads
    fun increaseValue(animate: Boolean = isAnimation) {
        previousState = currentState
        currentState = currentState + 1 // % numberOfStates;
        if (!isFreeRotation && currentState >= numberOfStates) currentState = numberOfStates - 1
        calcActualState()
        if (listener != null) listener?.onState(actualState)
        takeEffect(animate)
    }

    @JvmOverloads
    fun decreaseValue(animate: Boolean = isAnimation) {
        previousState = currentState
        currentState = currentState - 1 // % numberOfStates;
        if (!isFreeRotation && currentState < 0) currentState = 0
        calcActualState()
        if (listener != null) listener?.onState(actualState)
        takeEffect(animate)
    }

    fun setValueByAngle(
        angle: Double,
        animate: Boolean
    ) {  // sets the value of the knob given an angle instead of a state
        var angle = angle
        if (numberOfStates <= 1) return
        previousState = currentState
        var min = Math.toRadians(minAngle.toDouble())
        var max = Math.toRadians(maxAngle.toDouble() - 0.0001)
        val range = max - min
        var singleStepAngle = range / numberOfStates
        if (Math.PI * 2 - range < singleStepAngle) singleStepAngle = range / numberOfStates
        min = normalizeAngle(min).toFloat().toDouble()
        while (min > max) max += 2 * Math.PI // both min and max are positive and in the correct order.
        angle = normalizeAngle(angle + Math.PI / 2)
        while (angle < min) angle += 2 * Math.PI // set angle after minangle
        if (angle > max) { // if angle is out of range because the range is limited set to the closer limit
            angle = if (angle - max > min - angle + Math.PI * 2) min else max
        }
        currentState = ((angle - min) / singleStepAngle).toInt() // calculate value
        if (!isFreeRotation && Math.abs(currentState - previousState) == numberOfStates - 1) // manage free rotation
            currentState = previousState
        calcActualState()
        if (listener != null) listener?.onState(actualState)
        takeEffect(animate)
    }

    private fun takeEffect(animate: Boolean) {
        if (animate) {
            setIndicatorAngleWithDirection()
        } else {
            spring?.currentValue = calcAngle(actualState)
        }
        postInvalidate()
    }

    // public listener interface
    private var listener: com.hypersoft.cyclicseekbar.CyclicSeekbar.OnStateChanged? = null

    interface OnStateChanged {
        fun onState(state: Int)
    }

    fun setOnStateChanged(onStateChanged: com.hypersoft.cyclicseekbar.CyclicSeekbar.OnStateChanged?) {
        listener = onStateChanged
    }

    fun setState(newState: Int, animate: Boolean) {
        forceState(newState, animate)
        if (listener != null) listener?.onState(currentState)
    }

    @JvmOverloads
    fun forceState(newState: Int, animate: Boolean = isAnimation) {
        previousState = currentState
        currentState = newState
        calcActualState()
        takeEffect(animate)
    }

    var state: Int
        get() = actualState
        // methods
        set(newState) {
            setState(newState, isAnimation)
        }

    // getters and setters
    fun getNumberOfStates(): Int {
        return numberOfStates
    }

    fun setNumberOfStates(numberOfStates: Int) {
        setNumberOfStates(numberOfStates, isAnimation)
    }

    fun setNumberOfStates(numberOfStates: Int, animate: Boolean) {
        this.numberOfStates = numberOfStates
        takeEffect(animate)
    }

    fun getBorderWidth(): Int {
        return borderWidth
    }

    fun setBorderWidth(borderWidth: Int) {
        this.borderWidth = borderWidth
        takeEffect(isAnimation)
    }

    fun getBorderColor(): Int {
        return borderColor
    }

    fun setBorderColor(borderColor: Int) {
        this.borderColor = borderColor
        takeEffect(isAnimation)
    }

    fun getIndicatorWidth(): Int {
        return indicatorWidth
    }

    fun setIndicatorWidth(indicatorWidth: Int) {
        this.indicatorWidth = indicatorWidth
        takeEffect(isAnimation)
    }

    fun getIndicatorColor(): Int {
        return indicatorColor
    }

    fun setIndicatorColor(indicatorColor: Int) {
        this.indicatorColor = indicatorColor
        takeEffect(isAnimation)
    }

    fun getIndicatorRelativeLength(): Float {
        return indicatorRelativeLength
    }

    fun setIndicatorRelativeLength(indicatorRelativeLength: Float) {
        this.indicatorRelativeLength = indicatorRelativeLength
        takeEffect(isAnimation)
    }

    fun getKnobColor(): Int {
        return knobColor
    }

    fun setKnobColor(knobColor: Int) {
        this.knobColor = knobColor
        takeEffect(isAnimation)
    }

    fun getKnobRelativeRadius(): Float {
        return knobRelativeRadius
    }

    fun setKnobRelativeRadius(knobRelativeRadius: Float) {
        this.knobRelativeRadius = knobRelativeRadius
        takeEffect(isAnimation)
    }

    fun getKnobCenterRelativeRadius(): Float {
        return knobCenterRelativeRadius
    }

    fun setKnobCenterRelativeRadius(knobCenterRelativeRadius: Float) {
        this.knobCenterRelativeRadius = knobCenterRelativeRadius
        takeEffect(isAnimation)
    }

    fun getKnobCenterColor(): Int {
        return knobCenterColor
    }

    fun setKnobCenterColor(knobCenterColor: Int) {
        this.knobCenterColor = knobCenterColor
        takeEffect(isAnimation)
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        takeEffect(isAnimation)
    }

    fun getStateMarkersWidth(): Int {
        return stateMarkersWidth
    }

    fun setStateMarkersWidth(stateMarkersWidth: Int) {
        this.stateMarkersWidth = stateMarkersWidth
        takeEffect(isAnimation)
    }

    fun getStateMarkersColor(): Int {
        return stateMarkersColor
    }

    fun setStateMarkersColor(stateMarkersColor: Int) {
        this.stateMarkersColor = stateMarkersColor
        takeEffect(isAnimation)
    }

    fun getSelectedStateMarkerColor(): Int {
        return selectedStateMarkerColor
    }

    fun setSelectedStateMarkerColor(selectedStateMarkerColor: Int) {
        this.selectedStateMarkerColor = selectedStateMarkerColor
        takeEffect(isAnimation)
    }

    fun getStateMarkersRelativeLength(): Float {
        return stateMarkersRelativeLength
    }

    fun setStateMarkersRelativeLength(stateMarkersRelativeLength: Float) {
        this.stateMarkersRelativeLength = stateMarkersRelativeLength
        takeEffect(isAnimation)
    }

    fun getKnobRadius(): Float {
        return knobRadius
    }

    fun setKnobRadius(knobRadius: Float) {
        this.knobRadius = knobRadius
        takeEffect(isAnimation)
    }

    fun getStateMarkersAccentWidth(): Int {
        return stateMarkersAccentWidth
    }

    fun setStateMarkersAccentWidth(stateMarkersAccentWidth: Int) {
        this.stateMarkersAccentWidth = stateMarkersAccentWidth
        takeEffect(isAnimation)
    }

    fun getStateMarkersAccentColor(): Int {
        return stateMarkersAccentColor
    }

    fun setStateMarkersAccentColor(stateMarkersAccentColor: Int) {
        this.stateMarkersAccentColor = stateMarkersAccentColor
        takeEffect(isAnimation)
    }

    fun getStateMarkersAccentRelativeLength(): Float {
        return stateMarkersAccentRelativeLength
    }

    fun setStateMarkersAccentRelativeLength(stateMarkersAccentRelativeLength: Float) {
        this.stateMarkersAccentRelativeLength = stateMarkersAccentRelativeLength
        takeEffect(isAnimation)
    }

    fun getStateMarkersAccentPeriodicity(): Int {
        return stateMarkersAccentPeriodicity
    }

    fun setStateMarkersAccentPeriodicity(stateMarkersAccentPeriodicity: Int) {
        this.stateMarkersAccentPeriodicity = stateMarkersAccentPeriodicity
        takeEffect(isAnimation)
    }

    fun getKnobDrawableRes(): Int {
        return knobDrawableRes
    }

    fun setKnobDrawableRes(knobDrawableRes: Int) {
        this.knobDrawableRes = knobDrawableRes
        takeEffect(isAnimation)
    }

    fun isKnobDrawableRotates(): Boolean {
        return knobDrawableRotates
    }

    fun setKnobDrawableRotates(knobDrawableRotates: Boolean) {
        this.knobDrawableRotates = knobDrawableRotates
        takeEffect(isAnimation)
    }

    fun getCircularIndicatorRelativeRadius(): Float {
        return circularIndicatorRelativeRadius
    }

    fun setCircularIndicatorRelativeRadius(circularIndicatorRelativeRadius: Float) {
        this.circularIndicatorRelativeRadius = circularIndicatorRelativeRadius
        takeEffect(isAnimation)
    }

    fun getCircularIndicatorRelativePosition(): Float {
        return circularIndicatorRelativePosition
    }

    fun setCircularIndicatorRelativePosition(circularIndicatorRelativePosition: Float) {
        this.circularIndicatorRelativePosition = circularIndicatorRelativePosition
        takeEffect(isAnimation)
    }

    fun getCircularIndicatorColor(): Int {
        return circularIndicatorColor
    }

    fun setCircularIndicatorColor(circularIndicatorColor: Int) {
        this.circularIndicatorColor = circularIndicatorColor
        takeEffect(isAnimation)
    }

    fun isSelectedStateMarkerContinuous(): Boolean {
        return selectedStateMarkerContinuous
    }

    fun setSelectedStateMarkerContinuous(selectedStateMarkerContinuous: Boolean) {
        this.selectedStateMarkerContinuous = selectedStateMarkerContinuous
        takeEffect(isAnimation)
    }

    fun getMinAngle(): Float {
        return minAngle
    }

    fun setMinAngle(minAngle: Float) {
        this.minAngle = minAngle
        takeEffect(isAnimation)
    }

    fun getMaxAngle(): Float {
        return maxAngle
    }

    fun setMaxAngle(maxAngle: Float) {
        this.maxAngle = maxAngle
        takeEffect(isAnimation)
    }

    fun getExternalRadius(): Float {
        return externalRadius
    }

    fun setExternalRadius(externalRadius: Float) {
        this.externalRadius = externalRadius
        takeEffect(isAnimation)
    }

    fun getKnobDrawable(): Drawable? {
        return knobDrawable
    }

    fun setKnobDrawable(knobDrawable: Drawable?) {
        this.knobDrawable = knobDrawable
        takeEffect(isAnimation)
    }

    fun setUserBehaviour(userRunnable: Runnable?) {
        // when "user" click behaviour is selected
        this.userRunnable = userRunnable
    }

    fun runUserBehaviour() {   // to be initialized with setUserBehaviour()
        if (userRunnable == null) return
        userRunnable?.run()
    }

    companion object {
        const val SWIPEDIRECTION_NONE = 0
        const val SWIPEDIRECTION_VERTICAL = 1
        const val SWIPEDIRECTION_HORIZONTAL = 2
        const val SWIPEDIRECTION_HORIZONTALVERTICAL = 3
        const val SWIPEDIRECTION_CIRCULAR = 4
        const val ONCLICK_NONE = 0
        const val ONCLICK_NEXT = 1
        const val ONCLICK_PREV = 2
        const val ONCLICK_RESET = 3
        const val ONCLICK_MENU = 4
        const val ONCLICK_USER = 5
        const val BALLONANIMATION_POP = 0
        const val BALLONANIMATION_SCALE = 1
        const val BALLONANIMATION_FADE = 2
    }
}