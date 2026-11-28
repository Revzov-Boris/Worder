package com.example.worder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.worder.viewmodel.SettingsViewModel
import com.example.worder.ui.theme.WorderTheme
import androidx.compose.material3.Surface
import com.example.worder.ui.compose.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("StartActivity", "onCreate")
        setContent {   // вместо setContentView(R.layout.activity_main)
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()
            WorderTheme(darkTheme = settings?.darkTheme ?: false) {
                Surface {
                    AppNavigation()
                }
            }
        }
    }

    override fun onStart()   { super.onStart();   Log.d("StartActivity", "onStart") }
    override fun onResume()  { super.onResume();  Log.d("StartActivity", "onResume") }
    override fun onPause()   { super.onPause();   Log.d("StartActivity", "onPause") }
    override fun onStop()    { super.onStop();    Log.d("StartActivity", "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d("StartActivity", "onDestroy") }
}