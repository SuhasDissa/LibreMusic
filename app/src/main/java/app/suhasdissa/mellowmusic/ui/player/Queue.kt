package app.suhasdissa.mellowmusic.ui.player

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import app.suhasdissa.mellowmusic.ui.components.SongCardCompact
import app.suhasdissa.mellowmusic.utils.DisposableListener
import app.suhasdissa.mellowmusic.utils.queue
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Queue(
    controller: MediaController
) {
    val queueItems = remember { controller.currentTimeline.queue.toMutableStateList() }
    controller.DisposableListener {
        object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                queueItems.clear()
                queueItems.addAll(timeline.queue)
            }
        }
    }
    val state = rememberReorderableLazyListState(onMove = { from, to ->
        queueItems.apply {
            val removedItem = removeAt(from.index)
            add(to.index, removedItem)
        }
    }, onDragEnd = { from, to ->
        val removedItem = controller.getMediaItemAt(from)
        controller.removeMediaItem(from)
        controller.addMediaItem(to, removedItem)
    })
    LazyColumn(
        state = state.listState,
        modifier = Modifier
            .reorderable(state)
    ) {
        items(items = queueItems, { it.first }) { queue ->
            val mediaItem = queue.second
            ReorderableItem(reorderableState = state, key = queue.first) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                SongCardCompact(
                    thumbnail = mediaItem.mediaMetadata.artworkUri,
                    title = mediaItem.mediaMetadata.title.toString(),
                    artist = mediaItem.mediaMetadata.artist.toString(),
                    TrailingContent = {
                        IconButton(onClick = { controller.removeMediaItem(queue.first) }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    },
                    onClickVideoCard = { controller.seekTo(queue.first, 0L) },
                    modifier = Modifier
                        .shadow(elevation)
                        .detectReorderAfterLongPress(state)
                        .background(MaterialTheme.colorScheme.surface)
                        .animateItemPlacement(
                            animationSpec = tween(
                                durationMillis = 100,
                                easing = FastOutSlowInEasing
                            )
                        )
                )
            }
        }
    }
}