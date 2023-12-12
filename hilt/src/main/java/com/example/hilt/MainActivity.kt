package com.example.hilt

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.hilt.models.DataHolder
import com.example.hilt.models.MainViewModel
import com.example.hilt.models.User
import com.example.hilt.theme.HiltApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var user1: User

    @Inject
    lateinit var user2: User

    @Inject
    lateinit var app: Application

    @Inject
    lateinit var dataHolder: DataHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 获取到 view model 对象的时候，就自动完成了依赖注入
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            HiltApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(
                        arrayListOf(
                            user1,
                            user2,
                            app,
                            dataHolder,
                            viewModel,
                        ).joinToString(separator = "\n")
                    ) {
                        startActivity(Intent(this, SecondActivity::class.java))
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, click: () -> Unit) {
    Column {
        Text(
            text = "Hello $name",
            modifier = modifier
        )
        Button(onClick = click) {
            Text("Click")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HiltApplicationTheme {
        Greeting("Android") {
            // do nothing for preview.
        }
    }
}