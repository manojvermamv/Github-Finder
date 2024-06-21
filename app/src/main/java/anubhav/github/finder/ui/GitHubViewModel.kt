package anubhav.github.finder.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.objectbox.kotlin.flow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import anubhav.github.finder.data.FullProfile
import anubhav.github.finder.data.GithubData
import anubhav.github.finder.data.Profile
import anubhav.github.finder.data.Profile_
import anubhav.github.finder.data.Repo
import anubhav.github.finder.data.SearchOrder
import anubhav.github.finder.global.MyApp
import anubhav.github.finder.helpers.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GitHubViewModel : ViewModel() {

    // Create a MutableLiveData object to
    // hold the list of GitHub repositories or profiles
    private var githubReposData = MutableLiveData<List<Repo>>()
    private var githubProfilesData = MutableLiveData<List<Profile>?>()
    private var githubUserData = MutableLiveData<FullProfile?>()

    // Fetch the list of Kotlin repositories
    // from the GitHub API using Retrofit
    fun getAllRepositories() {
        RetrofitInstance.githubApi.getRepos("language:kotlin", ">2022-12-19")
            .enqueue(object : Callback<GithubData<Repo>> {
                override fun onResponse(call: Call<GithubData<Repo>>, response: Response<GithubData<Repo>>) {
                    // If the API call is successful and the response body is not null,
                    // set the value of githubLiveData to the list of repositories returned by the API
                    if (response.body() != null) {
                        response.body()!!.items?.let { githubReposData.value = it }
                    } else {
                        return
                    }
                }

                // If the API call fails, log the error message using Logcat
                override fun onFailure(call: Call<GithubData<Repo>>, t: Throwable) {
                    Log.d("TAG", t.message.toString())
                }
            })
    }

    // Fetch the list of Kotlin repositories
    // from the GitHub API using Retrofit
    fun getAllProfiles(nameOrLocation: String, order: SearchOrder, page: Int = 1) {
        val query = if (order == SearchOrder.LOCATION) {
            "location:${Uri.encode(nameOrLocation.ifBlank { "Delhi" })}"
        } else {
            nameOrLocation.ifBlank { "location:Delhi" }
        }

        println("getAllProfiles: $query")
        RetrofitInstance.githubApi.getProfiles(query, page)
            .enqueue(object : Callback<GithubData<Profile>> {
                override fun onResponse(call: Call<GithubData<Profile>>, response: Response<GithubData<Profile>>) {
                    // If the API call is successful and the response body is not null,
                    // set the value of githubLiveData to the list of repositories returned by the API
                    githubProfilesData.value = response.body()?.items
                }

                // If the API call fails, log the error message using Logcat
                override fun onFailure(call: Call<GithubData<Profile>>, t: Throwable) {
                    githubProfilesData.value = null
                    Log.d("TAG", t.message.toString())
                }
            })
    }

    fun getUserProfile(username: String) {
        RetrofitInstance.githubApi.getUser(username)
            .enqueue(object : Callback<FullProfile> {
                override fun onResponse(call: Call<FullProfile>, response: Response<FullProfile>) {
                    // If the API call is successful and the response body is not null,
                    // set the value of githubLiveData to the list of repositories returned by the API
                    githubUserData.value = response.body()
                }

                // If the API call fails, log the error message using Logcat
                override fun onFailure(call: Call<FullProfile>, t: Throwable) {
                    githubUserData.value = null
                    Log.d("getUserProfile", t.message.toString())
                }
            })
    }

    // Expose the list of repositories as LiveData
    // so it can be observed by the UI
    fun observeRepositoriesData(): LiveData<List<Repo>> {
        return githubReposData
    }
    fun observeProfilesData(): LiveData<List<Profile>?> {
        return githubProfilesData
    }
    fun observeUserData(): LiveData<FullProfile?> {
        return githubUserData
    }


    /**
     * Fetch from Local database
     * */

    private val profileBox by lazy { MyApp.boxStore.boxFor(Profile::class.java) }

    @OptIn(ExperimentalCoroutinesApi::class)
    var getSavedProfiles = profileBox.query().order(Profile_.login).build().flow()


    /*
    * */
    val onUpdateSearchQuery = MutableLiveData<Pair<String, SearchOrder>>()
    fun updateSearchQuery(query: String, order: SearchOrder) {
        onUpdateSearchQuery.value = Pair(query, order)
    }

}
