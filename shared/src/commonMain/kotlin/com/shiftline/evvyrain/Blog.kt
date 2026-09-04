@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package com.shiftline.evvyrain

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import evvyrain.shared.generated.resources.Res
import evvyrain.shared.generated.resources.close
import evvyrain.shared.generated.resources.open_in_new

internal data class BlogPost(
    val slug: String,
    val title: String,
    val date: String,
    val summary: String,
    val banner: String?,
    val tags: List<String>,
    val markdown: String,
)

private sealed interface BlogState {
    data object Loading : BlogState
    data class Ready(val posts: List<BlogPost>) : BlogState
    data object Empty : BlogState
}

@Composable
internal fun BlogScene(
    wide: Boolean,
    loadTextAsset: suspend (String) -> String?,
    loadBinaryAsset: suspend (String) -> ByteArray?,
    openPost: (BlogPost) -> Unit,
) {
    val state by produceState<BlogState>(BlogState.Loading, loadTextAsset) {
        val entries = loadTextAsset("/blog/index.txt")
            ?.lineSequence()
            ?.map(String::trim)
            ?.filter { it.isNotEmpty() && !it.startsWith("#") }
            ?.toList()
            .orEmpty()
        val posts = entries.mapNotNull { entry ->
            loadTextAsset("/blog/posts/$entry")?.let { parsePost(entry.substringBeforeLast('.'), it) }
        }
        value = if (posts.isEmpty()) BlogState.Empty else BlogState.Ready(posts)
    }

    SceneHeading("Notes from the workbench.", "Thoughts on interfaces, code, tools, and the small details worth writing down.")
    when (val current = state) {
        BlogState.Loading -> BlogSkeleton(wide)
        BlogState.Empty -> EmptyBlog()
        is BlogState.Ready -> {
            if (wide) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    current.posts.chunked((current.posts.size + 1) / 2).forEach { column ->
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            column.forEachIndexed { index, post -> BlogCard(post, index, loadBinaryAsset, openPost) }
                        }
                    }
                }
            } else {
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    current.posts.forEachIndexed { index, post -> BlogCard(post, index, loadBinaryAsset, openPost) }
                }
            }
        }
    }
}

@Composable
private fun BlogCard(
    post: BlogPost,
    index: Int,
    loadBinaryAsset: suspend (String) -> ByteArray?,
    openPost: (BlogPost) -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    val colors = listOf(scheme.primaryContainer, scheme.tertiaryContainer, scheme.secondaryContainer)
    Surface(
        onClick = { openPost(post) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = colors[index % colors.size],
    ) {
        Column {
            BlogBanner(post.banner, post.title, loadBinaryAsset, Modifier.fillMaxWidth().height(210.dp), index)
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(post.date.uppercase(), style = MaterialTheme.typography.labelMedium, color = scheme.onSurfaceVariant)
                    Spacer(Modifier.weight(1f))
                    post.tags.take(2).forEach { tag ->
                        AssistChip(onClick = { openPost(post) }, label = { Text(tag) })
                        Spacer(Modifier.width(6.dp))
                    }
                }
                Text(post.title, style = MaterialTheme.typography.headlineMedium)
                Text(post.summary, style = MaterialTheme.typography.bodyLarge, color = scheme.onSurfaceVariant, maxLines = 3)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Read in sheet", style = MaterialTheme.typography.labelLarge, color = scheme.primary)
                    Spacer(Modifier.width(8.dp))
                    AppIcon(Res.drawable.open_in_new, "Open post", Modifier.size(19.dp))
                }
            }
        }
    }
}

