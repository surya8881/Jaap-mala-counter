package com.example.japmala

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class MalaView(context: Context) : View(context) {

    var count: Int = 0
        set(value) { field = value; invalidate() }
    val total = 108

    private val nf = NumberFormat.getInstance(Locale.US)
    private val doneBead = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E65100") }
    private val pendingBead = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D7CCC8") }
    private val numPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4E342E")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8D6E63")
        textAlign = Paint.Align.CENTER
    }
    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A1887F")
        textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val size = min(width, height).toFloat()
        val beadR = size / 48f
        val ringR = size / 2f - beadR * 2f

        for (i in 0 until total) {
            val angle = Math.toRadians(-90.0 + i * 360.0 / total)
            val x = cx + ringR * cos(angle).toFloat()
            val y = cy + ringR * sin(angle).toFloat()
            canvas.drawCircle(x, y, beadR, if (i < count) doneBead else pendingBead)
        }

        numPaint.textSize = size / 4f
        canvas.drawText(nf.format(count), cx, cy + size / 12f, numPaint)
        subPaint.textSize = size / 14f
        canvas.drawText("/ " + nf.format(total), cx, cy + size / 12f + size / 10f, subPaint)
        hintPaint.textSize = size / 20f
        canvas.drawText("Tap to count", cx, cy - size / 5f, hintPaint)
    }
}
