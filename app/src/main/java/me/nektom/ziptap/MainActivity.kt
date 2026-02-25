package me.nektom.ziptap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.nektom.ziptap.compose.ui.Block
import me.nektom.ziptap.compose.ui.PlainText
import me.nektom.ziptap.compose.ui.ZipTapBaseText
import me.nektom.ziptap.ui.theme.ZipTapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            ZipTapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {
                        Greeting(
                            name = "Android",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {

        ZipTapBaseText {

            Block {
                PlainText("1")
            }
            Block {
                PlainText("2")
            }
            Block {
                PlainText("3")
            }
            Block {
                PlainText("4")
            }
            Block {
                PlainText("5")
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ZipTapTheme {
        Greeting("Android")
    }
}