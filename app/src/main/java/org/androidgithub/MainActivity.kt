package org.androidgithub

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.androidgithub.ui.theme.AndroidGithubActionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidGithubActionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),  // Makes the column fill the available space
                        verticalArrangement = Arrangement.Center,  // Centers content vertically
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CheckForUpdateScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidGithubActionTheme {
        Greeting("Android")
    }
}

@Composable
fun CheckForUpdateScreen() {
    val scope = rememberCoroutineScope()
    var latestVersion by remember { mutableStateOf<String?>(null) }
    var downloadUrl by remember { mutableStateOf<String?>(null) }
    var releaseNote by remember { mutableStateOf<String?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    // Fetch latest release on app start
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val release = RetrofitInstance.api.getLatestRelease()
//                Log.d("GitHubRelease", "Release fetched: ${release.tag_name}, ${release.body}, ${release.browser_download_url}")

                val currentVersion = BuildConfig.VERSION_NAME
                if (release.tag_name != currentVersion) {
                    latestVersion = release.tag_name
                    releaseNote = release.body
//                    downloadUrl = release.browser_download_url  // Use the direct browser_download_url
                    downloadUrl = release.assets.firstOrNull { it.name.endsWith(".apk") }?.browser_download_url
                    showBottomSheet = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // UI with bottom sheet for new version
//    if (showBottomSheet && latestVersion != null && downloadUrl != null) {
//        UpdateBottomSheet(latestVersion!!, downloadUrl!!)
//    } else {
//        // Regular app content
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 50.dp),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.CenterHorizontally
//
//        ) {
//            Text("Welcome to the app!")
//            Text("Version Name : ${BuildConfig.VERSION_NAME}")
//            Text("Version Code : ${BuildConfig.VERSION_CODE}")
//
//        }
//    }

    //
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the app!")
        Text("Version Name : ${BuildConfig.VERSION_NAME}")
        Text("Version Code : ${BuildConfig.VERSION_CODE}")
        if (showBottomSheet && latestVersion != null && downloadUrl != null) {
            UpdateBottomSheet(latestVersion!!, downloadUrl!!, releaseNote!!)
        }
    }
    //
}

@Composable
fun UpdateBottomSheet(latestVersion: String, downloadUrl: String, releaseTxt: String) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 160.dp),
            verticalArrangement = Arrangement.Center,  // Centers content vertically
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("A new version ($latestVersion) is available!")
            Text("($releaseTxt)")

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                scope.launch {
                    // Handle APK download here
                    downloadNewVersion(downloadUrl, context)
                }
            }) {
                Text("Download and Update")
            }
        }
    }
}

fun downloadNewVersion(downloadUrl: String, context: Context) {
    val request = DownloadManager.Request(Uri.parse(downloadUrl))
        .setTitle("Downloading New Version")
        .setDescription("Downloading APK")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app-release.apk")

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}