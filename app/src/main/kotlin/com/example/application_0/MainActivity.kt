package com.example.application_0

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.application_0.ui.theme.Application_0Theme
import data.database.DatabaseProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DatabaseProvider.initialise(applicationContext)
        setContent {
            Application_0Theme {
                AppNavigation()
            }
        }
    }
}