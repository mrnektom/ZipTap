package me.nektom.ziptap.core

interface ZipTapNode {
    val name: String
}

interface ZipTapBlock : ZipTapNode {
    val topLevel: Boolean get() = false
    val content: List<ZipTapNode>
}

interface ZipTapInline : ZipTapNode {
    val atom get() = false
}