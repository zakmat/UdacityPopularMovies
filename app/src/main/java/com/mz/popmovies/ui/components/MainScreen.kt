package com.mz.popmovies.ui

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mz.popmovies.R
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.ui.components.Thumbnail
import com.mz.popmovies.ui.theme.PopMoviesTheme
import timber.log.Timber

@Composable
fun CategorySelection(
    currentCategory: String,
    categories: Map<String, String>,
    onCategoryChanged: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    Row(Modifier.clickable { expanded = !expanded }) {
        Text(text = currentCategory, modifier = Modifier.padding(end = 8.dp))
        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            for (category in categories) {
                DropdownMenuItem(onClick = {
                    onCategoryChanged(category.value)
                    expanded = false
                }) {
                    Text(category.key)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
//TODO: pass category as parameter
fun MainScreen(
    movies: List<MovieEntry>,
    isLoading: Boolean,
    onClick: (MovieEntry) -> Unit = {},
    onCategoryChanged: (String) -> Unit = {},
    onEndReached: () -> Unit = {}
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.app_name))
                    CategorySelection(
                        currentCategory = "Popular",
                        mapOf("Popular" to "popular", "Top rated" to "top_rated"),
                        onCategoryChanged = { onCategoryChanged(it) }
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.primary
        )
    }) {
        val listState = rememberLazyListState()
        val rowSize =
            if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 4 else 2
        if ((listState.firstVisibleItemIndex + 4) * rowSize > movies.size && !isLoading) {
            Timber.i("End is reached, get more movies")
            onEndReached()
        }
        LazyVerticalGrid(cells = GridCells.Fixed(rowSize), state = listState) {
            itemsIndexed(movies) { index, movie ->
                Column(Modifier.clickable { onClick(movie) }) {
                    Timber.i("${movie.title} ${index}")
                    Thumbnail(url = movie.thumbnail, modifier = Modifier.aspectRatio(2f / 3f))
                    Text(movie.title, style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}


@Composable
@Preview
fun MainScreenPreview() {
    val movies: List<MovieEntry> = emptyList()
    PopMoviesTheme {
        MainScreen(movies, false)
    }
}
