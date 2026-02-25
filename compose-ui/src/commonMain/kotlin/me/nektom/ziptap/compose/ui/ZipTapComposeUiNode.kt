package me.nektom.ziptap.compose.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.ui.graphics.drawscope.DrawScope

sealed class ZipTapComposeUiNode {
    val children = mutableListOf<ZipTapComposeUiNode>()
}

class TextNode : ZipTapComposeUiNode() {
    var content: String = ""
}

class BlockNode : ZipTapComposeUiNode()

@Composable
@TextComposable
fun PlainText(content: String) {
    ComposeNode<TextNode, ZipTapApplier>(::TextNode) {
        set(content) { this.content = it }
    }
}

@Composable
@TextComposable
fun Block(
    content: @Composable @TextComposable () -> Unit
) {
    ComposeNode<BlockNode, ZipTapApplier>(::BlockNode, {}, content)
}