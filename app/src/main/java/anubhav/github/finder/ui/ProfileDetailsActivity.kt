package anubhav.github.finder.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import anubhav.github.finder.data.FullProfile
import anubhav.github.finder.data.Profile
import anubhav.github.finder.databinding.ActivityProfileDetailsBinding
import anubhav.github.finder.helpers.Utils
import anubhav.github.finder.utils.UtilX
import anubhav.github.finder.utils.copyToClipboard
import anubhav.github.finder.utils.device.Huawei
import anubhav.github.finder.utils.getMutatedIcon
import anubhav.github.finder.utils.sdkAbove
import anubhav.github.finder.utils.showToast
import anubhav.github.finder.utils.initZoomView
import anubhav.github.finder.utils.loadImage
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

import anubhav.github.finder.R as CommonR
import anubhav.github.finder.R.string as stringRes

// This is the class that displays the details of a repository
class ProfileDetailsActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, item: Profile) {
            context.startActivity(Intent(context, ProfileDetailsActivity::class.java).apply {
                putExtra("login", item.login)
                putExtra("htmlUrl", item.htmlUrl)
                putExtra("avatarUrl", item.avatarUrl)
            })
        }
    }



    private var _binding: ActivityProfileDetailsBinding? = null
    private val binding get() = _binding!!
    private val toolbar: MaterialToolbar get() = binding.toolbar


    private val login by lazy { intent.getStringExtra("login") ?: "" }
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
        binding.tvDesc.isGone = profile?.name.isNullOrBlank()

        binding.tvMore.text = profile?.bio
        binding.tvMore.isGone = profile?.bio.isNullOrBlank()

        binding.tvOne.text = profile?.location
        binding.layOne.isGone = profile?.location.isNullOrBlank()

        val followers = UtilX.formatUsersCount((profile?.followers?:0).toLong())
        val following = UtilX.formatUsersCount((profile?.following?:0).toLong())
        binding.tvTwo.text = resources.getString(stringRes.followers_following, followers, following)

        binding.webView.loadUrl(mHtmlUrl?:htmlUrl)
        binding.ownerAvatarImage.loadImage(mAvatarUrl?:avatarUrl)
        binding.zoomView.initZoomView(binding.ownerAvatarImage, mAvatarUrl?:avatarUrl)
    }

    private fun fetchUserData() {
        viewModel.getUserProfile(login)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUserData().observe(this@ProfileDetailsActivity) { data ->
                    setupViews(data?.htmlUrl, data?.avatarUrl, data)
                }
            }
        }
    }

}