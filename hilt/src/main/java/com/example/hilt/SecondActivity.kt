package com.example.hilt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.hilt.models.DataHolder
import com.example.hilt.models.User
import com.example.hilt.theme.HiltApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SecondActivity : ComponentActivity() {

    @Inject
    lateinit var user1: User

    @Inject
    lateinit var user2: User

    @Inject
    lateinit var dataHolder: DataHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HiltApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GreetingSecond(
                        arrayListOf(
                            user1,
                            user2,
                            dataHolder,
                        ).joinToString(separator = "\n")
                    )
                }
            }
        }
    }
}

@Composable
fun GreetingSecond(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingSecondPreview() {
    HiltApplicationTheme {
        GreetingSecond("Android")
    }
}