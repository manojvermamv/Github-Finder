package anubhav.github.finder.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import anubhav.github.finder.R
import anubhav.github.finder.data.FullProfile
import com.bumptech.glide.Glide
import anubhav.github.finder.data.Profile
import anubhav.github.finder.databinding.ActivityProfileDetailsBinding
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

// This is the class that displays
// the details of a repository
class ProfileDetails : AppCompatActivity() {

    companion object {
        fun start(context: Context, item: Profile) {
            context.startActivity(Intent(context, ProfileDetails::class.java).apply {
                putExtra("login", item.login)
                putExtra("url", item.url)
                putExtra("htmlUrl", item.htmlUrl)
                putExtra("avatarUrl", item.avatarUrl)
            })
        }
    }



    private var _binding: ActivityProfileDetailsBinding? = null
    private val binding get() = _binding!!
    private val toolbar: MaterialToolbar get() = binding.toolbar


    private val login by lazy { intent.getStringExtra("login") ?: "" }
    private val url by lazy { intent.getStringExtra("url") ?: "" }
    private val htmlUrl by lazy { intent.getStringExtra("htmlUrl") ?: "" }
    private val avatarUrl by lazy { intent.getStringExtra("avatarUrl") ?: "" }


    private val viewModel by viewModels<GitHubViewModel>()

    // This function is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViews()
        fetchUserData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Handle back button click (optional)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)

        supportActionBar?.title = login
        // Enable the back button (up navigation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // This function initializes the views with the values
    private fun setupViews(mHtmlUrl: String? = null, mAvatarUrl: String? = null, profile: FullProfile? = null) {
        binding.tvDesc.text = profile?.name
        binding.tvMore.text = profile?.bio

        binding.tvOne.text = profile?.location
        binding.layOne.isVisible = (profile?.location != null)

        val followers = (profile?.followers?:0).toString()
        val following = (profile?.following?:0).toString()
        binding.tvTwo.text = resources.getString(R.string.follower_following, followers, following)

        binding.webView.loadUrl(mHtmlUrl?:htmlUrl)
        Glide.with(applicationContext).load(mAvatarUrl?:avatarUrl).into(binding.ownerAvatarImage)
    }

    private fun fetchUserData() {
        viewModel.getUserProfile(login)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUserData().observe(this@ProfileDetails) { data ->
                    setupViews(data?.htmlUrl, data?.avatarUrl, data)
                }
            }
        }
    }

}