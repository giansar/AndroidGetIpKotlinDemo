package id.giansar.demo

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import id.giansar.demo.component.pullrefresh.PullRefreshIndicator
import id.giansar.demo.component.pullrefresh.pullRefresh
import id.giansar.demo.component.pullrefresh.rememberPullRefreshState
import id.giansar.demo.ui.theme.GetIPTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GetIPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(applicationContext)
                }
            }
        }
    }
}

fun getIp(applicationContext: Context): String {
    try {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val linkAddresses =
            connectivityManager?.getLinkProperties(connectivityManager.activeNetwork)?.linkAddresses
        val ipV4 = linkAddresses?.firstOrNull { linkAddress ->
            linkAddress.address.hostAddress.contains(".") ?: false
        }?.address?.hostAddress
        Log.d("ipV4", ipV4.toString())
        return ipV4 ?: "Not Found"
    } catch (e: Exception) {
        Log.e("errorGetIp", e.toString())
        return "Not Found"
    }
}

@Composable
fun Greeting(applicationContext: Context, modifier: Modifier = Modifier) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableIntStateOf(15) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        itemCount += 5
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        modifier = Modifier.pullRefresh(state)
    ) {
        LazyColumn(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!refreshing) {
                item {
                    Text(
                        text = "Your IP Address is\n${getIp(applicationContext)}",
                        modifier = modifier,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
}