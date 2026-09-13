package com.example.worder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.yourappname.ui.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {   // вместо setContentView(R.layout.activity_main)
            MaterialTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}