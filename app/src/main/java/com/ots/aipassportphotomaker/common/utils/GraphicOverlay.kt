package com.ots.aipassportphotomaker.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.Log
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.ots.aipassportphotomaker.R
import com.ots.aipassportphotomaker.common.utils.BitmapUtils.isMemorySufficient
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.util.*
import kotlin.text.get
import kotlin.times

/**
 * Created by Hamza Chaudhary
 * Sr. Software Engineer Android
 * Created on 25 Jan,2022 15:20
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


open class GraphicOverlay(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private val TAG = GraphicOverlay::class.java.simpleName
    private val lock = Any()

    // Matrix for transforming from image coordinates to overlay view coordinates.
    private val transformationMatrix = Matrix()
    var imageWidth = 0
        private set
    var imageHeight = 0
        private set

    // The factor of overlay View size to image size. Anything in the image coordinates need to be
    // scaled by this amount to fit with the area of overlay View.
    private var scaleFactor = 1.0f

    // The number of horizontal pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private var postScaleWidthOffset = 0f

    // The number of vertical pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private var postScaleHeightOffset = 0f
    private var isImageFlipped = false
    private var needUpdateTransformation = true

    private var brushSize = 50f
    private val brushPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private var touchX = 50f
    private var touchY = 50f
    private var isBrushVisible = true

    fun setBrushSize(size: Int) {
        brushSize = size.toFloat()
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

    fun isBrushVisible(): Boolean {
        return isBrushVisible
    }
    /**
     * Removes all graphics from the overlay.
     */
    fun clear() {
        //synchronized(lock) { graphics.clear() }
        postInvalidate()
    }



    private fun updateTransformationIfNeeded() {
        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
            return
        }

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val viewAspectRatio = viewWidth / viewHeight
        val imageAspectRatio = imageWidth.toFloat() / imageHeight

        transformationMatrix.reset()

        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically centered
            scaleFactor = viewHeight / imageHeight
            val scaledWidth = imageWidth * scaleFactor
            postScaleWidthOffset = (viewWidth - scaledWidth) / 2
            postScaleHeightOffset = 0f

            transformationMatrix.setScale(scaleFactor, scaleFactor)
            transformationMatrix.postTranslate(postScaleWidthOffset, 0f)
        } else {
            // The image needs to be horizontally centered
            scaleFactor = viewWidth / imageWidth
            val scaledHeight = imageHeight * scaleFactor
            postScaleWidthOffset = 0f
            postScaleHeightOffset = (viewHeight - scaledHeight) / 2

            transformationMatrix.setScale(scaleFactor, scaleFactor)
            transformationMatrix.postTranslate(0f, postScaleHeightOffset)
        }

        if (isImageFlipped) {
            transformationMatrix.postScale(-1f, 1f, width / 2f, height / 2f)
        }

        needUpdateTransformation = false
    }


    private var mask: ByteBuffer? = null
    private var maskWidth: Int = 0
    private var maskHeight: Int = 0



    /**
     * Draws the overlay with its associated graphic objects.
     */

    /*@SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


            Log.d("onDrawGraphic", "onDraw GraphicOverlay Manual")
            canvas.save()
            if (imageBitmap != null && isMemorySufficient(imageBitmap!!)) {
                canvas.drawBitmap(imageBitmap!!, transformationMatrix, null)
                for (action in cuts) {
                    if (action.first != null) {
                        canvas.drawPath(action.first!!.first, action.first!!.second)
                    }
                }
                if (currentAction == DrawViewAction.MANUAL_CLEAR) {
                    canvas.drawPath(livePath, pathPaint)
                }
            }
            canvas.restore()

        if (isBrushVisible && touchX >= 0 && touchY >= 0) {
            canvas.drawCircle(touchX, touchY, brushSize, brushPaint)
        }
    }*/

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        updateTransformationIfNeeded()

        Log.d("onDrawGraphic", "onDraw GraphicOverlay Manual")
        canvas.save()
        if (imageBitmap != null && isMemorySufficient(imageBitmap!!)) {
            // Apply the transformation matrix to center the image
            canvas.drawBitmap(imageBitmap!!, transformationMatrix, null)
            for (action in cuts) {
                if (action.first != null) {
                    canvas.drawPath(action.first!!.first, action.first!!.second)
                }
            }
            if (currentAction == DrawViewAction.MANUAL_CLEAR) {
                canvas.drawPath(livePath, pathPaint)
            }
        }
        canvas.restore()

        if (isBrushVisible && touchX >= 0 && touchY >= 0) {
            canvas.drawCircle(touchX, touchY, brushSize, brushPaint)
        }
    }

    init {
        addOnLayoutChangeListener { view: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int ->
            needUpdateTransformation = true
        }
    }


    internal var livePath: Path
    private var pathPaint: Paint
    private var imageBitmap: Bitmap? = null
    internal val cuts = Stack<Pair<Pair<Path, Paint>?, Bitmap?>>()
    internal val undoneCuts = Stack<Pair<Pair<Path, Paint>?, Bitmap?>>()
    internal var pathX = 0f
    internal var pathY = 0f
    private var undoButton: ImageView? = null
    private var redoButton: ImageView? = null
    private var loadingModal: View? = null
    internal var currentAction: DrawViewAction? = null


    open fun setButtons(undoButton: ImageView?, redoButton: ImageView?) {
        this.undoButton = undoButton
        this.redoButton = redoButton
    }

    override fun onSizeChanged(newWidth: Int, newHeight: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight)
        resizeBitmap(newWidth, newHeight)
    }


    private fun touchStart(x: Float, y: Float) {
        pathX = x
        pathY = y
        undoneCuts.clear()
        redoButton?.isEnabled = false
        if (currentAction == DrawViewAction.AUTO_CLEAR) {
            AutomaticPixelClearingTask(this).execute(x.toInt(), y.toInt())
        } else {
            livePath.moveTo(x, y)
        }
        invalidate()
    }

    private fun touchMove(x: Float, y: Float) {
        if (currentAction == DrawViewAction.MANUAL_CLEAR) {
            val dx = Math.abs(x - pathX)
            val dy = Math.abs(y - pathY)
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                livePath.quadTo(pathX, pathY, (x + pathX) / 2, (y + pathY) / 2)
                pathX = x
                pathY = y
            }
        }
    }

    private fun touchUp() {
        if (currentAction == DrawViewAction.MANUAL_CLEAR) {
            livePath.lineTo(pathX, pathY)
            cuts.push(Pair(Pair(livePath, pathPaint), null))
            livePath = Path()
            undoButton?.isEnabled = true
        }
    }

    open fun undo() {
        if (cuts.size > 0) {
            val cut = cuts.pop()
            if (cut.second != null) {
                undoneCuts.push(Pair(null, imageBitmap))
                imageBitmap = cut.second
            } else {
                undoneCuts.push(cut)
            }
            if (cuts.isEmpty()) {
                undoButton?.isEnabled = false
            }
            redoButton?.isEnabled = true
            invalidate()
        }
        //toast the user
    }

    open fun redo() {
        if (undoneCuts.size > 0) {
            val cut = undoneCuts.pop()
            if (cut.second != null) {
                cuts.push(Pair(null, imageBitmap))
                imageBitmap = cut.second
            } else {
                cuts.push(cut)
            }
            if (undoneCuts.isEmpty()) {
                redoButton?.isEnabled = false
            }
            undoButton?.isEnabled = true
            invalidate()
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (imageBitmap != null && currentAction != DrawViewAction.ZOOM) {

            when (ev.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    touchX = ev.x
                    touchY = ev.y
                    if (ev.action == MotionEvent.ACTION_DOWN) {
                        Log.d(TAG, "onTouchEvent: Action down")
                        touchStart(ev.x, ev.y)
                    } else {
                        Log.d(TAG, "onTouchEvent: Action move")
                        touchMove(ev.x, ev.y)
                    }
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "onTouchEvent: Action up")
                    touchUp()
                    invalidate()
                    return true
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun resizeBitmap(width: Int, height: Int) {
        if (width > 0 && height > 0 && imageBitmap != null) {
            imageBitmap?.let {
                imageBitmap = BitmapUtils.getResizedBitmap(it, width, height)
                imageBitmap?.setHasAlpha(true)
                invalidate()
            }

        }
    }

    fun resizeBitmapWithoutScaling(originalBitmap: Bitmap): Bitmap {

        // Calculate the desired maximum dimensions for your view
        val maxViewWidth = resources.getDimensionPixelSize(R.dimen._400sdp)
        val maxViewHeight = resources.getDimensionPixelSize(R.dimen._420sdp)

        // Calculate the scaling factors to fit within the view
        val scaleFactorX = maxViewWidth.toFloat() / originalBitmap.width
        val scaleFactorY = maxViewHeight.toFloat() / originalBitmap.height
        val scaleFactor = minOf(scaleFactorX, scaleFactorY)

        // Calculate the new dimensions
        val newWidth = (originalBitmap.width * scaleFactor).toInt()
        val newHeight = (originalBitmap.height * scaleFactor).toInt()

        // Resize the image while maintaining its aspect ratio
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        return resizedBitmap
    }

    fun setBitmap(bitmap: Bitmap?) {

        if (bitmap != null) {
            // Convert to a format that supports pixel manipulation if needed
            imageBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                bitmap.copy(Bitmap.Config.ARGB_8888, true)
            } else {
                bitmap
            }

            imageWidth = imageBitmap!!.width
            imageHeight = imageBitmap!!.height
            needUpdateTransformation = true
            invalidate()
        } else {
            imageBitmap = null
            invalidate()
        }
    }

    fun getCurrentBitmap(): Bitmap? {
        return imageBitmap
    }

    fun setAction(newAction: DrawViewAction?) {
        currentAction = newAction
    }

    fun setStrokeWidth(strokeWidth: Int) {
        pathPaint = Paint(pathPaint)
        pathPaint.strokeWidth = strokeWidth.toFloat()
    }

    fun setLoadingModal(loadingModal: View?) {
        this.loadingModal = loadingModal
    }

    /*t0ODO =============================>Automatic clearing task */
    class AutomaticPixelClearingTask(drawView: GraphicOverlay) :
        AsyncTask<Int?, Void?, Bitmap>() {
        private val drawViewWeakReference: WeakReference<GraphicOverlay>
        override fun onPreExecute() {
            super.onPreExecute()
            drawViewWeakReference.get()?.loadingModal?.visibility = VISIBLE
            drawViewWeakReference.get()?.cuts?.push(
                Pair(
                    null,
                    drawViewWeakReference.get()?.imageBitmap
                )
            )
        }

        override fun doInBackground(vararg points: Int?): Bitmap? {
            return try {
                val oldBitmap = drawViewWeakReference.get()?.imageBitmap
                    ?: return null

                // Convert HARDWARE bitmap to a mutable format
                val mutableBitmap = if (oldBitmap.config == Bitmap.Config.HARDWARE) {
                    oldBitmap.copy(Bitmap.Config.ARGB_8888, true)
                } else {
                    oldBitmap
                }

                // Check if coordinates are within bitmap bounds
                if (points[0] == null || points[1] == null ||
                    points[0]!! < 0 || points[0]!! >= mutableBitmap.width ||
                    points[1]!! < 0 || points[1]!! >= mutableBitmap.height) {
                    return null
                }

                val colorToReplace = mutableBitmap.getPixel(points[0]!!, points[1]!!)
                val width = mutableBitmap.width
                val height = mutableBitmap.height
                val pixels = IntArray(width * height)
                mutableBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

                val rA = Color.alpha(colorToReplace)
                val rR = Color.red(colorToReplace)
                val rG = Color.green(colorToReplace)
                val rB = Color.blue(colorToReplace)
                var pixel: Int

                // Iterate through pixels
                for (y in 0 until height) {
                    for (x in 0 until width) {
                        val index = y * width + x
                        pixel = pixels[index]
                        val rrA = Color.alpha(pixel)
                        val rrR = Color.red(pixel)
                        val rrG = Color.green(pixel)
                        val rrB = Color.blue(pixel)

                        if (rA - COLOR_TOLERANCE < rrA && rrA < rA + COLOR_TOLERANCE &&
                            rR - COLOR_TOLERANCE < rrR && rrR < rR + COLOR_TOLERANCE &&
                            rG - COLOR_TOLERANCE < rrG && rrG < rG + COLOR_TOLERANCE &&
                            rB - COLOR_TOLERANCE < rrB && rrB < rB + COLOR_TOLERANCE
                        ) {
                            pixels[index] = Color.TRANSPARENT
                        }
                    }
                }

                val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                newBitmap
            } catch (e: Exception) {
                Log.e("doInBackground", "Error processing bitmap: ${e.message}", e)
                null
            }
        }


        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            drawViewWeakReference.get()?.loadingModal?.visibility = INVISIBLE

            if (result == null) {
                Log.e("AsyncTask", "Failed to process bitmap")
            } else {
                drawViewWeakReference.get()?.apply {
                    imageBitmap = result
                    undoButton?.isEnabled = true
                    loadingModal?.visibility = INVISIBLE
                    invalidate()
                }
            }
        }

        init {
            drawViewWeakReference = WeakReference(drawView)
        }


    }


    companion object {
        private const val TOUCH_TOLERANCE = 4f
        private const val COLOR_TOLERANCE = 20f
    }

    init {
        livePath = Path()
        pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pathPaint.isDither = true
        pathPaint.color = Color.TRANSPARENT
        pathPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        pathPaint.style = Paint.Style.STROKE
        pathPaint.strokeJoin = Paint.Join.ROUND
        pathPaint.strokeCap = Paint.Cap.ROUND
    }
}
