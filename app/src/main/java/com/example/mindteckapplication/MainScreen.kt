package com.example.mindteckapplication

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable function that displays a main screen with a list of images and a search functionality.
 * When the list contains a large number of items, scrolling is enabled and the search text field remains pinned at the top.
 * The user can search for specific items by entering keywords related to the images.
 *
 * Example for searching:
 * To search for a specific image item, enter keywords related to the item in the search field.
 * The list will display the matching items.
 * Additionally, the corresponding image for the matched item will be shown.
 *
 * For example, to find the second image item, type "second image item 1" in the search field.
 * This will display the list item containing the text "this item will appear in list item" and show the related image for that item.
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val images = listOf(
        R.drawable.first_image,
        R.drawable.second_image,
        R.drawable.third_image,
        R.drawable.fourth_image,
        R.drawable.fifth_image
    )
    val pagerState = rememberPagerState()
    val currentItemIndex = pagerState.currentPage
    val currentItem = images.getOrNull(currentItemIndex)

    var searchQuery by remember { mutableStateOf("") }
    val listContent = generateListContent(currentItem, searchQuery)

    LaunchedEffect(searchQuery) {
        val targetImageIndex = images.indexOfFirst { image ->
            generateListContent(image, searchQuery).isNotEmpty()
        }
        if (targetImageIndex != -1 && targetImageIndex != currentItemIndex) {
            pagerState.scrollToPage(targetImageIndex)
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HorizontalPager(
                state = pagerState,
                pageCount = images.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) { page ->
                Image(
                    painter = painterResource(id = images[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                images.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(10.dp)
                            .background(
                                color = if (index == pagerState.currentPage) Color.Red else Color.LightGray,
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        stickyHeader {
            SearchField(
                modifier = Modifier.fillMaxWidth(),
                searchText = searchQuery,
                onSearchTextChanged = { searchQuery = it }
            )
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(listContent) { item ->

            ListItem(
                image = currentItem,
                content = item
            )
        }
    }
}

@Composable
fun ListItem(image: Int?, content: String) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            image?.let {
                painterResource(id = it)
            }?.let {
                Image(
                    painter = it,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                )
            }

            Text(
                text = content,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp
            )
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        placeholder = { Text(text = "Search") },
        modifier = modifier
    )
}

private fun generateListContent(image: Int?, searchText: String): List<String> {
    val itemList = when (image) {
        R.drawable.first_image -> (1..30).map { "First image item $it" }
        R.drawable.second_image -> listOf("Second image item 1", "Second image item 2")
        R.drawable.third_image -> listOf("Third image item 1", "Third image item 2")
        R.drawable.fourth_image -> listOf("Fourth image item 1", "Fourth image item 2")
        R.drawable.fifth_image -> listOf("Fifth image item 1", "Fifth image item 3")
        else -> emptyList()
    }

    return if (searchText.isBlank()) {
        itemList
    } else {
        itemList.filter { it.contains(searchText, ignoreCase = true) }
    }
}