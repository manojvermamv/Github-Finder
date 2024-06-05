package anubhav.github.finder.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import anubhav.github.finder.data.Profile
import anubhav.github.finder.databinding.ActivityProfileDetailsBinding
import com.google.android.material.appbar.MaterialToolbar

// This is the class that displays
// the details of a repository
class ProfileDetails : AppCompatActivity() {

    companion object {
        fun start(context: Context, item: Profile) {
            context.startActivity(Intent(context, ProfileDetails::class.java).apply {
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

    // This function is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViews()
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
    private fun setupViews() {
        //binding.starCount.text = "$starCount Stars"
        binding.webView.loadUrl(htmlUrl)
        Glide.with(applicationContext).load(avatarUrl).into(binding.ownerAvatarImage)
    }

}