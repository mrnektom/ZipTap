package me.nektom.ziptap.compose.ui

import androidx.compose.runtime.AbstractApplier

class ZipTapApplier(root: ZipTapComposeUiNode) : AbstractApplier<ZipTapComposeUiNode>(root) {
    override fun insertTopDown(
        index: Int,
        instance: ZipTapComposeUiNode
    ) {
        current.children.add(index, instance)
    }

    override fun insertBottomUp(
        index: Int,
        instance: ZipTapComposeUiNode
    ) {}

    override fun remove(index: Int, count: Int) {
        current.children.remove(index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.children.move(from, to, count)
    }

    override fun onClear() {
        root.children.clear()
    }
}