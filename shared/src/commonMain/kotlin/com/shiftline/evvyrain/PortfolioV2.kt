@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.shiftline.evvyrain

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import evvyrain.shared.generated.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import kotlin.math.sqrt
import kotlin.math.roundToInt

private val PortfolioLightScheme = lightColorScheme(
    primary = Color(0xFF904B3D), onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD3), onPrimaryContainer = Color(0xFF733427),
    secondary = Color(0xFF775750), onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD3), onSecondaryContainer = Color(0xFF5D3F39),
    tertiary = Color(0xFF6E5C2E), onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF9E0A6), onTertiaryContainer = Color(0xFF554519),
    error = Color(0xFFBA1A1A), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF93000A),
    background = Color(0xFFFFF8F6), onBackground = Color(0xFF231917),
    surface = Color(0xFFFFF8F6), onSurface = Color(0xFF231917),
    surfaceVariant = Color(0xFFF5DDD9), onSurfaceVariant = Color(0xFF534340),
    outline = Color(0xFF85736F), outlineVariant = Color(0xFFD8C2BD),
    scrim = Color.Black,
    inverseSurface = Color(0xFF392E2C), inverseOnSurface = Color(0xFFFFEDE9), inversePrimary = Color(0xFFFFB4A5),
    surfaceDim = Color(0xFFE8D6D3), surfaceBright = Color(0xFFFFF8F6),
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color(0xFFFFF0EE),
    surfaceContainer = Color(0xFFFCEAE6), surfaceContainerHigh = Color(0xFFF7E4E1), surfaceContainerHighest = Color(0xFFF1DFDB),
)

private val PortfolioDarkScheme = darkColorScheme(
    primary = Color(0xFFFFB4A5), onPrimary = Color(0xFF561F13),
    primaryContainer = Color(0xFF733427), onPrimaryContainer = Color(0xFFFFDAD3),
    secondary = Color(0xFFE7BDB4), onSecondary = Color(0xFF442A24),
    secondaryContainer = Color(0xFF5D3F39), onSecondaryContainer = Color(0xFFFFDAD3),
    tertiary = Color(0xFFDCC48C), onTertiary = Color(0xFF3D2F04),
    tertiaryContainer = Color(0xFF554519), onTertiaryContainer = Color(0xFFF9E0A6),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1110), onBackground = Color(0xFFF1DFDB),
    surface = Color(0xFF1A1110), onSurface = Color(0xFFF1DFDB),
    surfaceVariant = Color(0xFF534340), onSurfaceVariant = Color(0xFFD8C2BD),
    outline = Color(0xFFA08C88), outlineVariant = Color(0xFF534340),
    scrim = Color.Black,
    inverseSurface = Color(0xFFF1DFDB), inverseOnSurface = Color(0xFF392E2C), inversePrimary = Color(0xFF904B3D),
    surfaceDim = Color(0xFF1A1110), surfaceBright = Color(0xFF423734),
    surfaceContainerLowest = Color(0xFF140C0A), surfaceContainerLow = Color(0xFF231917),
    surfaceContainer = Color(0xFF271D1B), surfaceContainerHigh = Color(0xFF322826), surfaceContainerHighest = Color(0xFF3D3230),
)

@Composable private fun portfolioTypography(): Typography {
    val nunito = FontFamily(Font(Res.font.nunito, weight = FontWeight.Normal), Font(Res.font.nunito, weight = FontWeight.Bold))
    val bagel = FontFamily(Font(Res.font.bagel_fat_one))
    val baseline = Typography()
    return baseline.copy(
        displayLarge = baseline.displayLarge.copy(fontFamily = bagel),
        displayMedium = baseline.displayMedium.copy(fontFamily = bagel),
        displaySmall = baseline.displaySmall.copy(fontFamily = bagel),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = bagel),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = bagel),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = bagel),
        titleLarge = baseline.titleLarge.copy(fontFamily = bagel),
        titleMedium = baseline.titleMedium.copy(fontFamily = bagel),
        titleSmall = baseline.titleSmall.copy(fontFamily = bagel),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = nunito),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = nunito),
        bodySmall = baseline.bodySmall.copy(fontFamily = nunito),
        labelLarge = baseline.labelLarge.copy(fontFamily = nunito),
        labelMedium = baseline.labelMedium.copy(fontFamily = nunito),
        labelSmall = baseline.labelSmall.copy(fontFamily = nunito),
    )
}

