package com.lab.island.sdk.island

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Receives the play/pause button from this package's media notification. */
class MediaControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == XiaomiSuperIslandPublisher.ACTION_TOGGLE_MEDIA) {
            IslandController.get(context.applicationContext).toggleMediaPlayback()
        }
    }
}
