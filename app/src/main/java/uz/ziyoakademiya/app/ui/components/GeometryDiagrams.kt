package uz.ziyoakademiya.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.ziyoakademiya.app.core.theme.GoldAccent
import uz.ziyoakademiya.app.core.theme.NavyDark
import uz.ziyoakademiya.app.core.theme.TurquoiseAccent

/**
 * Geometric diagrams rendered dynamically via Compose Canvas.
 * Provides clear schematics with labeled vertices, lengths, angles, and heights.
 */
@Composable
fun TopicGeometryDiagramCard(
    topicId: String,
    modifier: Modifier = Modifier
) {
    val diagramType = when (topicId) {
        "topic_triangles" -> GeometryDiagramType.RIGHT_TRIANGLE
        "topic_geometry_plane" -> GeometryDiagramType.PARALLEL_LINES
        "topic_quadrilaterals" -> GeometryDiagramType.TRAPEZOID
        "topic_circles" -> GeometryDiagramType.CIRCLE
        "topic_prisms" -> GeometryDiagramType.RECTANGULAR_PRISM
        "topic_polyhedra_pyramids" -> GeometryDiagramType.PYRAMID
        "topic_solids_other" -> GeometryDiagramType.CYLINDER_AND_CONE
        else -> null
    } ?: return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Geometrik chizma va sxema",
                    style = MaterialTheme.typography.titleSmall,
                    color = TurquoiseAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Masshtab: shartli",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .border(1.dp, TurquoiseAccent.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    when (diagramType) {
                        GeometryDiagramType.RIGHT_TRIANGLE -> drawLabeledRightTriangle()
                        GeometryDiagramType.PARALLEL_LINES -> drawParallelLinesAndAngles()
                        GeometryDiagramType.TRAPEZOID -> drawLabeledTrapezoid()
                        GeometryDiagramType.CIRCLE -> drawLabeledCircle()
                        GeometryDiagramType.RECTANGULAR_PRISM -> drawLabeledPrism()
                        GeometryDiagramType.PYRAMID -> drawLabeledPyramid()
                        GeometryDiagramType.CYLINDER_AND_CONE -> drawLabeledCylinder()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getDiagramCaption(diagramType),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class GeometryDiagramType {
    RIGHT_TRIANGLE,
    PARALLEL_LINES,
    TRAPEZOID,
    CIRCLE,
    RECTANGULAR_PRISM,
    PYRAMID,
    CYLINDER_AND_CONE
}

private fun getDiagramCaption(type: GeometryDiagramType): String {
    return when (type) {
        GeometryDiagramType.RIGHT_TRIANGLE -> "To'g'ri burchakli uchburchak: a, b - katetlar, c - gipotenuza, c² = a² + b²"
        GeometryDiagramType.PARALLEL_LINES -> "Parallel to'g'ri chiziqlar va kesuvchi: ichki almashinuvchi va mos burchaklar teng"
        GeometryDiagramType.TRAPEZOID -> "Trapetsiya: a, b - asoslar, h - balandlik, S = (a + b) / 2 * h"
        GeometryDiagramType.CIRCLE -> "Aylana va doira: r - radius, O - markaz, C = 2πr, S = πr²"
        GeometryDiagramType.RECTANGULAR_PRISM -> "Parallelepiped: a, b, c - o'lchamlar, V = a*b*c, S = 2(ab + bc + ac)"
        GeometryDiagramType.PYRAMID -> "Muntazam piramida: h - balandlik, l - apofema, V = (1/3) * S_asos * h"
        GeometryDiagramType.CYLINDER_AND_CONE -> "Aylanish jismi (Silindr): r - radius, h - balandlik, V = πr²h"
    }
}

private fun DrawScope.drawLabeledRightTriangle() {
    val strokeColor = TurquoiseAccent
    val accentColor = GoldAccent
    val strokeWidth = 3.dp.toPx()

    val pA = Offset(40f, size.height - 30f)
    val pB = Offset(size.width - 50f, size.height - 30f)
    val pC = Offset(40f, 30f)

    // Triangle path
    val path = Path().apply {
        moveTo(pA.x, pA.y)
        lineTo(pB.x, pB.y)
        lineTo(pC.x, pC.y)
        close()
    }
    drawPath(path, color = strokeColor, style = Stroke(width = strokeWidth))

    // Right angle square marker at A
    val sqSize = 20f
    val rightAnglePath = Path().apply {
        moveTo(pA.x, pA.y - sqSize)
        lineTo(pA.x + sqSize, pA.y - sqSize)
        lineTo(pA.x + sqSize, pA.y)
    }
    drawPath(rightAnglePath, color = accentColor, style = Stroke(width = 2.dp.toPx()))

    // Labels
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            isAntiAlias = true
            isFakeBoldText = true
        }
        drawText("C", pC.x - 25f, pC.y + 10f, paint)
        drawText("A", pA.x - 25f, pA.y + 10f, paint)
        drawText("B", pB.x + 10f, pB.y + 10f, paint)

        val legPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.CYAN
            textSize = 26f
            isAntiAlias = true
        }
        drawText("b (katet)", pA.x - 15f, (pA.y + pC.y) / 2, legPaint)
        drawText("a (katet)", (pA.x + pB.x) / 2 - 40f, pA.y + 25f, legPaint)
        drawText("c (gipotenuza)", (pC.x + pB.x) / 2 + 10f, (pC.y + pB.y) / 2 - 10f, legPaint)
    }
}

