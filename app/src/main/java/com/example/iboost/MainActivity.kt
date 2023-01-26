package com.example.iboost

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.iboost.ui.theme.IBoostTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var textView: TextView
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IBoostTheme {
                IBoostApp()
            }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun IBoostApp() {
    val currentScreen = remember { mutableStateOf(0) }

    IBoostTheme {
        when (currentScreen.value) {
            0 -> HomeScreen()
            1 -> BoostScreen()
            2 -> MapScreen()
        }
        BottomNavigation(backgroundColor = Color.White) {
            BottomNavigationItem(
                icon = {},
                label = { Text("Home") },
                selected = currentScreen.value == 0,
                onClick = { currentScreen.value = 0 }
            )
            BottomNavigationItem(
                icon = { },
                label = { Text("Boost") },
                selected = currentScreen.value == 1,
                onClick = { currentScreen.value = 1 }
            )
            BottomNavigationItem(
                icon = {},
                label = { Text("Map") },
                selected = currentScreen.value == 2,
                onClick = { currentScreen.value = 2 }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun getSignalStrength(context: Context): Int {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
    return networkCapabilities?.signalStrength ?: -1
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun SignalStrengthDisplay() {
    val context = LocalContext.current
    val signalStrength = remember { mutableStateOf(getSignalStrength(context)) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(key1 = true) {
        while (true) {
            withContext(Dispatchers.IO) {
                delay(30_000)
            }
            signalStrength.value = getSignalStrength(context)
        }
    }
    Column {
        Text("Signal Strength: ${signalStrength.value} dBm")
    }

}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        },
        content = {
            Column {
                val image = painterResource(R.drawable.iboost_2)
                Box {
                    Image(painter = image,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(Alignment.Top),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)

                ) {
                    SignalStrengthDisplay()
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    )
}

@Composable
fun BoostScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Boost Screen") })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .padding(it)
            ) {
                Button(onClick = { /*TODO*/ })
            }
        }
    )
}

@Composable
fun Button(
    modifier: Modifier = Modifier.size(60.dp),
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.Red,
        elevation = 2.dp,
        modifier = Modifier
            .width(70.dp)
            .height(70.dp)
    ) {
        Text("Boost")
    }
    // OnClick event
    onClick()
}

@Composable
fun MapScreen() {
    Scaffold(
        topBar = {
            TopAppBar({ TopAppBar(title = { Text("Map Screen") }) })
        },
        content = {
            MyContent()
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
            }
        }
    )
}

@Composable
fun MyContent(){

    val mUrl = "https://www.geeksforgeeks.org"

    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            loadUrl(mUrl)
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}

@Preview
@Composable
fun MapScreenPreview() {
    IBoostTheme {
        MapScreen()
    }
}

@Preview
@Composable
fun BoostScreenPreview() {
    IBoostTheme {
        BoostScreen()
    }
}