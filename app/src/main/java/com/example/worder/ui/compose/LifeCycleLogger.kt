package com.example.worder.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@Composable
fun ScreenLifecycleLogger(screenName: String, lifecycleOwner: LifecycleOwner) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val eventName = when (event) {
                Lifecycle.Event.ON_CREATE -> "onCreate"
                Lifecycle.Event.ON_START -> "onStart"
                Lifecycle.Event.ON_RESUME -> "onResume"
                Lifecycle.Event.ON_PAUSE -> "onPause"
                Lifecycle.Event.ON_STOP -> "onStop"
                Lifecycle.Event.ON_DESTROY -> "onDestroy"
                else -> event.name
            }
            android.util.Log.d("Screen_LIFE_CYCLE", "$screenName: $eventName")
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}