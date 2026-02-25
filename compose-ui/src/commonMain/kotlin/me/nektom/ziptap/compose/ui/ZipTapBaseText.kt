package me.nektom.ziptap.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

@Composable
fun ZipTapBaseText(
    modifier: Modifier = Modifier,
    content: (@TextComposable @Composable () -> Unit),
) {
    val textMeasurer = rememberTextMeasurer()
    val context = rememberCompositionContext()
    val render = remember { ZipTapEditorRender() }
    val composition = remember(content) {
        val cur = render.composition
        val next =
            if (cur == null || cur.isDisposed) Composition(ZipTapApplier(render.treeRoot), context)
            else cur

        next.setContent { content() }
        next
    }

    render.composition = composition

    Layout(
        modifier = Modifier
            .drawBehind {
                render.run { onDraw() }
            }
            .then(modifier)
    ) { measurables, constraints ->
        this.density
        val result = render.run { measure(textMeasurer, constraints) }
        layout(0, result.height) {}
    }
}

class ZipTapEditorRender {
    var composition: Composition? = null
    val treeRoot = BlockNode()
    var textLayoutResult: TextLayout? = null


    fun DrawScope.onDraw() {
        val layout = textLayoutResult ?: return

        drawBlock(layout.rootResult)
    }

    fun DrawScope.drawBlock(blockResult: TextLayout.BlockResult) {
        var y = 0f
        blockResult.children.forEachIndexed { index, result ->
            when (result) {
                is TextLayout.BlockResult -> {
                    translate(top = y) {
                        drawBlock(result)
                    }
                    y += result.height
                }

                is TextLayout.TextResult -> {
                    translate(top = y) {
                        drawText(result)
                    }
                    with(result.textLayoutResult) {
                        val nextResult = blockResult.children.getOrNull(index + 1)
                            ?: return@forEachIndexed

                        if (lineCount > 1 && nextResult is TextLayout.TextResult) {
                            y += getLineTop(lineCount - 1)
                        }
                        if (nextResult is TextLayout.BlockResult) {
                            y += size.height
                        }
                    }
                }
            }
        }
    }

    fun DrawScope.drawText(textResult: TextLayout.TextResult) {
        drawText(textResult.textLayoutResult)
    }


    fun MeasureScope.measure(textMeasurer: TextMeasurer, constraints: Constraints): TextLayout {
        val blockResult = measureBlock(textMeasurer, constraints, treeRoot)
        val result = TextLayout(blockResult)
        textLayoutResult = result
        return result
    }

    fun MeasureScope.measureBlock(
        textMeasurer: TextMeasurer,
        constraints: Constraints,
        blockNode: BlockNode
    ): TextLayout.BlockResult {
        var indent = 0
        val results = blockNode.children.map {
            when (it) {
                is BlockNode -> {
                    indent = 0
                    measureBlock(textMeasurer, constraints, it)
                }

                is TextNode -> {
                    val result = measureText(textMeasurer, constraints, it, indent)

                    with(result.textLayoutResult) {
                        if (lineCount > 0) {
                            indent = getLineRight(lineCount - 1).roundToInt()
                        }
                    }

                    result
                }
            }
        }

        return TextLayout.BlockResult(results)
    }

    fun MeasureScope.measureText(
        textMeasurer: TextMeasurer,
        constraints: Constraints,
        textNode: TextNode,
        indent: Int
    ): TextLayout.TextResult {
        val textLayoutResult = textMeasurer.measure(
            textNode.content,
            style = TextStyle(textIndent = TextIndent(firstLine = indent.toSp())),
            constraints = constraints
        )
        return TextLayout.TextResult(
            textLayoutResult = textLayoutResult,
        )
    }
}

class TextLayout(
    val rootResult: BlockResult
) {
    val height = rootResult.height

    sealed class LayoutResult {
        abstract val height: Int
    }

    class BlockResult(
        val children: List<LayoutResult>
    ) : LayoutResult() {
        override val height: Int = children
            .mapIndexed { index, result ->
                if (result is TextResult) {
                    val prev = children.getOrNull(index - 1)
                    if (prev is TextResult) 0
                    else result.height
                } else {
                    result.height
                }

            }
            .sum()
    }

    class TextResult(
        val textLayoutResult: TextLayoutResult
    ) : LayoutResult() {
        override val height = textLayoutResult.size.height
    }
}