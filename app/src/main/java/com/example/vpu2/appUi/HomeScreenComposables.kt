package com.example.vpu2.appUi


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay


/**
 * The main content of the home screen.
 *
 * @param allUArchData The list of all micro-architectures.
 * @param filteredUArchData The list of micro-architectures filtered by the selected architecture.
 * @param status The current status of the data fetching.
 * @param onArchSelect A callback to be invoked when an architecture is selected.
 * @param onArticleClick A callback to be invoked when an article is clicked.
 * @param onFoundationCardClick A callback to be invoked when a foundation card is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    allUArchData: List<UArch>,
    filteredUArchData: List<UArch>,
    selectedArch: String,
    status: String,
    onArchSelect: (String) -> Unit,
    onArticleClick: (UArch) -> Unit,
    onFoundationCardClick: (String) -> Unit
) {
    var showStatusPopup by remember { mutableStateOf(false) }
    var previousStatus by remember { mutableStateOf("") }
    var hasShownInitialPopup by remember { mutableStateOf(false) }

    // Only show popup when status changes from empty to non-empty, and only once
    LaunchedEffect(status) {
        if (status.isNotEmpty() && previousStatus.isEmpty() && !hasShownInitialPopup) {
            showStatusPopup = true
            hasShownInitialPopup = true
        }
        previousStatus = status
    }

    LaunchedEffect(showStatusPopup) {
        if (showStatusPopup) {
            delay(2000)
            showStatusPopup = false
        }
    }

    Scaffold(
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "μArch encyclopedia ",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Top Lazy Row with Foundation Entries
                TopLazyRow(
                    allUArchData = allUArchData,
                    onFoundationCardClick = onFoundationCardClick
                )

                // Architecture Navigation Bar
                ArchitectureNavBar(
                    selectedArch = selectedArch,
                    onArchSelect = onArchSelect
                )

                // Reduced gap between navigation bar and content
                Spacer(modifier = Modifier.height(10.dp))

                // Content Lazy Column
                if (filteredUArchData.isEmpty() && status == "Fetching data...") {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2D4A2B), RoundedCornerShape(25.dp))
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.width(20.dp).height(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Fetching data...",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    ContentLazyColumn(
                        allUArchData = allUArchData,
                        filteredUArchData = filteredUArchData,
                        selectedArch = selectedArch,
                        onArticleClick = onArticleClick
                    )
                }
            }
            if (showStatusPopup) {
                StatusPopup(status = status)
            }
        }
    }
}

@Composable
fun StatusPopup(status: String) {
        if (status.isEmpty()) return // Do not draw anything if the status is empty

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter  // ← CHANGED: was Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color.DarkGray, RoundedCornerShape(16.dp))
                    .padding(8.dp, 8.dp)
            ) {
                Text(text = status, color = Color.White)
            }
        }
    }


/**
 * A lazy row that displays the foundation entries.
 *
 * @param allUArchData The list of all micro-architectures.
 * @param onFoundationCardClick A callback to be invoked when a foundation card is clicked.
 */
@Composable
fun TopLazyRow(
    allUArchData: List<UArch>,
    onFoundationCardClick: (String) -> Unit
) {
    // Filter foundation entries only
    val foundationEntries = allUArchData.filter { it.arch == "foundation" }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Foundation concept cards
        items(foundationEntries) { foundation ->
            FoundationCard(
                title = foundation.name,
                description = foundation.description ?: "null",
                imageUrl = foundation.images?.firstOrNull() ?: "mull",
                modifier = Modifier
                    .width(280.dp)
                    .height(120.dp),
                onClick = { onFoundationCardClick(foundation.name) }
            )
        }
    }
}

/**
 * A card that displays a foundation entry.
 *
 * @param title The title of the foundation entry.
 * @param description The description of the foundation entry.
 * @param imageUrl The URL of the image for the foundation entry.
 * @param modifier The modifier to be applied to the card.
 * @param onClick A callback to be invoked when the card is clicked.
 */
