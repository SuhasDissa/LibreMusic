package app.suhasdissa.mellowmusic.ui.screens.songs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.suhasdissa.mellowmusic.R
import app.suhasdissa.mellowmusic.backend.database.entities.Song
import app.suhasdissa.mellowmusic.backend.viewmodel.PlayerViewModel
import app.suhasdissa.mellowmusic.backend.viewmodel.SongViewModel
import app.suhasdissa.mellowmusic.ui.components.IllustratedMessageScreen
import app.suhasdissa.mellowmusic.ui.components.SongCard
import app.suhasdissa.mellowmusic.ui.components.SongSettingsSheet

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongListView(
    showFavourites: Boolean,
    songViewModel: SongViewModel = viewModel(factory = SongViewModel.Factory),
    playerViewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory)
) {
    var showSongSettings by remember { mutableStateOf(false) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    val songs by if (showFavourites) songViewModel.favSongs.collectAsState() else songViewModel.songs.collectAsState()
    if (songs.isEmpty()) {
        IllustratedMessageScreen(image = R.drawable.ic_launcher_monochrome)
    } else {
        val groups = remember(songs) { songs.groupBy { song -> song.title.first().toString() } }
        LazyColumn(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            groups.forEach { group ->
                stickyHeader {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Text(
                            text = group.key,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
                }
                items(items = group.value) { item ->
                    SongCard(
                        thumbnail = item.thumbnailUrl,
                        title = item.title,
                        artist = item.artistsText,
                        duration = item.durationText,
                        onClickCard = {
                            playerViewModel.playSong(item)
                        },
                        onLongPress = {
                            selectedSong = item
                            showSongSettings = true
                        }
                    )
                }
            }
        }
    }
    if (showSongSettings) {
        selectedSong?.let {
            SongSettingsSheet(
                onDismissRequest = { showSongSettings = false },
                song = selectedSong!!
            )
        }
    }
}
