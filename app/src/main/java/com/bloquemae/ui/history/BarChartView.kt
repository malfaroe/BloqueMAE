package com.bloquemae.ui.history

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.bloquemae.data.BlockWithStats
import kotlin.math.min

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFB0B0B0.toInt()
        textAlign = Paint.Align.CENTER
        textSize = 28f
    }
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF444444.toInt()
        strokeWidth = 2f
    }

    private var blocks: List<BlockWithStats> = emptyList()

    fun setData(data: List<BlockWithStats>) {
        // Show last 10 blocks, oldest → newest left to right
        blocks = data.takeLast(10).reversed()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (blocks.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        val paddingBottom = 48f  // space for block number labels
        val paddingTop = 24f
        val paddingSide = 16f
        val chartH = h - paddingBottom - paddingTop
        val chartW = w - paddingSide * 2

        // X axis line
        canvas.drawLine(paddingSide, h - paddingBottom, w - paddingSide, h - paddingBottom, axisPaint)

        val n = blocks.size
        val barWidth = min(chartW / n * 0.6f, 60f)
        val gap = chartW / n

        blocks.forEachIndexed { i, block ->
            val cx = paddingSide + gap * i + gap / 2f
            val pct = block.completionPct.coerceIn(0f, 1f)
            val barH = chartH * pct
            val top = paddingTop + chartH - barH
            val bottom = h - paddingBottom

            barPaint.color = lerpColor(pct)
            canvas.drawRoundRect(cx - barWidth / 2, top, cx + barWidth / 2, bottom, 6f, 6f, barPaint)

            // % label above bar
            if (pct > 0f) {
                labelPaint.textSize = 26f
                canvas.drawText("${(pct * 100).toInt()}%", cx, top - 6f, labelPaint)
            }

            // Block number label below axis
            labelPaint.textSize = 24f
            canvas.drawText("#${block.number}", cx, h - 10f, labelPaint)
        }
    }

    // Interpolates red→yellow→green based on 0..1
    private fun lerpColor(t: Float): Int {
        return if (t < 0.5f) {
            val f = t / 0.5f
            Color.rgb((255).toInt(), (255 * f).toInt(), 0)
        } else {
            val f = (t - 0.5f) / 0.5f
            Color.rgb((255 * (1f - f)).toInt(), 255, 0)
        }
    }
}
