package com.lab.island.sdk.island

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

class IslandClient private constructor(private val controller: IslandController) {
    val activeIslands: StateFlow<List<ActiveIsland>>
        get() = controller.activeIslands

    fun capability(): DeviceCapability = controller.capability()

    fun publish(draft: IslandDraft, callback: IslandCallback) {
        controller.publish(draft) { callback.onResult(it) }
    }

    fun updateActive(draft: IslandDraft, callback: IslandCallback?) {
        controller.updateActive(draft) { callback?.onResult(it) }
    }

    fun cancel(notificationId: Int) = controller.cancel(notificationId)

    companion object {
        @JvmStatic
        fun get(context: Context): IslandClient = IslandClient(IslandController.get(context))
    }
}