private enum class StudioPage(val title: String) { Home("Hello"), Work("Work"), Stack("Stack"), Devices("Devices"), Blog("Blog") }
private data class Project(val name: String, val kicker: String, val body: String, val tech: String, val url: String)
private val projects = listOf(
    Project("Skye", "01 · TELEGRAM BOT", "A Python-powered Telegram bot, with a web companion currently in the works.", "PYTHON / TELEGRAM", "https://github.com/evvyraine/skye-next"),
    Project("Sunkit", "02 · TOOLKIT", "Bright utilities built to make the browser feel more personal.", "FRONTEND / MOTION", "https://github.com/evvyraine/sunkit"),
    Project("Ensage", "03 · SHARING WORKSPACE", "A security-first, self-hosted workspace for sharing text, files, and links — with a companion CLI.", "NEXT.JS / REACT / POSTGRESQL", "https://github.com/evvyraine/ensage-x"),
)

@Composable internal fun PortfolioAppV2(
    openUrl: (String) -> Unit,
    loadTextAsset: suspend (String) -> String?,
    loadBinaryAsset: suspend (String) -> ByteArray?,
) {
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
    var dark by remember { mutableStateOf(systemDark) }
    var ready by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(520); ready = true }
    MaterialExpressiveTheme(colorScheme = if (dark) PortfolioDarkScheme else PortfolioLightScheme, motionScheme = MotionScheme.expressive(), typography = portfolioTypography()) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            AnimatedContent(ready, transitionSpec = { (fadeIn() + scaleIn(initialScale = .97f)) togetherWith (fadeOut() + scaleOut(targetScale = 1.03f)) }, label = "load") {
                if (it) Studio(openUrl, loadTextAsset, loadBinaryAsset, dark) { dark = !dark } else ExpressiveLoader()
            }
        }
    }
}

@Composable private fun ExpressiveLoader() {
    val infinite = rememberInfiniteTransition(label = "loader")
    val turn by infinite.animateFloat(0f, 360f, androidx.compose.animation.core.infiniteRepeatable(androidx.compose.animation.core.tween(1200)), label = "turn")
    val pop by infinite.animateFloat(.78f, 1f, androidx.compose.animation.core.infiniteRepeatable(androidx.compose.animation.core.tween(560), androidx.compose.animation.core.RepeatMode.Reverse), label = "pop")
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
            Box(Modifier.size(112.dp).graphicsLayer { rotationZ = turn; scaleX = pop; scaleY = pop }.background(MaterialTheme.colorScheme.primaryContainer, MaterialShapes.Sunny.toShape()), contentAlignment = Alignment.Center) {
                Icon(painterResource(Res.drawable.code), null, Modifier.size(38.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            LoadingIndicator(Modifier.size(48.dp), color = MaterialTheme.colorScheme.primary)
            Text("Warming up the pixels", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { repeat(3) { i -> Box(Modifier.width((58 + i * 20).dp).height(12.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .5f + i * .14f))) } }
        }
    }
}

@Composable private fun Studio(
    openUrl: (String) -> Unit,
    loadTextAsset: suspend (String) -> String?,
    loadBinaryAsset: suspend (String) -> ByteArray?,
    dark: Boolean,
    toggleTheme: () -> Unit,
) {
    var page by remember { mutableStateOf(StudioPage.Home) }
    var socials by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<BlogPost?>(null) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 860.dp
        Row(Modifier.fillMaxSize()) {
            if (wide) StudioRail(page) { page = it }
            Crossfade(page, Modifier.weight(1f), animationSpec = spring(dampingRatio = .76f, stiffness = 240f), label = "studio page") { current ->
                StudioPage(current, wide, openUrl, loadTextAsset, loadBinaryAsset, { selectedPost = it }, dark, toggleTheme)
            }
        }
        if (!wide) StudioBar(page, { page = it }, Modifier.align(Alignment.BottomCenter))
        SocialFabMenu(socials, { socials = it }, openUrl, Modifier.align(Alignment.BottomEnd).padding(end = 18.dp, bottom = if (wide) 22.dp else 98.dp))
        selectedPost?.let { post ->
            BlogPostSheet(post, wide, loadBinaryAsset, openUrl) { selectedPost = null }
        }
    }
}

