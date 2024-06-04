package anubhav.github.finder.helpers

import anubhav.github.finder.data.FullProfile
import anubhav.github.finder.data.Profile
import anubhav.github.finder.data.Repo
import anubhav.github.finder.data.GithubData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


object RetrofitInstance {
    val githubApi: GithubApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApi::class.java)
    }
}

interface GithubApi {
    @GET("search/repositories?")
    fun getRepos(@Query("q") q: String, @Query("pushed") date: String): Call<GithubData<Repo>>

    @GET("search/users?")
    fun getProfiles(
        @Query("q") search: String,
        @Query("page") pageNumber: Int
    ): Call<GithubData<Profile>>

    @GET("users/")
    fun getUser(
        @Query("q") search: String
    ): Call<ArrayList<FullProfile>>
}