@Composable
fun FoundationCard(
    title: String,
    description: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x66000000))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = title.replace("-", " ").split(" ").joinToString(" ") {
                        it.replaceFirstChar { char -> char.uppercase() }
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Fundamental",
                    color = Color(0xFF00FF00),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * A navigation bar that allows the user to select an architecture.
 *
 * @param selectedArch The currently selected architecture.
 * @param onArchSelect A callback to be invoked when an architecture is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchitectureNavBar(
    selectedArch: String,
    onArchSelect: (String) -> Unit
) {
    val architectures = listOf("x86", "arm", "riscv")
    val selectedIndex = architectures.indexOf(selectedArch).coerceAtLeast(0)

    PrimaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color(0xFF121212),
        modifier = Modifier.padding(horizontal = 80.dp),
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex), // Corrected to use selectedIndex directly
                color = Color(0xFF00FF00) // Green color
            )
        },
        divider = {}
    ) {
        architectures.forEachIndexed { index, arch ->
            val isSelected = selectedIndex == index
            Tab(
                selected = isSelected,
                onClick = { onArchSelect(arch) },
                text = {
                    Text(
                        text = arch.uppercase(),
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                // Removed interactionSource to eliminate click ripple animation
                modifier = Modifier
                    .padding(4.dp)
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                color = Color(0x1AFFFFFF),
                                shape = RoundedCornerShape(4.dp) // Rounded square
                            )
                        } else Modifier
                    )
            )
        }
    }
}


/**
 * A lazy column that displays the content of the home screen.
 *
 * @param allUArchData The list of all micro-architectures.
 * @param filteredUArchData The list of micro-architectures filtered by the selected architecture.
 * @param status The current status of the data fetching.
 * @param onArticleClick A callback to be invoked when an article is clicked.
 */
@Composable
fun ContentLazyColumn(
    allUArchData: List<UArch>,
    filteredUArchData: List<UArch>,
    selectedArch: String,
    onArticleClick: (UArch) -> Unit
) {
    // Sort by date (newest to oldest), handling null dates by putting them at the end
    val sortedData = filteredUArchData.sortedWith(
        compareByDescending<UArch> { it.date }
            .thenBy { it.name } // Secondary sort by name for items without dates
    )

    // Container for the lazy column with enhanced styling
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)) // Slightly different background for contrast
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp), // Increased spacing between cards
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
        ) {
            items(sortedData) { uArch ->
                UArchCard(
                    uArch = uArch,
                    onClick = { onArticleClick(uArch) }
                )
            }
        }
    }
}

/**
 * A card that displays a micro-architecture.
 *
 * @param uArch The micro-architecture to be displayed.
 * @param onClick A callback to be invoked when the card is clicked.
 */
@Composable
fun UArchCard(
    uArch: UArch,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color(0x2AFFFFFF), // Subtle white border
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // Increased elevation
        shape = RoundedCornerShape(12.dp) // More rounded corners
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row with square thumbnail and text content
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Square thumbnail image
                val imageUrl = uArch.images?.firstOrNull()
                    ?: "https://images.unsplash.com/photo-1518770660439-4636190af475?w=400&h=200&fit=crop"
                AsyncImage(
                    model = imageUrl,
                    contentDescription = uArch.name,
                    modifier = Modifier
                        .width(96.dp) // Increased from 80dp
                        .height(96.dp), // Increased from 80dp
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(24.dp))

                // Text content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Top)
                ) {
                    // Architecture tag
                    uArch.arch?.let { arch ->
                        Text(
                            text = arch.uppercase(),
                            color = Color(0xFF00FF00),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Title
                    Text(
                        text = uArch.name,
                        color = Color.White,
                        fontSize = 20.sp, // Increased from 18sp
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Description
                    uArch.description?.let { description ->
                        val truncatedDescription = if (description.length > 30) { // Increased from 25
                            "${description.take(30)}..."
                        } else description
                        Text(
                            text = truncatedDescription,
                            color = Color(0xFFE0E0E0), // Slightly lighter gray for better visibility
                            fontSize = 15.sp, // Increased from 14sp
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Footer with date and read button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        uArch.date?.let { date ->
                            Text(
                                text = date.toString(),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Text(
                            text = "READ",
                            color = Color(0xFF00FF00),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}