private fun DrawScope.drawParallelLinesAndAngles() {
    val strokeColor = TurquoiseAccent
    val strokeWidth = 3.dp.toPx()

    val y1 = size.height * 0.3f
    val y2 = size.height * 0.7f

    // Line 1
    drawLine(strokeColor, Offset(20f, y1), Offset(size.width - 20f, y1), strokeWidth)
    // Line 2
    drawLine(strokeColor, Offset(20f, y2), Offset(size.width - 20f, y2), strokeWidth)

    // Transversal line
    val tPath = Path().apply {
        moveTo(size.width * 0.25f, 20f)
        lineTo(size.width * 0.75f, size.height - 20f)
    }
    drawPath(tPath, color = GoldAccent, style = Stroke(width = strokeWidth))

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 26f
            isAntiAlias = true
        }
        drawText("L1", size.width - 60f, y1 - 10f, paint)
        drawText("L2", size.width - 60f, y2 - 10f, paint)
        drawText("α", size.width * 0.42f, y1 - 10f, paint)
        drawText("β", size.width * 0.58f, y2 + 30f, paint)
    }
}

private fun DrawScope.drawLabeledTrapezoid() {
    val strokeColor = TurquoiseAccent
    val strokeWidth = 3.dp.toPx()

    val topY = 40f
    val botY = size.height - 30f

    val p1 = Offset(size.width * 0.3f, topY)
    val p2 = Offset(size.width * 0.7f, topY)
    val p3 = Offset(size.width * 0.9f, botY)
    val p4 = Offset(size.width * 0.1f, botY)

    val path = Path().apply {
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        lineTo(p4.x, p4.y)
        close()
    }
    drawPath(path, color = strokeColor, style = Stroke(width = strokeWidth))

    // Height dashed line
    val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    drawLine(
        color = GoldAccent,
        start = p1,
        end = Offset(p1.x, botY),
        strokeWidth = 2.dp.toPx(),
        pathEffect = dashedEffect
    )

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 26f
            isAntiAlias = true
        }
        drawText("b (kichik asos)", size.width * 0.35f, topY - 12f, paint)
        drawText("a (katta asos)", size.width * 0.4f, botY + 25f, paint)
        drawText("h", p1.x + 8f, (topY + botY) / 2, paint)
    }
}

private fun DrawScope.drawLabeledCircle() {
    val strokeColor = TurquoiseAccent
    val strokeWidth = 3.dp.toPx()

    val center = Offset(size.width * 0.45f, size.height * 0.5f)
    val radius = minOf(size.width, size.height) * 0.38f

    // Circle
    drawCircle(color = strokeColor, radius = radius, center = center, style = Stroke(strokeWidth))

    // Center point
    drawCircle(color = GoldAccent, radius = 5f, center = center)

    // Radius line
    val rEnd = Offset(center.x + radius, center.y)
    drawLine(color = GoldAccent, start = center, end = rEnd, strokeWidth = 2.dp.toPx())

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 26f
            isAntiAlias = true
        }
        drawText("O", center.x - 20f, center.y - 10f, paint)
        drawText("r", center.x + radius * 0.45f, center.y - 10f, paint)
        drawText("S = πr²", size.width * 0.72f, size.height * 0.45f, paint)
        drawText("C = 2πr", size.width * 0.72f, size.height * 0.65f, paint)
    }
}

