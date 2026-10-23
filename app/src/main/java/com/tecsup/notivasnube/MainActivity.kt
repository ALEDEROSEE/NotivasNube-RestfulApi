package com.tecsup.notivasnube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tecsup.notivasnube.ui.NotasApp
import com.tecsup.notivasnube.ui.NotasViewModel
import com.tecsup.notivasnube.ui.theme.NotivasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val contenedor = (application as NotivasNubeApplication).container
        setContent {
            NotivasTheme {
                val vm: NotasViewModel = viewModel(
                    factory = viewModelFactory { initializer { NotasViewModel(contenedor.repositorio) } }
                )
                NotasApp(vm)
            }
        }
    }
}
