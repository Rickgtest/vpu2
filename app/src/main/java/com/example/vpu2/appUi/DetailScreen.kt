package com.example.vpu2.appUi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun DetailScreen(navController: NavController, uArchName: String?, viewModel: MainViewModel = viewModel()) {
    val uArch = uArchName?.let { viewModel.getUArchByName(it) }
    DetailScreenContent(uArch = uArch, onBack = { navController.popBackStack() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(uArch: UArch?, onBack: () -> Unit) {
    var showImageZoom by remember { mutableStateOf(false) }
    var zoomedImageUrl by remember { mutableStateOf("") }

    // Click handler for images
    val onImageClick = { imageUrl: String ->
        zoomedImageUrl = imageUrl
        showImageZoom = true
    }

    if (uArch != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .systemBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Blurred background image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    AsyncImage(
                        model = uArch.images?.firstOrNull(),
                        contentDescription = uArch.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(12.dp), // Blurred thumbnail as requested
                        contentScale = ContentScale.Crop
                    )
                    // Dark overlay for better text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    ),
                                    startY = 200f
                                )
                            )
                    )
                    // Title and metadata overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = uArch.name,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Arch : ${uArch.arch}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Year released : ${uArch.date ?: "Recently"}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }

                // Content section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF121212))
                        .padding(24.dp)
                ) {
                    // Single "Overview" tab, as requested (Album/Discussion removed)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        TabItem(text = "Overview", isSelected = true)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Format text with image gaps every 150 words
                    FormattedContent(
                        // Use the 'content' field for the main text
                        fullText = uArch.content ?: (uArch.description ?: "No description available for this architecture."),
                        images = uArch.images ?: emptyList(),
                        onImageClick = onImageClick // Pass the click handler
                    )
                }
            }

            // Top navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Image zoom dialog - This is what handles the zoom
        if (showImageZoom) {
            ImageZoomDialog(
                imageUrl = zoomedImageUrl,
                onDismiss = { showImageZoom = false }
            )
        }
    } else {
        // Loading state
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFFFFD700))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading architecture details...",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * This composable formats the description text, inserting an image
 * every 150 words, as requested.
 */
@Composable
fun FormattedContent(
    fullText: String,
    images: List<String>,
    onImageClick: (String) -> Unit // Receive click handler
) {
    Column {
        val imageQueue = remember { images.toMutableList() }

        // 1. Show first image (thumbnail)
        if (imageQueue.isNotEmpty()) {
            val initialImageUrl = imageQueue.removeAt(0) // Take image 0
            ClickableImage(
                imageUrl = initialImageUrl,
                contentDescription = "Detail image 1",
                onClick = { onImageClick(initialImageUrl) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        val words = fullText.split(" ")
        val wordsPerImage = 150 // Set to 150 words
        var wordCount = 0
        val stringBuilder = StringBuilder()

        words.forEachIndexed { index, word ->
            stringBuilder.append(word).append(" ") // Add word and a space
            wordCount++

            // Check if we hit the word count *and* have images left
            if (wordCount >= wordsPerImage && imageQueue.isNotEmpty()) {
                // 1. Print the accumulated text
                Text(
                    text = stringBuilder.toString().trim(),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                stringBuilder.clear() // Reset text
                Spacer(modifier = Modifier.height(16.dp))

                // 2. Print the next image
                val currentImageUrl = imageQueue.removeAt(0) // Take next image
                ClickableImage(
                    imageUrl = currentImageUrl,
                    contentDescription = "Detail image",
                    onClick = { onImageClick(currentImageUrl) }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // 3. Reset word count
                wordCount = 0
            }
        }

        // After loop, print any remaining text
        if (stringBuilder.isNotEmpty()) {
            Text(
                text = stringBuilder.toString().trim(),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                lineHeight = 24.sp
            )
        }

        // Print any remaining images
        while (imageQueue.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            val currentImageUrl = imageQueue.removeAt(0)
            ClickableImage(
                imageUrl = currentImageUrl,
                contentDescription = "Detail image",
                onClick = { onImageClick(currentImageUrl) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * This composable makes the image clickable to trigger the zoom.
 */
@Composable
fun ClickableImage(
    imageUrl: String?,
    contentDescription: String,
    onClick: () -> Unit // This onClick lambda triggers the zoom
) {
    if (!imageUrl.isNullOrEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }, // The clickable modifier
            shape = RoundedCornerShape(12.dp), // Rounded edges as requested
            colors = CardDefaults.cardColors(containerColor = Color(0xFF252525)) // Dark bg for images
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 450.dp), // Set max height
                contentScale = ContentScale.Fit // Maintain aspect ratio, no crop
            )
        }
    }
}

/**
 * This is the Dialog that shows the pinch-to-zoom image.
 */
@Composable
fun ImageZoomDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Use full screen
    ) {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)) // Semi-transparent background
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val oldScale = scale
                        scale = (scale * zoom).coerceIn(1f, 4f) // Clamp zoom (1x to 4x)

                        // This formula adjusts offset to zoom towards the gesture centroid
                        offset = (offset + centroid - (centroid * (scale / oldScale))) + pan

                        // When zoomed out, reset offset
                        if (scale == 1f) {
                            offset = Offset.Zero
                        }
                        // Note: Offset is not clamped, user can pan image off-screen.
                        // Clamping is significantly more complex.
                    }
                }
                .clickable( // Click background to dismiss
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Zoomed image",
                modifier = Modifier
                    .fillMaxWidth() // Image will fit width initially
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .clickable( // Stop clicks on image from dismissing
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean) {
    // Underline for the selected tab
    Column {
        Text(
            text = text,
            color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(3.dp)
                    .background(Color(0xFFFFD700), shape = RoundedCornerShape(2.dp))
            )
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun DetailScreenPreview() {
    val longContent = "This is a sample architecture description. We will split this text every 150 words to insert an image. This is the first chunk, and it needs to be quite long to demonstrate the effect. Let's keep writing some placeholder text to fill up the space. " +
            "One hundred and fifty words is actually a good amount of text, making up a solid paragraph or two. This ensures the user isn't interrupted by images too frequently, which would be distracting. " +
            "By the time the user gets to this point, they should have read a substantial amount of information before the first image break appears. This is much better than the 50-character chunking. " +
            "Okay, that's probably close to 150 words. Now we'd expect an image to show up. " +
            "After the image, the text continues. This will be the second chunk of text, also aiming for 150 words. " +
            "This modular approach allows the article to be dynamically populated with text and images from the database without a fixed layout. " +
            "The images will all be clickable, opening a pinch-to-zoom viewer, which is a great user experience enhancement. " +
            "The blurred background at the top also sets a nice visual tone for the article, grabbing the user's attention. " +
            "Removing the extra tabs simplifies the UI, focusing the user entirely on the 'Overview' content. This is the end of the second chunk."


    val mockUArch = UArch(
        name = "Sample Architecture",
        description = "This is the short description, not used for the main text.",
        content = longContent, // Use the long content here
        date = 2024,
        images = listOf(
            "https://placehold.co/800x600/2a2a2a/ffffff?text=Image+1+(Thumbnail)",
            "https://placehold.co/600x400/3a3a3a/ffffff?text=Image+2",
            "https://placehold.co/600x500/4a4a4a/ffffff?text=Image+3",
            "https://placehold.co/600x450/5a5a5a/ffffff?text=Image+4"
        ),
        arch = "x86-64"
    )
    MaterialTheme {
        DetailScreenContent(uArch = mockUArch, onBack = {})
    }
}