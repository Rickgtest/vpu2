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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * A wrapper composable that collects state from the [MainViewModel] and passes it to [HomeScreenContent].
 *
 * @param mainViewModel The view model for the home screen.
 * @param onArticleClick A callback to be invoked when an article is clicked.
 * @param onFoundationCardClick A callback to be invoked when a foundation card is clicked.
 */
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    onArticleClick: (UArch) -> Unit,
    onFoundationCardClick: (String) -> Unit
) {
    val allUArchData by mainViewModel.uArchData.collectAsState()
    val filteredUArchData by mainViewModel.filteredUArchData.collectAsState()
    val selectedArch by mainViewModel.selectedArch.collectAsState()
    val status by mainViewModel.status.collectAsState()

    HomeScreenContent(
        allUArchData = allUArchData,
        filteredUArchData = filteredUArchData,
        selectedArch = selectedArch,
        status = status,
        onArchSelect = { mainViewModel.setSelectedArch(it) },
        onArticleClick = onArticleClick,
        onFoundationCardClick = onFoundationCardClick
    )
}

/**
 * The main composable for the home screen. It lays out the UI components and handles the overall structure of the screen.
 *
 * @param allUArchData The list of all micro-architectures.
 * @param filteredUArchData The list of micro-architectures filtered by the selected architecture.
 * @param selectedArch The currently selected architecture.
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

                TopLazyRow(
                    allUArchData = allUArchData,
                    onFoundationCardClick = onFoundationCardClick
                )

                ArchitectureNavBar(
                    selectedArch = selectedArch,
                    onArchSelect = onArchSelect
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (filteredUArchData.isEmpty() && (status == "Fetching data..." || status == "No network connection, loading from cache.")) {
                    LoadingIndicator(status = status)
                } else {
                    ContentLazyColumn(
                        filteredUArchData = filteredUArchData,
                        onArticleClick = onArticleClick
                    )
                }
            }
        }
    }
}

/**
 * A composable that shows a loading indicator with a status message.
 *
 * @param status The status message to display.
 */
@Composable
fun LoadingIndicator(status: String) {
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
                if (status == "Fetching data...") {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
                Text(
                    text = status,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * A lazy row that displays the "foundation" entries.
 *
 * @param allUArchData The list of all micro-architectures.
 * @param onFoundationCardClick A callback to be invoked when a foundation card is clicked.
 */
@Composable
fun TopLazyRow(
    allUArchData: List<UArch>,
    onFoundationCardClick: (String) -> Unit
) {
    val foundationEntries = allUArchData.filter { it.arch == "foundation" }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(foundationEntries) { foundation ->
            FoundationCard(
                uArch = foundation,
                onClick = { onFoundationCardClick(foundation.name) }
            )
        }
    }
}

/**
 * A card composable for displaying a single "foundation" entry.
 *
 * @param uArch The foundation entry to display.
 * @param onClick A callback to be invoked when the card is clicked.
 */
@Composable
fun FoundationCard(
    uArch: UArch,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AsyncImage(
                model = uArch.images?.firstOrNull() ?: "",
                contentDescription = uArch.name,
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
                    text = uArch.name.replace("-", " ").split(" ").joinToString(" ") {
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
 * The navigation bar for selecting different CPU architectures.
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
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                color = Color(0xFF00FF00)
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
                modifier = Modifier
                    .padding(4.dp)
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                color = Color(0x1AFFFFFF),
                                shape = RoundedCornerShape(4.dp)
                            )
                        } else Modifier
                    )
            )
        }
    }
}

/**
 * A lazy column that displays the list of micro-architectures.
 *
 * @param filteredUArchData The list of micro-architectures to display.
 * @param onArticleClick A callback to be invoked when an article is clicked.
 */
@Composable
fun ContentLazyColumn(
    filteredUArchData: List<UArch>,
    onArticleClick: (UArch) -> Unit
) {
    val sortedData = filteredUArchData.sortedWith(
        compareByDescending<UArch> { it.date }
            .thenBy { it.name }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
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
 * A card composable for displaying a single micro-architecture.
 *
 * @param uArch The micro-architecture to display.
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
                color = Color(0x2AFFFFFF),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                val imageUrl = uArch.images?.firstOrNull()
                    ?: "https.images.unsplash.com/photo-1518770660439-4636190af475?w=400&h=200&fit=crop"
                AsyncImage(
                    model = imageUrl,
                    contentDescription = uArch.name,
                    modifier = Modifier
                        .width(96.dp)
                        .height(96.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(24.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Top)
                ) {
                    uArch.arch?.let { arch ->
                        Text(
                            text = arch.uppercase(),
                            color = Color(0xFF00FF00),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Text(
                        text = uArch.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    uArch.description?.let { description ->
                        val truncatedDescription = if (description.length > 30) {
                            "${description.take(30)}..."
                        } else description
                        Text(
                            text = truncatedDescription,
                            color = Color(0xFFE0E0E0),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

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