private fun DrawScope.drawLabeledPrism() {
    val strokeColor = TurquoiseAccent
    val strokeWidth = 3.dp.toPx()

    val w = size.width * 0.55f
    val h = size.height * 0.5f
    val d = 40f

    val x0 = size.width * 0.15f
    val y0 = size.height * 0.4f

    // Front rectangle
    drawRect(
        color = strokeColor,
        topLeft = Offset(x0, y0),
        size = Size(w, h),
        style = Stroke(strokeWidth)
    )

    // Back top edges
    drawLine(strokeColor, Offset(x0, y0), Offset(x0 + d, y0 - d), strokeWidth)
    drawLine(strokeColor, Offset(x0 + w, y0), Offset(x0 + w + d, y0 - d), strokeWidth)
    drawLine(strokeColor, Offset(x0 + w, y0 + h), Offset(x0 + w + d, y0 + h - d), strokeWidth)
    drawLine(strokeColor, Offset(x0 + d, y0 - d), Offset(x0 + w + d, y0 - d), strokeWidth)
    drawLine(strokeColor, Offset(x0 + w + d, y0 - d), Offset(x0 + w + d, y0 + h - d), strokeWidth)

    // Hidden edges (dashed)
    val dashed = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
    drawLine(strokeColor.copy(alpha = 0.5f), Offset(x0, y0 + h), Offset(x0 + d, y0 + h - d), strokeWidth, pathEffect = dashed)
    drawLine(strokeColor.copy(alpha = 0.5f), Offset(x0 + d, y0 + h - d), Offset(x0 + w + d, y0 + h - d), strokeWidth, pathEffect = dashed)
    drawLine(strokeColor.copy(alpha = 0.5f), Offset(x0 + d, y0 - d), Offset(x0 + d, y0 + h - d), strokeWidth, pathEffect = dashed)

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 26f
            isAntiAlias = true
        }
        drawText("a", x0 + w / 2 - 10f, y0 + h + 25f, paint)
        drawText("b (balandlik)", x0 - 30f, y0 + h / 2, paint)
        drawText("c", x0 + w + d / 2 + 5f, y0 + h / 2, paint)
    }
}

private fun DrawScope.drawLabeledPyramid() {
    val strokeColor = TurquoiseAccent
    val strokeWidth = 3.dp.toPx()

    val apex = Offset(size.width * 0.5f, 25f)
    val p1 = Offset(size.width * 0.2f, size.height * 0.8f)
    val p2 = Offset(size.width * 0.6f, size.height * 0.85f)
    val p3 = Offset(size.width * 0.85f, size.height * 0.75f)
    val p4 = Offset(size.width * 0.45f, size.height * 0.7f)

    // Base visible
    drawLine(strokeColor, p1, p2, strokeWidth)
    drawLine(strokeColor, p2, p3, strokeWidth)
    // Edges visible
    drawLine(strokeColor, apex, p1, strokeWidth)
    drawLine(strokeColor, apex, p2, strokeWidth)
    drawLine(strokeColor, apex, p3, strokeWidth)

    // Hidden edges (dashed)
    val dashed = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
    drawLine(strokeColor.copy(alpha = 0.5f), p1, p4, strokeWidth, pathEffect = dashed)
    drawLine(strokeColor.copy(alpha = 0.5f), p3, p4, strokeWidth, pathEffect = dashed)
    drawLine(strokeColor.copy(alpha = 0.5f), apex, p4, strokeWidth, pathEffect = dashed)

    // Height line from apex to base center
    val baseCenter = Offset(size.width * 0.52f, size.height * 0.78f)
    drawLine(GoldAccent, apex, baseCenter, 2.dp.toPx(), pathEffect = dashed)

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 26f
            isAntiAlias = true
        }
        drawText("S (uch)", apex.x - 15f, apex.y - 8f, paint)
        drawText("h", apex.x + 8f, (apex.y + baseCenter.y) / 2, paint)
        drawText("V = 1/3 * S_asos * h", size.width * 0.4f, size.height - 10f, paint)
    }
}

private fun DrawScope.drawLabeledCylinder() {
    val strokeColor = TurquoiseAccent
    val strokeWidth = 3.dp.toPx()

    val cx = size.width * 0.5f
    val rx = size.width * 0.28f
    val ry = 22f
    val topY = 45f
    val botY = size.height - 45f

    // Top ellipse
    drawOval(strokeColor, Offset(cx - rx, topY - ry), Size(rx * 2, ry * 2), style = Stroke(strokeWidth))

    // Sides
    drawLine(strokeColor, Offset(cx - rx, topY), Offset(cx - rx, botY), strokeWidth)
    drawLine(strokeColor, Offset(cx + rx, topY), Offset(cx + rx, botY), strokeWidth)

    // Bottom half ellipse (visible)
    val arcPath = Path().apply {
        arcTo(androidx.compose.ui.geometry.Rect(cx - rx, botY - ry, cx + rx, botY + ry), 0f, 180f, false)
    }
    drawPath(arcPath, strokeColor, style = Stroke(strokeWidth))

    // Bottom back half ellipse (dashed)
    val dashed = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
    val backArcPath = Path().apply {
        arcTo(androidx.compose.ui.geometry.Rect(cx - rx, botY - ry, cx + rx, botY + ry), 180f, 180f, false)
    }
    drawPath(backArcPath, strokeColor.copy(alpha = 0.5f), style = Stroke(strokeWidth, pathEffect = dashed))

    // Radius line at top
    drawLine(GoldAccent, Offset(cx, topY), Offset(cx + rx, topY), 2.dp.toPx())

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 26f
            isAntiAlias = true
        }
        drawText("r", cx + rx / 2 - 5f, topY - 8f, paint)
        drawText("h", cx + rx + 12f, (topY + botY) / 2, paint)
    }
}