@Composable
private fun BlogBanner(
    path: String?,
    description: String,
    loadBinaryAsset: suspend (String) -> ByteArray?,
    modifier: Modifier,
    seed: Int = 0,
) {
    val image by produceState<ImageBitmap?>(null, path) {
        value = path?.let { runCatching { loadBinaryAsset(it)?.decodeToImageBitmap() }.getOrNull() }
    }
    Box(modifier.clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(MaterialTheme.colorScheme.surfaceContainerHigh)) {
        image?.let {
            Image(it, description, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val shape = listOf(MaterialShapes.Cookie7Sided, MaterialShapes.Flower, MaterialShapes.PuffyDiamond)[seed % 3].toShape()
            Box(Modifier.size(130.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = .18f), shape))
            Text("ER", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun BlogSkeleton(wide: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
        repeat(if (wide) 2 else 1) {
            Column(Modifier.weight(1f).clip(RoundedCornerShape(32.dp)).background(MaterialTheme.colorScheme.surfaceContainer).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
                Box(Modifier.fillMaxWidth(.58f).height(24.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                Box(Modifier.fillMaxWidth().height(14.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                Box(Modifier.fillMaxWidth(.82f).height(14.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
            }
        }
    }
}

@Composable
private fun EmptyBlog() {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp), color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(Modifier.padding(32.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Fresh page, for now.", style = MaterialTheme.typography.headlineMedium)
            Text("New notes will appear here soon.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BlogPostSheet(
    post: BlogPost,
    wide: Boolean,
    loadBinaryAsset: suspend (String) -> ByteArray?,
    openUrl: (String) -> Unit,
    dismiss: () -> Unit,
) {
    if (wide) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInHorizontally { it / 2 },
            exit = fadeOut() + slideOutHorizontally { it },
        ) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .28f)).clickable(onClick = dismiss)) {
                Surface(
                    Modifier.align(Alignment.CenterEnd).fillMaxHeight().widthIn(min = 520.dp, max = 680.dp).fillMaxWidth(.52f).clickable(enabled = false) {},
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 36.dp, bottomStart = 36.dp),
                ) { PostContent(post, loadBinaryAsset, openUrl, dismiss, Modifier.fillMaxSize()) }
            }
        }
    } else {
        ModalBottomSheet(
            onDismissRequest = dismiss,
            modifier = Modifier.fillMaxHeight(.96f),
            sheetState = rememberBottomSheetState(
                initialValue = SheetValue.Hidden,
                enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
            ),
        ) {
            PostContent(post, loadBinaryAsset, openUrl, dismiss, Modifier.fillMaxWidth().fillMaxHeight())
        }
    }
}

@Composable
private fun PostContent(
    post: BlogPost,
    loadBinaryAsset: suspend (String) -> ByteArray?,
    openUrl: (String) -> Unit,
    dismiss: () -> Unit,
    modifier: Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState()).padding(bottom = 48.dp)) {
        Box {
            BlogBanner(post.banner, post.title, loadBinaryAsset, Modifier.fillMaxWidth().height(300.dp))
            FilledTonalIconButton(dismiss, Modifier.align(Alignment.TopEnd).padding(18.dp)) { AppIcon(Res.drawable.close, "Close post") }
        }
        Column(Modifier.padding(horizontal = 28.dp, vertical = 26.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(post.date.uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(post.title, style = MaterialTheme.typography.displaySmall)
            if (post.tags.isNotEmpty()) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { post.tags.forEach { SuggestionChip({}, { Text(it) }) } }
            HorizontalDivider()
            MarkdownBody(post.markdown, openUrl)
        }
    }
}

private sealed interface MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class Paragraph(val text: String) : MarkdownBlock
    data class Bullet(val text: String) : MarkdownBlock
    data class Quote(val text: String) : MarkdownBlock
    data class Code(val text: String) : MarkdownBlock
    data object Divider : MarkdownBlock
}

@Composable
private fun MarkdownBody(markdown: String, openUrl: (String) -> Unit) {
    val blocks = remember(markdown) { parseMarkdown(markdown) }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> InlineMarkdown(block.text, when (block.level) { 1 -> MaterialTheme.typography.headlineLarge; 2 -> MaterialTheme.typography.headlineMedium; else -> MaterialTheme.typography.titleLarge }, openUrl)
                is MarkdownBlock.Paragraph -> InlineMarkdown(block.text, MaterialTheme.typography.bodyLarge, openUrl)
                is MarkdownBlock.Bullet -> Row { Text("•", Modifier.width(24.dp), style = MaterialTheme.typography.bodyLarge); Box(Modifier.weight(1f)) { InlineMarkdown(block.text, MaterialTheme.typography.bodyLarge, openUrl) } }
                is MarkdownBlock.Quote -> Surface(shape = RoundedCornerShape(0.dp, 20.dp, 20.dp, 0.dp), color = MaterialTheme.colorScheme.secondaryContainer) { InlineMarkdown(block.text, MaterialTheme.typography.bodyLarge, openUrl, Modifier.padding(18.dp)) }
                is MarkdownBlock.Code -> Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.inverseSurface) { Text(block.text, Modifier.fillMaxWidth().padding(18.dp), color = MaterialTheme.colorScheme.inverseOnSurface, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodyMedium) }
                MarkdownBlock.Divider -> HorizontalDivider()
            }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
private fun InlineMarkdown(text: String, style: androidx.compose.ui.text.TextStyle, openUrl: (String) -> Unit, modifier: Modifier = Modifier) {
    val annotated = remember(text) { inlineMarkdown(text) }
    ClickableText(
        text = annotated,
        modifier = modifier,
        style = style.copy(color = MaterialTheme.colorScheme.onSurface),
        onClick = { offset -> annotated.getStringAnnotations("URL", offset, offset).firstOrNull()?.let { openUrl(it.item) } },
    )
}

private fun inlineMarkdown(source: String): AnnotatedString = buildAnnotatedString {
    val token = Regex("(\\*\\*[^*]+\\*\\*|`[^`]+`|\\[[^]]+]\\([^)]+\\))")
    var cursor = 0
    token.findAll(source).forEach { match ->
        append(source.substring(cursor, match.range.first))
        val value = match.value
        when {
            value.startsWith("**") -> pushStyle(SpanStyle(fontWeight = FontWeight.Bold)).also { append(value.removePrefix("**").removeSuffix("**")); pop() }
            value.startsWith('`') -> pushStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x1A904B3D))).also { append(value.trim('`')); pop() }
            else -> {
                val label = value.substringAfter('[').substringBefore(']')
                val url = value.substringAfter("](").substringBeforeLast(')')
                pushStringAnnotation("URL", url)
                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold))
                append(label)
                pop(); pop()
            }
        }
        cursor = match.range.last + 1
    }
    append(source.substring(cursor))
}

