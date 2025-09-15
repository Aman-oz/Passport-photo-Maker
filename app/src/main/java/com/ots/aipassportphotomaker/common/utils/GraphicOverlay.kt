package com.ots.aipassportphotomaker.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.ots.aipassportphotomaker.common.utils.BitmapUtils.isMemorySufficient
import java.util.Stack
import kotlin.compareTo

/**
 * Created by Aman Ullah
 * Sr. Software Engineer Android
 * Created on 13 Aug,2025 15:20
 * Copyright (c) All rights reserved.
 */


/**
 * A view which renders a series of custom graphics to be overlayed on top of an associated preview
 * (i.e., the camera preview). The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.
 *
 *
 * Supports scaling and mirroring of the graphics relative the camera's preview properties. The
 * idea is that detection items are expressed in terms of an image size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.
 *
 *
 * Associated [Graphic] items should use the following methods to convert to view
 * coordinates for the graphics that are drawn:
 *
 *
 *  1. [Graphic.scale] adjusts the size of the supplied value from the image scale
 * to the view scale.
 *  1. [Graphic.translateX] and [Graphic.translateY] adjust the
 * coordinate from the image's coordinate system to the view coordinate system.
 *
 */


open class GraphicOverlay(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val TAG = GraphicOverlay::class.java.simpleName

    private val transformationMatrix = Matrix()
    var imageWidth = 0
        private set
    var imageHeight = 0
        private set

    private var scaleFactor = 1.0f
    private var postScaleWidthOffset = 0f
    private var postScaleHeightOffset = 0f
    private var needUpdateTransformation = true

    private var brushSize = 50f
    private val brushPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private var touchX = 50f
    private var touchY = 50f
    private var isBrushVisible = false

    private var livePath: Path = Path()
    private var pathPaint: Paint
    private var originalBitmap: Bitmap? = null
    private var imageBitmap: Bitmap? = null
    private val actionHistory = Stack<Bitmap>()
    private val redoHistory = Stack<Bitmap>()
    private var pathX = 0f
    private var pathY = 0f
    private var undoButton: ImageView? = null
    private var redoButton: ImageView? = null
    private var currentAction: DrawViewAction = DrawViewAction.ERASE_BACKGROUND
    private var isDrawing = false

    private var brushOffset = 0f // Default offset
    private var brushActualX = 0f // Actual touch position
    private var brushActualY = 0f // Actual touch position

    init {
        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = brushSize
        }
        setAction(DrawViewAction.ERASE_BACKGROUND)
    }

    fun getBrushSize(): Float = brushSize

    fun getCurrentAction(): DrawViewAction = currentAction

    fun setBrushOffset(offset: Float) {
        brushOffset = offset
        invalidate()
    }

    fun getBrushOffset(): Float = brushOffset

    fun setBrushSize(size: Float) {
        brushSize = size
        pathPaint.strokeWidth = size
        invalidate()
    }

    fun showBrush() {
        isBrushVisible = true
        invalidate()
    }

    fun hideBrush() {
        isBrushVisible = false
        invalidate()
    }

    fun setAction(action: DrawViewAction) {
        isDrawing = false
        livePath.reset()
        currentAction = action
        when (action) {
            DrawViewAction.ERASE_BACKGROUND -> {
                pathPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                pathPaint.color = Color.TRANSPARENT
                showBrush()
            }
            DrawViewAction.RECOVER_AREA -> {
                pathPaint.xfermode = null
                pathPaint.color = Color.WHITE // Placeholder for recovery
                showBrush()
            }
            DrawViewAction.NONE -> {
                pathPaint.xfermode = null
                hideBrush()
            }
        }
        updateBrushIndicator()
    }

    private fun updateBrushIndicator() {
        brushPaint.color = when (currentAction) {
            DrawViewAction.ERASE_BACKGROUND -> Color.BLUE
            DrawViewAction.RECOVER_AREA -> Color.GREEN
            DrawViewAction.NONE -> Color.GRAY
        }
    }

    fun setBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            val convertedBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                bitmap.copy(Bitmap.Config.ARGB_8888, true)
            } else {
                bitmap.copy(Bitmap.Config.ARGB_8888, true)
            }

            originalBitmap = convertedBitmap.copy(Bitmap.Config.ARGB_8888, false)
            imageBitmap = convertedBitmap
            imageWidth = imageBitmap!!.width
            imageHeight = imageBitmap!!.height
            needUpdateTransformation = true

            actionHistory.clear()
            redoHistory.clear()
            updateButtonStates()

            invalidate()
        } else {
            originalBitmap = null
            imageBitmap = null
            invalidate()
        }
    }

    fun getCurrentBitmap(): Bitmap? = imageBitmap

    fun setButtons(undoButton: ImageView?, redoButton: ImageView?) {
        this.undoButton = undoButton
        this.redoButton = redoButton
        updateButtonStates()
    }

    private fun updateButtonStates() {
        undoButton?.isEnabled = actionHistory.isNotEmpty()
        redoButton?.isEnabled = redoHistory.isNotEmpty()
    }

    fun undo() {
        if (actionHistory.isNotEmpty() && imageBitmap != null) {
            redoHistory.push(imageBitmap!!.copy(Bitmap.Config.ARGB_8888, false))
            val previousState = actionHistory.pop()
            imageBitmap = previousState.copy(Bitmap.Config.ARGB_8888, true)
            updateButtonStates()
            invalidate()
        }
    }

    fun redo() {
        if (redoHistory.isNotEmpty() && imageBitmap != null) {
            actionHistory.push(imageBitmap!!.copy(Bitmap.Config.ARGB_8888, false))
            val nextState = redoHistory.pop()
            imageBitmap = nextState.copy(Bitmap.Config.ARGB_8888, true)
            updateButtonStates()
            invalidate()
        }
    }

    private fun saveStateToHistory() {
        imageBitmap?.let { bitmap ->
            actionHistory.push(bitmap.copy(Bitmap.Config.ARGB_8888, false))
            redoHistory.clear()
            updateButtonStates()
        }
    }

    private fun updateTransformationIfNeeded() {
        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) return

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val viewAspectRatio = viewWidth / viewHeight
        val imageAspectRatio = imageWidth.toFloat() / imageHeight

        transformationMatrix.reset()

        if (viewAspectRatio > imageAspectRatio) {
            scaleFactor = viewHeight / imageHeight
            val scaledWidth = imageWidth * scaleFactor
            postScaleWidthOffset = (viewWidth - scaledWidth) / 2
            postScaleHeightOffset = 0f
            transformationMatrix.setScale(scaleFactor, scaleFactor)
            transformationMatrix.postTranslate(postScaleWidthOffset, 0f)
        } else {
            scaleFactor = viewWidth / imageWidth
            val scaledHeight = imageHeight * scaleFactor
            postScaleWidthOffset = 0f
            postScaleHeightOffset = (viewHeight - scaledHeight) / 2
            transformationMatrix.setScale(scaleFactor, scaleFactor)
            transformationMatrix.postTranslate(0f, postScaleHeightOffset)
        }

        needUpdateTransformation = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (touchX == 50f && touchY == 50f) {  // Only if still at default position
            touchX = w * 0.8f
            touchY = h * 0.8f
            invalidate()
        }

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateTransformationIfNeeded()
        canvas.save()

        imageBitmap?.let { bitmap ->
            if (isMemorySufficient(bitmap)) {
                canvas.drawBitmap(bitmap, transformationMatrix, null)
            }
        }

        canvas.restore()

        if (isBrushVisible && touchX >= 0 && touchY >= 0) {
            val offsetY = touchY - brushOffset

            // Create fill paint with transparent color based on current action
            val fillPaint = Paint().apply {
                style = Paint.Style.FILL
                when (currentAction) {
                    DrawViewAction.ERASE_BACKGROUND -> {
                        color = Color.BLUE
                        alpha = 60 // Transparent blue (about 25% opacity)
                    }
                    DrawViewAction.RECOVER_AREA -> {
                        color = Color.GREEN
                        alpha = 60 // Transparent green (about 25% opacity)
                    }
                    DrawViewAction.NONE -> {
                        color = Color.GRAY
                        alpha = 40 // Transparent gray
                    }
                }
            }

            // Draw filled circle first (background)
            canvas.drawCircle(touchX, offsetY, brushSize / 2, fillPaint)

            // Draw stroke circle on top (border)
            canvas.drawCircle(touchX, offsetY, brushSize / 2, brushPaint)

            /*if (brushOffset > 0) {
                val linePaint = Paint().apply {
                    color = brushPaint.color
                    strokeWidth = 4f
                    alpha = 100
                }
                canvas.drawLine(touchX, touchY, touchX, offsetY, linePaint)
            }*/
        }
    }

    init {
        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            needUpdateTransformation = true
        }
    }

    private fun touchStart(x: Float, y: Float) {
        if (currentAction == DrawViewAction.NONE) return


        val imagePoint = screenToImageCoordinates(x, y)
        if (imagePoint != null && imagePoint[1] >= 0) {
            isDrawing = true
            pathX = imagePoint[0]
            pathY = imagePoint[1]
            livePath.reset()
            livePath.moveTo(pathX, pathY)
            saveStateToHistory()
        }
    }

    private fun touchMove(x: Float, y: Float) {
        if (currentAction == DrawViewAction.NONE || !isDrawing) return

        val imagePoint = screenToImageCoordinates(x, y)
        if (imagePoint != null) {
            val newX = imagePoint[0]
            val newY = imagePoint[1]
            val dx = Math.abs(newX - pathX)
            val dy = Math.abs(newY - pathY)
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                livePath.quadTo(pathX, pathY, (newX + pathX) / 2, (newY + pathY) / 2)
                pathX = newX
                pathY = newY
                applyPathToBitmap()
            }
        }
    }

    private fun touchUp() {
        if (currentAction == DrawViewAction.NONE || !isDrawing) return

        isDrawing = false
        livePath.lineTo(pathX, pathY)
        applyPathToBitmap()
        livePath.reset()
        invalidate()
    }

    private fun screenToImageCoordinates(screenX: Float, screenY: Float): FloatArray? {
        if (imageBitmap == null) return null

        val invertedMatrix = Matrix()
        if (transformationMatrix.invert(invertedMatrix)) {
            val point = floatArrayOf(screenX, screenY)
            invertedMatrix.mapPoints(point)
            if (point[0] >= 0 && point[0] < imageWidth && point[1] >= 0 && point[1] < imageHeight) {
                return point
            }
        }
        return null
    }

    private fun applyPathToBitmap() {
        imageBitmap?.let { bitmap ->
            if (currentAction == DrawViewAction.NONE) return

            val canvas = Canvas(bitmap)
            when (currentAction) {
                DrawViewAction.ERASE_BACKGROUND -> {
                    canvas.drawPath(livePath, pathPaint)
                }
                DrawViewAction.RECOVER_AREA -> {
                    originalBitmap?.let { original ->
                        val maskBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
                        val maskCanvas = Canvas(maskBitmap)
                        maskCanvas.drawPath(livePath, Paint().apply {
                            color = Color.WHITE
                            style = Paint.Style.STROKE
                            strokeWidth = brushSize
                            strokeCap = Paint.Cap.ROUND
                            strokeJoin = Paint.Join.ROUND
                            isAntiAlias = true
                        })

                        val tempBitmap = original.copy(Bitmap.Config.ARGB_8888, true)
                        val tempCanvas = Canvas(tempBitmap)
                        tempCanvas.drawBitmap(maskBitmap, 0f, 0f, Paint().apply {
                            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                        })

                        canvas.drawBitmap(tempBitmap, 0f, 0f, Paint().apply {
                            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
                            isAntiAlias = true
                        })

                        maskBitmap.recycle()
                        tempBitmap.recycle()
                    }
                }
                DrawViewAction.NONE -> return
            }
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (imageBitmap == null || currentAction == DrawViewAction.NONE) {
            return super.onTouchEvent(event)
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Store actual touch position
                brushActualX = event.x
                brushActualY = event.y

                touchX = event.x
                touchY = event.y

                // Apply offset to drawing position
                val drawingY = event.y - brushOffset
                touchStart(event.x, drawingY)
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Store actual touch position
                brushActualX = event.x
                brushActualY = event.y

                touchX = event.x
                touchY = event.y

                // Apply offset to drawing position
                val drawingY = event.y - brushOffset
                touchMove(event.x, drawingY)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }
}