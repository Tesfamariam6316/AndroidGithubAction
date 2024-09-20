package org.androidgithub

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface GitHubApi {
    @GET("repos/Tesfamariam6316/AndroidGithubAction/releases/latest")
    suspend fun getLatestRelease(): GitHubRelease
}

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: GitHubApi by lazy {
        retrofit.create(GitHubApi::class.java)
    }
}

data class GitHubRelease(
    val tag_name: String,  // The version tag
    val body: String,       // The release notes (GitHub release body)
    val assets: List<Asset>
) {
    data class Asset(
        val browser_download_url: String,  // URL to the APK file
        val name: String
    )
}

//data class GitHubRelease(
//    val tag_name: String,   // The version tag
//    val body: String,       // The release notes (GitHub release body)
//    val browser_download_url: String  // URL to the APK file
//)