private fun parsePost(slug: String, raw: String): BlogPost? {
    val normalized = raw.replace("\r\n", "\n")
    if (!normalized.startsWith("---\n")) return null
    val end = normalized.indexOf("\n---\n", 4)
    if (end < 0) return null
    val metadata = normalized.substring(4, end).lineSequence().mapNotNull { line ->
        val divider = line.indexOf(':')
        if (divider < 1) null else line.substring(0, divider).trim() to line.substring(divider + 1).trim().trim('"')
    }.toMap()
    val title = metadata["title"]?.takeIf(String::isNotBlank) ?: return null
    val markdown = normalized.substring(end + 5).trim()
    val summary = metadata["summary"]?.takeIf(String::isNotBlank)
        ?: markdown.lineSequence().firstOrNull { it.isNotBlank() && !it.startsWith('#') }.orEmpty().take(180)
    val banner = metadata["banner"]?.takeIf(String::isNotBlank)?.let { if (it.startsWith('/')) it else "/blog/$it" }
    return BlogPost(slug, title, metadata["date"].orEmpty(), summary, banner, metadata["tags"]?.split(',')?.map(String::trim)?.filter(String::isNotBlank).orEmpty(), markdown)
}

private fun parseMarkdown(markdown: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val paragraph = mutableListOf<String>()
    val code = mutableListOf<String>()
    var inCode = false
    fun flushParagraph() {
        if (paragraph.isNotEmpty()) { blocks += MarkdownBlock.Paragraph(paragraph.joinToString(" ")); paragraph.clear() }
    }
    markdown.lineSequence().forEach { rawLine ->
        val line = rawLine.trimEnd()
        if (line.startsWith("```")) {
            flushParagraph()
            if (inCode) { blocks += MarkdownBlock.Code(code.joinToString("\n")); code.clear() }
            inCode = !inCode
        } else if (inCode) code += rawLine
        else when {
            line.isBlank() -> flushParagraph()
            line == "---" -> { flushParagraph(); blocks += MarkdownBlock.Divider }
            line.startsWith("### ") -> { flushParagraph(); blocks += MarkdownBlock.Heading(3, line.drop(4)) }
            line.startsWith("## ") -> { flushParagraph(); blocks += MarkdownBlock.Heading(2, line.drop(3)) }
            line.startsWith("# ") -> { flushParagraph(); blocks += MarkdownBlock.Heading(1, line.drop(2)) }
            line.startsWith("- ") || line.startsWith("* ") -> { flushParagraph(); blocks += MarkdownBlock.Bullet(line.drop(2)) }
            line.startsWith("> ") -> { flushParagraph(); blocks += MarkdownBlock.Quote(line.drop(2)) }
            else -> paragraph += line.trim()
        }
    }
    flushParagraph()
    if (code.isNotEmpty()) blocks += MarkdownBlock.Code(code.joinToString("\n"))
    return blocks
}
