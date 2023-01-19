import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.telephony.TelephonyManager
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.iboost.ui.theme.IBoostTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun IBoostApp() {
    val currentScreen = remember { mutableStateOf(0) }

    IBoostTheme {
        when (0) {
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

fun checkNetworkInfo(context: Context) {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
    val isWiFi: Boolean = cm.getNetworkCapabilities(activeNetwork?.network)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    val isMobile: Boolean = cm.getNetworkCapabilities(activeNetwork?.network)?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
    val mobileType: String = when (activeNetwork?.subtype) {
        TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
        TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
        TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
        TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
        TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
        TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
        TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA"
        TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO revision 0"
        TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO revision A"
        TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
        TelephonyManager.NETWORK_TYPE_LTE -> "LTE"
        else -> "Unknown"
    }
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val carrierFrequency: String = if (telephonyManager.carrierFrequencyInfo != null) {
        "${telephonyManager.carrierFrequencyInfo.dlBandwidth} Hz"
    } else {
        "Not available"
    }
    println("Active Network: $activeNetwork")
    println("Connected: $isConnected")
    println("WiFi: $isWiFi")
    println("Mobile: $isMobile")
    println("Mobile Type: $mobileType")
    println("Carrier Frequency: $carrierFrequency")
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
fun SignalStrengthDisplay(context: Context) {
    val signalStrength = remember { mutableStateOf(getSignalStrength(context)) }
    Column {
        Text("Signal Strength: ${signalStrength.value} dBm")
    }
    launch {
        while (true) {
            withContext(Dispatchers.IO) {
                delay(30_000)
            }
            signalStrength.value = getSignalStrength(context)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun HomeScreen(context: Context) {
    checkNetworkInfo(context)
    SignalStrengthDisplay(context)
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        },
        content = {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(it)) {
                Text("Welcome to the home screen!")
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
                RoundMetallicRedButton(onClick = { /*TODO*/ })
            }
        }
    )
}

@Composable
fun RoundMetallicRedButton(
    modifier: Modifier = Modifier.size(60.dp),
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = Color.Red,
        elevation = 6.dp,
        modifier = Modifier
            .width(60.dp)
            .height(60.dp)
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
                Text("This is the Map Screen!")
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