@Composable private fun StudioRail(page: StudioPage, select: (StudioPage) -> Unit) {
    NavigationRail(modifier = Modifier.width(96.dp).fillMaxHeight(), containerColor = MaterialTheme.colorScheme.surface) {
        Spacer(Modifier.height(20.dp))
        Image(
            painter = painterResource(Res.drawable.rail_avatar),
            contentDescription = "Evelyn Raine",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(58.dp).clip(MaterialShapes.Cookie4Sided.toShape()),
        )
        Spacer(Modifier.height(24.dp))
        StudioPage.entries.forEach { item -> NavigationRailItem(page == item, { select(item) }, { AppIcon(pageIconV2(item, page == item), item.title) }, label = { Text(item.title, style = MaterialTheme.typography.labelMedium) }, colors = NavigationRailItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)) }
    }
}
@Composable private fun StudioBar(page: StudioPage, select: (StudioPage) -> Unit, modifier: Modifier) {
    NavigationBar(modifier.fillMaxWidth().navigationBarsPadding(), containerColor = MaterialTheme.colorScheme.surface) {
        StudioPage.entries.forEach { item -> NavigationBarItem(page == item, { select(item) }, { AppIcon(pageIconV2(item, page == item), item.title) }, label = { Text(item.title, style = MaterialTheme.typography.labelMedium) }, colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer)) }
    }
}
@Composable private fun pageIconV2(page: StudioPage, selected: Boolean): DrawableResource = when (page) {
    StudioPage.Home -> if (selected) Res.drawable.home_filled else Res.drawable.home
    StudioPage.Work -> if (selected) Res.drawable.work_filled else Res.drawable.work
    StudioPage.Stack -> if (selected) Res.drawable.memory_filled else Res.drawable.memory
    StudioPage.Devices -> if (selected) Res.drawable.devices_filled else Res.drawable.devices
    StudioPage.Blog -> if (selected) Res.drawable.article_filled else Res.drawable.article
}

@Composable private fun StudioPage(
    page: StudioPage,
    wide: Boolean,
    openUrl: (String) -> Unit,
    loadTextAsset: suspend (String) -> String?,
    loadBinaryAsset: suspend (String) -> ByteArray?,
    openPost: (BlogPost) -> Unit,
    dark: Boolean,
    toggleTheme: () -> Unit,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = if (wide) 80.dp else 150.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 1200.dp).fillMaxWidth().padding(horizontal = if (wide) 42.dp else 18.dp, vertical = 22.dp), verticalArrangement = Arrangement.spacedBy(28.dp)) {
            StudioToolbar(page, dark, toggleTheme, openUrl)
            when (page) {
                StudioPage.Home -> HomeScene(wide)
                StudioPage.Work -> WorkScene(wide, openUrl)
                StudioPage.Stack -> StackScene(wide)
                StudioPage.Devices -> DeviceScene(wide)
                StudioPage.Blog -> BlogScene(wide, loadTextAsset, loadBinaryAsset, openPost)
            }
        }
    }
}

@Composable private fun StudioToolbar(page: StudioPage, dark: Boolean, toggleTheme: () -> Unit, openUrl: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("EVVY / ${page.title.uppercase()}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.weight(1f))
        HorizontalFloatingToolbar(expanded = true, colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()) {
            IconButton(toggleTheme) { AppIcon(if (dark) Res.drawable.light_mode else Res.drawable.dark_mode, "Toggle theme") }
            IconButton({ openUrl("https://github.com/evvyraine") }) { AppIcon(Res.drawable.code, "GitHub") }
        }
    }
}

