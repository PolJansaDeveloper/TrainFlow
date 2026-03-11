package com.pjdev.trainflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pjdev.trainflow.data.datastore.TrainFlowDataStore
import com.pjdev.trainflow.data.repository.TrainFlowRepository
import com.pjdev.trainflow.navigation.TrainFlowApp
import com.pjdev.trainflow.presentation.TrainFlowViewModel
import com.pjdev.trainflow.ui.theme.TrainFlowTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<TrainFlowViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TrainFlowViewModel(
                    TrainFlowRepository(
                        TrainFlowDataStore(applicationContext)
                    )
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrainFlowTheme {
                TrainFlowApp(viewModel)
            }
        }
    }
}
