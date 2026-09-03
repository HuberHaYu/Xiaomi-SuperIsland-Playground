package com.lab.island

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.lab.island.island.DeviceCapability
import com.lab.island.island.IslandController
import com.lab.island.island.IslandDraft
import com.lab.island.ui.IslandApp
import com.lab.island.ui.theme.IslandTheme

class MainActivity : ComponentActivity() {
    private val controller by lazy { IslandController.get(applicationContext) }
    private var pendingDraft: IslandDraft? = null
    private var uiMessage by mutableStateOf<String?>(null)
    private var capability by mutableStateOf(DeviceCapability.OTHER_ANDROID)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val draft = pendingDraft
        pendingDraft = null
        if (granted && draft != null) {
            publish(draft)
        } else if (!granted) {
            uiMessage = getString(R.string.notification_permission_required)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        refreshSystemState()
        setContent {
            IslandTheme {
                val activeIslands by controller.activeIslands.collectAsState()
                IslandApp(
                    capability = capability,
                    activeIslands = activeIslands,
                    message = uiMessage,
                    onMessageShown = { uiMessage = null },
                    onSend = ::sendWithPermission,
                    onUpdate = { draft ->
                        controller.updateActive(draft) { outcome ->
                            uiMessage = outcome.message
                        }
                    },
                    onCancel = { id ->
                        controller.cancel(id)
                        uiMessage = getString(R.string.message_island_withdrawn)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshSystemState()
        controller.refreshActiveIslands()
    }

    private fun refreshSystemState() {
        capability = controller.capability()
    }

    private fun sendWithPermission(draft: IslandDraft) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            pendingDraft = draft
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        publish(draft)
    }

    private fun publish(draft: IslandDraft) {
        controller.publish(draft) { outcome ->
            uiMessage = outcome.message
        }
    }

}
