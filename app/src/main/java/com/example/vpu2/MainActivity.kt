package com.example.vpu2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vpu2.appUi.DetailScreen
import com.example.vpu2.appUi.HomeScreen
import com.example.vpu2.appUi.MainViewModel
import com.example.vpu2.ui.theme.Vpu2Theme

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Called")
        enableEdgeToEdge()
        setContent {
            Vpu2Theme {
                AppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Called")
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController, viewModel = mainViewModel)
        }
        composable("detail/{uArchName}") { backStackEntry ->
            val uArchName = backStackEntry.arguments?.getString("uArchName")
            DetailScreen(navController = navController, uArchName = uArchName, viewModel = mainViewModel)
        }
    }
}