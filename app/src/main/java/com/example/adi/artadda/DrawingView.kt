package com.example.adi.artadda

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attr: AttributeSet): View(context, attr) {

    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()

    private var drawPath: CustomPath? = null
    private var canvasBitmap: Bitmap? = null
    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null
    private var brushThickness: Float = 0.0f
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val paths: ArrayList<CustomPath> = ArrayList()
    private var undoPaths: ArrayList<CustomPath> = ArrayList()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        drawPaint = Paint()
        drawPath = CustomPath(color, brushThickness)
        drawPaint?.color = color
        drawPaint?.style = Paint.Style.STROKE
        drawPaint?.strokeJoin = Paint.Join.ROUND
        drawPaint?.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasBitmap?.let { canvas.drawBitmap(it, 0f, 0f, canvasPaint) }

        for (path in paths) {
            drawPaint?.strokeWidth = path.brushThickness
            drawPaint?.color = path.color
            canvas.drawPath(path, drawPaint!!)
        }

        if (!drawPath!!.isEmpty) {
            drawPaint?.strokeWidth = drawPath!!.brushThickness
            drawPaint?.color = drawPath!!.color
            canvas.drawPath(drawPath!!, drawPaint!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath?.color = color
                drawPath?.brushThickness = brushThickness
                drawPath?.reset()
                if (touchY != null) {
                    if (touchX != null) {
                        drawPath?.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        drawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                paths.add(drawPath!!)
                drawPath = CustomPath(color, brushThickness)
            }
            else -> return false
        }
        invalidate()

        return true
    }

    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
    }

    fun setBrushThickness(newThickness: Float) {
        brushThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newThickness, resources.displayMetrics)
    }

    fun onClickUndo() {
        if (paths.size > 0) {
            undoPaths.add(paths.removeAt(paths.size - 1))
            invalidate()
        }
    }

    fun onClickRedo() {
        if (undoPaths.size > 0) {
            paths.add(undoPaths.removeAt(undoPaths.size - 1))
            invalidate()
        }
    }
}