@Composable private fun HomeScene(wide: Boolean) {
    if (wide) Row(Modifier.fillMaxWidth().heightIn(min = 620.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        IntroCopy(Modifier.weight(1.08f))
        ShapePortrait(Modifier.weight(.92f).fillMaxHeight())
    } else Column(verticalArrangement = Arrangement.spacedBy(24.dp)) { ShapePortrait(Modifier.fillMaxWidth().height(390.dp)); IntroCopy(Modifier.fillMaxWidth()) }
}
@Composable private fun IntroCopy(modifier: Modifier) {
    Column(modifier.padding(vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
        Text("HEY, I'M EVELYN", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text("I make the web wiggle.", style = MaterialTheme.typography.displayLarge)
        Text("Frontend developer. Sometimes Android. Often somewhere in a terminal. Always chasing the tiny detail that makes an interface feel alive.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.widthIn(max = 590.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SuggestionChip({}, { Text("Saint Petersburg") }, icon = { AppIcon(Res.drawable.location_on, "") })
            SuggestionChip({}, { Text("21") }, icon = { AppIcon(Res.drawable.cake, "") })
            SuggestionChip({}, { Text("Design") }, icon = { AppIcon(Res.drawable.palette, "") })
            SuggestionChip({}, { Text("Photography") }, icon = { AppIcon(Res.drawable.photo_camera, "") })
            SuggestionChip({}, { Text("Coffee") }, icon = { AppIcon(Res.drawable.local_cafe, "") })
        }
    }
}

@Composable private fun ShapePortrait(modifier: Modifier) {
    BoxWithConstraints(modifier) {
        val u = minOf(maxWidth, maxHeight)
        ShapeBlob(MaterialShapes.Slanted.toShape(), MaterialTheme.colorScheme.secondaryContainer.copy(alpha = .64f), u * .42f, Modifier.align(Alignment.CenterStart).offset(x = u * .02f, y = (-u * .16f)).rotate(-24f).blur(11.dp, BlurredEdgeTreatment.Unbounded))
        ShapeBlob(MaterialShapes.PuffyDiamond.toShape(), MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = .68f), u * .38f, Modifier.align(Alignment.CenterEnd).offset(x = (-u * .02f), y = u * .17f).rotate(28f).blur(13.dp, BlurredEdgeTreatment.Unbounded))
        Box(Modifier.size(u * .72f).align(Alignment.Center).background(MaterialTheme.colorScheme.primaryContainer, MaterialShapes.Ghostish.toShape()), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("ER", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("EVELYN RAINE", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("frontend dev", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .72f))
            }
        }
    }
}
@Composable private fun ShapeBlob(shape: Shape, color: Color, size: Dp, modifier: Modifier = Modifier) { Box(modifier.size(size).background(color, shape)) }

@Composable private fun WorkScene(wide: Boolean, openUrl: (String) -> Unit) {
    SceneHeading("A few things I've built.", "Selected projects across frontend, developer tooling, and the occasional terminal rabbit hole.")
    val state = rememberCarouselState { projects.size }
    HorizontalMultiBrowseCarousel(state, preferredItemWidth = if (wide) 480.dp else 310.dp, modifier = Modifier.fillMaxWidth().height(if (wide) 480.dp else 430.dp), itemSpacing = 16.dp, contentPadding = PaddingValues(horizontal = 2.dp)) { index ->
        val project = projects[index]
        ProjectPoster(project, index, Modifier.fillMaxSize().maskClip(RoundedCornerShape(36.dp)), openUrl)
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { AppIcon(Res.drawable.open_in_new, ""); Spacer(Modifier.width(10.dp)); Text("Drag to browse · use the arrow to open a repository", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }
}
@Composable private fun ProjectPoster(project: Project, index: Int, modifier: Modifier, openUrl: (String) -> Unit) {
    val base = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer)[index]
    Box(modifier.background(base)) {
        ShapeBlob(listOf(MaterialShapes.Burst, MaterialShapes.Flower, MaterialShapes.Boom)[index].toShape(), MaterialTheme.colorScheme.primary.copy(alpha = .18f), 190.dp, Modifier.align(Alignment.TopEnd).offset(36.dp, (-26).dp).rotate(index * 17f))
        Column(Modifier.fillMaxSize().padding(30.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text(project.kicker, style = MaterialTheme.typography.labelLarge); Spacer(Modifier.weight(1f)); FilledTonalIconButton({ openUrl(project.url) }) { AppIcon(Res.drawable.open_in_new, "Open ${project.name} repository") } }
            Spacer(Modifier.weight(1f)); Text(project.name, style = MaterialTheme.typography.displayLarge); Text(project.body, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.widthIn(max = 380.dp)); Spacer(Modifier.height(20.dp)); Text(project.tech, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable private fun StackScene(wide: Boolean) {
    SceneHeading("My everyday toolkit.", "The languages and technologies I reach for across the web, Android, and Linux.")
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    var arena by remember { mutableStateOf(IntSize.Zero) }
    val bodies = remember(wide) {
        listOf(
            PhysicsBody("TypeScript", Res.drawable.code, 0, if (wide) 214.dp else 156.dp, .39f, .29f, 92f, 64f),
            PhysicsBody("React", Res.drawable.work, 1, if (wide) 164.dp else 132.dp, .08f, .08f, 76f, 105f),
            PhysicsBody("Kotlin", Res.drawable.smartphone, 2, if (wide) 182.dp else 142.dp, .72f, .10f, -96f, 72f),
            PhysicsBody("Linux", Res.drawable.memory, 3, if (wide) 148.dp else 122.dp, .17f, .68f, 110f, -82f),
            PhysicsBody("Compose", Res.drawable.devices, 4, if (wide) 184.dp else 148.dp, .70f, .64f, -78f, -96f),
            PhysicsBody("Python", Res.drawable.terminal, 5, if (wide) 166.dp else 136.dp, .44f, .72f, 62f, -88f),
        )
    }

    LaunchedEffect(arena, bodies) {
        if (arena == IntSize.Zero) return@LaunchedEffect
        bodies.forEach { body ->
            val sizePx = with(density) { body.size.toPx() }
            if (!body.initialized) {
                body.x = (arena.width - sizePx).coerceAtLeast(0f) * body.startX
                body.y = (arena.height - sizePx).coerceAtLeast(0f) * body.startY
                body.initialized = true
            }
        }
        var previous = 0L
        while (true) {
            withFrameNanos { now ->
                if (previous == 0L) { previous = now; return@withFrameNanos }
                val dt = ((now - previous) / 1_000_000_000f).coerceAtMost(.034f)
                previous = now
                bodies.forEach { body ->
                    if (body.dragging) return@forEach
                    val sizePx = with(density) { body.size.toPx() }
                    val maxX = (arena.width - sizePx).coerceAtLeast(0f)
                    val maxY = (arena.height - sizePx).coerceAtLeast(0f)
                    body.x += body.vx * dt
                    body.y += body.vy * dt
                    body.rotation += body.spin * dt
                    if (body.x <= 0f && body.vx < 0f) { body.x = 0f; body.vx *= -.88f; body.spin = (body.spin + 8f).coerceIn(-120f, 120f) }
                    if (body.x >= maxX && body.vx > 0f) { body.x = maxX; body.vx *= -.88f; body.spin = (body.spin - 8f).coerceIn(-120f, 120f) }
                    if (body.y <= 0f && body.vy < 0f) { body.y = 0f; body.vy *= -.88f; body.spin = (body.spin - 6f).coerceIn(-120f, 120f) }
                    if (body.y >= maxY && body.vy > 0f) { body.y = maxY; body.vy *= -.88f; body.spin = (body.spin + 6f).coerceIn(-120f, 120f) }
                    body.vx = body.vx.coerceIn(-520f, 520f)
                    body.vy = body.vy.coerceIn(-520f, 520f)
                    body.spin = (body.spin * .998f).coerceIn(-120f, 120f)
                }

                // Treat the expressive silhouettes as soft circular bodies. This keeps
                // collisions stable even when their outlines have deep concave notches.
                for (firstIndex in 0 until bodies.lastIndex) {
                    for (secondIndex in firstIndex + 1 until bodies.size) {
                        resolveCollision(
                            bodies[firstIndex],
                            bodies[secondIndex],
                            with(density) { bodies[firstIndex].size.toPx() } * .42f,
                            with(density) { bodies[secondIndex].size.toPx() } * .42f,
                        )
                    }
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth().height(if (wide) 620.dp else 700.dp),
        shape = RoundedCornerShape(36.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .34f),
    ) {
        Box(
            Modifier.fillMaxSize()
                .onSizeChanged { arena = it }
        ) {
            bodies.forEach { body -> PhysicsOrb(body, arena, haptics) }
            Surface(Modifier.align(Alignment.BottomCenter).padding(14.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = .88f)) {
                Text("DRAG · FLICK · SCROLL TO SPIN", Modifier.padding(horizontal = 16.dp, vertical = 9.dp), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

private class PhysicsBody(
    val name: String,
    val icon: DrawableResource,
    val shapeIndex: Int,
    val size: Dp,
    val startX: Float,
    val startY: Float,
    initialVx: Float,
    initialVy: Float,
) {
    var x by mutableFloatStateOf(0f)
    var y by mutableFloatStateOf(0f)
    var vx by mutableFloatStateOf(initialVx)
    var vy by mutableFloatStateOf(initialVy)
    var rotation by mutableFloatStateOf(shapeIndex * 13f)
    var spin by mutableFloatStateOf(if (shapeIndex % 2 == 0) 16f else -13f)
    var dragging by mutableStateOf(false)
    var initialized by mutableStateOf(false)
}

private fun resolveCollision(a: PhysicsBody, b: PhysicsBody, radiusA: Float, radiusB: Float) {
    val ax = a.x + radiusA / .84f
    val ay = a.y + radiusA / .84f
    val bx = b.x + radiusB / .84f
    val by = b.y + radiusB / .84f
    val dx = bx - ax
    val dy = by - ay
    val minDistance = radiusA + radiusB
    val distanceSquared = dx * dx + dy * dy
    if (distanceSquared >= minDistance * minDistance) return

    val distance = sqrt(distanceSquared).coerceAtLeast(.001f)
    val nx = if (distanceSquared < .0001f) 1f else dx / distance
    val ny = if (distanceSquared < .0001f) 0f else dy / distance
    val overlap = minDistance - distance

    when {
        a.dragging && !b.dragging -> { b.x += nx * overlap; b.y += ny * overlap }
        b.dragging && !a.dragging -> { a.x -= nx * overlap; a.y -= ny * overlap }
        !a.dragging && !b.dragging -> {
            a.x -= nx * overlap * .5f; a.y -= ny * overlap * .5f
            b.x += nx * overlap * .5f; b.y += ny * overlap * .5f
        }
    }

    if (a.dragging && b.dragging) return
    val relativeSpeed = (b.vx - a.vx) * nx + (b.vy - a.vy) * ny
    if (relativeSpeed >= 0f) return
    val impulse = -(1f + .84f) * relativeSpeed / 2f
    if (!a.dragging) { a.vx = (a.vx - impulse * nx).coerceIn(-520f, 520f); a.vy = (a.vy - impulse * ny).coerceIn(-520f, 520f) }
    if (!b.dragging) { b.vx = (b.vx + impulse * nx).coerceIn(-520f, 520f); b.vy = (b.vy + impulse * ny).coerceIn(-520f, 520f) }
    val twist = (relativeSpeed * .035f).coerceIn(-18f, 18f)
    if (!a.dragging) a.spin = (a.spin + twist).coerceIn(-120f, 120f)
    if (!b.dragging) b.spin = (b.spin - twist).coerceIn(-120f, 120f)
}

@Composable private fun PhysicsOrb(body: PhysicsBody, arena: IntSize, haptics: androidx.compose.ui.hapticfeedback.HapticFeedback) {
    val shape = when (body.shapeIndex) {
        0 -> MaterialShapes.Sunny.toShape()
        1 -> MaterialShapes.Clover8Leaf.toShape()
        2 -> MaterialShapes.Gem.toShape()
        3 -> MaterialShapes.PixelCircle.toShape()
        4 -> MaterialShapes.Puffy.toShape()
        else -> MaterialShapes.SoftBurst.toShape()
    }
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val scale by animateFloatAsState(if (body.dragging) 1.12f else if (hovered) 1.06f else 1f, spring(dampingRatio = .55f, stiffness = 360f), label = "physics orb")
    val scheme = MaterialTheme.colorScheme
    val base = listOf(scheme.primaryContainer, scheme.tertiaryContainer, scheme.secondaryContainer, scheme.inversePrimary, scheme.primary, scheme.tertiary)[body.shapeIndex]
    val contentColor = listOf(scheme.onPrimaryContainer, scheme.onTertiaryContainer, scheme.onSecondaryContainer, scheme.inverseOnSurface, scheme.onPrimary, scheme.onTertiary)[body.shapeIndex]
    val color by animateColorAsState(if (body.dragging) lerp(base, scheme.surface, .20f) else base, label = "physics color")
    Box(
        Modifier.offset { IntOffset(body.x.roundToInt(), body.y.roundToInt()) }
            .size(body.size)
            .graphicsLayer { scaleX = scale; scaleY = scale; rotationZ = body.rotation; this.shape = shape; clip = true }
            .background(color, shape)
            .hoverable(source)
            .onPointerEvent(PointerEventType.Scroll) { event ->
                val wheel = event.changes.firstOrNull()?.scrollDelta?.y?.coerceIn(-3f, 3f) ?: 0f
                if (wheel != 0f) {
                    body.rotation += wheel * 4f
                    body.spin = (body.spin + wheel * 12f).coerceIn(-120f, 120f)
                    event.changes.forEach { it.consume() }
                }
            }
            .pointerInput(body, arena) {
                detectDragGestures(
                    onDragStart = { body.dragging = true; body.vx = 0f; body.vy = 0f; haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                    onDragEnd = { body.dragging = false; haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                    onDragCancel = { body.dragging = false },
                    onDrag = { change, amount ->
                        change.consume()
                        val sizePx = body.size.toPx()
                        body.x = (body.x + amount.x).coerceIn(0f, (arena.width - sizePx).coerceAtLeast(0f))
                        body.y = (body.y + amount.y).coerceIn(0f, (arena.height - sizePx).coerceAtLeast(0f))
                        body.vx = (amount.x * 10f).coerceIn(-420f, 420f)
                        body.vy = (amount.y * 10f).coerceIn(-420f, 420f)
                        body.rotation += amount.x.coerceIn(-12f, 12f) * .22f
                        body.spin = (amount.x * 1.4f).coerceIn(-90f, 90f)
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.graphicsLayer { rotationZ = -body.rotation }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(painterResource(body.icon), body.name, Modifier.size(30.dp), tint = contentColor)
            Text(body.name, style = MaterialTheme.typography.titleLarge, color = contentColor)
        }
    }
}

@Composable private fun DeviceScene(wide: Boolean) {
    SceneHeading("The little hardware lab.", "Different screens, one shared standard: it should feel good everywhere.")
    val devices = listOf("iPhone 17 Pro Max" to ("DAILY PHONE" to Res.drawable.smartphone), "Pixel 10a" to ("ANDROID TESTBED" to Res.drawable.smartphone), "MacBook Air M5" to ("MOBILE STUDIO" to Res.drawable.laptop), "Mac mini M4" to ("DESK ENGINE" to Res.drawable.desktop))
    Column(Modifier.fillMaxWidth()) { devices.forEachIndexed { index, device -> DeviceRow(index, device.first, device.second.first, device.second.second, wide); if (index < devices.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) } }
}
@Composable private fun DeviceRow(index: Int, name: String, label: String, icon: DrawableResource, wide: Boolean) {
    val shape = listOf(MaterialShapes.SemiCircle, MaterialShapes.Diamond, MaterialShapes.Bun, MaterialShapes.ClamShell)[index].toShape()
    Row(Modifier.fillMaxWidth().padding(vertical = 22.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("0${index + 1}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(if (wide) 34.dp else 16.dp))
        Box(Modifier.size(if (wide) 126.dp else 86.dp).background(if (index % 2 == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer, shape), contentAlignment = Alignment.Center) { AppIcon(icon, name, Modifier.size(if (wide) 44.dp else 34.dp)) }
        Spacer(Modifier.width(if (wide) 34.dp else 18.dp))
        Column(Modifier.weight(1f)) { Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(name, style = if (wide) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleLarge) }
        if (wide) { Text(if (index < 2) "POCKET" else "DESKTOP", style = MaterialTheme.typography.labelLarge); Spacer(Modifier.width(20.dp)); AppIcon(Res.drawable.arrow_outward, "") }
    }
}

@Composable private fun SocialFabMenu(expanded: Boolean, setExpanded: (Boolean) -> Unit, openUrl: (String) -> Unit, modifier: Modifier) {
    FloatingActionButtonMenu(expanded, button = { ToggleFloatingActionButton(expanded, setExpanded) { Icon(painterResource(if (expanded) Res.drawable.close else Res.drawable.alternate_email), if (expanded) "Close socials" else "Open socials", Modifier.size(28.dp), tint = if (expanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer) } }, modifier = modifier) {
        FloatingActionButtonMenuItem({ setExpanded(false); openUrl("https://github.com/evvyraine") }, { Text("GitHub · @evvyraine") }, { AppIcon(Res.drawable.code, "") })
        FloatingActionButtonMenuItem({ setExpanded(false); openUrl("https://t.me/evvyraine") }, { Text("Telegram · @evvyraine") }, { AppIcon(Res.drawable.alternate_email, "") })
        FloatingActionButtonMenuItem({ setExpanded(false); openUrl("https://x.com/evvyrain") }, { Text("X · @evvyrain") }, { AppIcon(Res.drawable.arrow_outward, "") })
    }
}

@Composable internal fun SceneHeading(title: String, subtitle: String) { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(title, style = MaterialTheme.typography.headlineLarge); Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.widthIn(max = 720.dp)) } }
@Composable internal fun AppIcon(resource: DrawableResource, description: String, modifier: Modifier = Modifier) { Icon(painterResource(resource), description.ifBlank { null }, modifier.size(24.dp)) }
