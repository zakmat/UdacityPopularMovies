package com.mz.popmovies.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil.compose.rememberImagePainter
import com.mz.popmovies.R

@Composable
fun Thumbnail(url: String?, modifier: Modifier = Modifier) {
    if (url == null) {
        Image(painterResource(id = R.mipmap.placeholder), null, modifier = modifier)
    } else {
        //
        Image(
            rememberImagePainter(
                data = url,
                builder = {
                    crossfade(true)
                    placeholder(R.mipmap.placeholder)

                }),
            contentDescription = null,
            modifier = modifier
        )
    }
}