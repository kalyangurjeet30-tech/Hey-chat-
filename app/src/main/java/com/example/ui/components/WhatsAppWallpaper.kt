package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun WhatsAppWallpaper(
  isDarkTheme: Boolean,
  modifier: Modifier = Modifier
) {
  val bgColor = if (isDarkTheme) Color(0xFF0B141A) else Color(0xFFEFEAE2)
  val doodleColor = if (isDarkTheme) Color(0x0CFFFFFF) else Color(0x18000000)

  Canvas(modifier = modifier.fillMaxSize()) {
    drawRect(color = bgColor)

    val stepX = 140f
    val stepY = 140f
    val cols = (size.width / stepX).toInt() + 2
    val rows = (size.height / stepY).toInt() + 2

    for (r in 0..rows) {
      for (c in 0..cols) {
        val x = c * stepX + (if (r % 2 == 0) 0f else stepX / 2f)
        val y = r * stepY
        val type = (r * 7 + c * 13) % 4

        when (type) {
          0 -> {
            // Chat bubble doodle
            drawCircle(
              color = doodleColor,
              radius = 16f,
              center = Offset(x, y),
              style = Stroke(width = 2.5f)
            )
            drawCircle(
              color = doodleColor,
              radius = 4f,
              center = Offset(x + 16f, y + 16f)
            )
          }
          1 -> {
            // Musical note / message doodle
            val path = Path().apply {
              moveTo(x - 12f, y - 10f)
              lineTo(x + 12f, y - 10f)
              lineTo(x + 12f, y + 10f)
              lineTo(x - 4f, y + 10f)
              lineTo(x - 12f, y + 18f)
              close()
            }
            drawPath(path = path, color = doodleColor, style = Stroke(width = 2.5f))
          }
          2 -> {
            // Coffee cup doodle
            drawArc(
              color = doodleColor,
              startAngle = 0f,
              sweepAngle = 180f,
              useCenter = false,
              topLeft = Offset(x - 12f, y - 8f),
              size = androidx.compose.ui.geometry.Size(24f, 20f),
              style = Stroke(width = 2.5f)
            )
            drawLine(
              color = doodleColor,
              start = Offset(x - 14f, y - 8f),
              end = Offset(x + 14f, y - 8f),
              strokeWidth = 2.5f
            )
          }
          3 -> {
            // Heart doodle
            drawCircle(
              color = doodleColor,
              radius = 10f,
              center = Offset(x, y),
              style = Stroke(width = 2f)
            )
          }
        }
      }
    }
  }
}
