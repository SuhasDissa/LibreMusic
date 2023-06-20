package app.suhasdissa.mellowmusic.ui.player

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.Player
import app.suhasdissa.mellowmusic.backend.viewmodel.PlayerViewModel
import app.suhasdissa.mellowmusic.utils.DisposableListener
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueSheet(
    onDismissRequest: () -> Unit,
    playerViewModel: PlayerViewModel = viewModel(factory = PlayerViewModel.Factory)
) {
    val playerSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()
    playerViewModel.controller?.let {
        it.DisposableListener {
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (playerSheetState.currentValue == SheetValue.Hidden) {
                        scope.launch {
                            playerSheetState.expand()
                        }
                    }
                }
            }
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = playerSheetState,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp,
        dragHandle = null
    ) {
        playerViewModel.controller?.let { controller ->
            Queue(controller)
        }
    }
}