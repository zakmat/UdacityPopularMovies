package com.mz.popmovies.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mz.popmovies.R
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.data.TrailerEntry
import com.mz.popmovies.ui.components.Thumbnail
import com.mz.popmovies.ui.theme.PopMoviesTheme
import java.lang.Long.max
import kotlin.math.roundToLong

@Composable
fun StarRating(rating: Double) {
    val emptyStar = painterResource(id = R.drawable.star_border_24dp)
    val halfStar = painterResource(id = R.drawable.star_half_24dp)
    val fullStar = painterResource(id = R.drawable.star_full_24dp)
    val tint = MaterialTheme.colors.primary

    Row {
        for (count in 0..4) {
            when (max((rating - count * 2).roundToLong(), 0L)) {
                0L ->
                    Icon(emptyStar, null, tint = tint)
                1L ->
                    Icon(halfStar, null, tint = tint)
                else ->
                    Icon(fullStar, null, tint = tint)
            }
        }
    }
}

@Composable
fun DetailScreen(state: MovieViewModel.MovieDetailState, onBack: () -> Unit = {}, onClick: (String) -> Unit = {}) {
    if (state.isLoading) {
        Dialog(onDismissRequest = { onBack() }) {
            CircularProgressIndicator()
        }
        return
    }
    val movie = state.movie!!
    val trailers = state.trailers
    val reviews = state.reviews
    Column {

        Text(
            movie.title, modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary), style = MaterialTheme.typography.h1
        )
        Row {
            Thumbnail(url = movie.thumbnail, modifier=Modifier.height(200.dp).padding(10.dp))
            Column {
                Text(movie.releaseDate ?: "Unknown")
                Text("TMDb Rating: ${movie.voteAverage}/10")
                StarRating(movie.voteAverage)
            }
        }
        LazyColumn {
            item {
                Text(stringResource(R.string.overview), style = MaterialTheme.typography.h2)
                Divider(color = MaterialTheme.colors.primary)
                Text(movie.overview)
                Spacer(Modifier.height(5.0.dp))
            }
            item {

                Text(stringResource(R.string.videos_label), style = MaterialTheme.typography.h2)
                Divider(color = MaterialTheme.colors.primary)
                LazyRow {
                    items(items = trailers, itemContent = { trailer ->
                        Column(
                            Modifier.size(140.dp, 100.dp).clickable { onClick(trailer.key) },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Thumbnail(trailer.thumbnail)
                            Text(
                                trailer.name,
                                Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    })
                }
                Spacer(Modifier.height(5.0.dp))
            }
            item {
                Text(stringResource(R.string.reviews_label), style = MaterialTheme.typography.h2)
                Divider(color = MaterialTheme.colors.primary)
            }
            items(reviews) { review ->
                Text(
                    review.author,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary
                )
                Spacer(Modifier.height(2.0.dp))
                Text(review.content, style = MaterialTheme.typography.body1)
                Divider(color = MaterialTheme.colors.primary)
            }
        }
    }
}


@Composable
@Preview
fun DetailPreview() {
    val movie = MovieEntry(
        movie_id = 0, title = "LOTR",
        overview = "Lorem ipsum sic quorat demostrandum nihil novi sum solei. Etcetera e quibibus",
        posterPath = null,
        releaseDate = "1.1.2007",
        voteAverage = 6.9,
    )

    val reviews = listOf(
        ReviewEntry(
            review_id = "0",
            author = "Gandalf",
            content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas sed tincidunt elit. In eget nibh vel urna fermentum mollis. In tincidunt, massa ut accumsan malesuada, leo erat egestas dui, malesuada rhoncus est ex in leo. Morbi ut nisi mollis, bibendum turpis iaculis, ullamcorper velit. Suspendisse vel nibh lectus. Proin ac nibh consequat, lobortis erat in, laoreet ipsum. Etiam dignissim justo vel sem elementum pretium. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Morbi tortor sem, sagittis eu leo eget, bibendum luctus nibh. Nam rhoncus, dolor ac tempor scelerisque, tortor tortor venenatis sem, et egestas mauris quam tempor eros. Vestibulum felis ante, molestie et mattis a, volutpat vitae dui. Aliquam eget urna ac nunc efficitur euismod. Maecenas quis lacinia turpis. Phasellus iaculis nibh et velit dapibus, ac pulvinar odio mollis. Nulla mi est, pharetra vitae mi ut, suscipit pharetra dui. Praesent faucibus viverra cursus. Aliquam erat volutpat. Ut feugiat ligula sit amet cursus fringilla. Ut sollicitudin ornare risus et sagittis. In vitae sem at turpis mollis efficitur. Quisque id augue in urna bibendum aliquet. Phasellus scelerisque interdum augue sit amet porttitor. Donec in eros eget justo sagittis imperdiet. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Sed laoreet enim a augue placerat, et lobortis augue sollicitudin. In viverra tincidunt ipsum, id venenatis metus blandit et. Aliquam quis blandit mauris. In gravida libero ac lectus dignissim placerat. Nam eget purus nunc. Proin vel commodo quam, sed fermentum sem. Nullam vel mauris fermentum, ornare nulla vel, ultrices sem. Sed placerat elit non diam efficitur elementum.".trimIndent()
        )
    )
    val trailers = listOf(
        TrailerEntry(
            trailer_id = "0",
            "Great epic adventure",
            "",
            "youtube"
        )
    )
    val state = MovieViewModel.MovieDetailState(false, movie, trailers, reviews)
    PopMoviesTheme {
        Surface(color = MaterialTheme.colors.background) {
            DetailScreen(state)
        }
    }
}