package anubhav.github.finder.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import anubhav.github.finder.data.Profile
import anubhav.github.finder.databinding.ActivityProfileDetailsBinding

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

    private lateinit var binding: ActivityProfileDetailsBinding // View binding for the activity

    private val login by lazy { intent.getStringExtra("login") ?: "" }
    private val htmlUrl by lazy { intent.getStringExtra("htmlUrl") ?: "" }
    private val avatarUrl by lazy { intent.getStringExtra("avatarUrl") ?: "" }

    // This function is called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the views with the values
        initialViews()
    }

    // This function initializes the views with the values
    private fun initialViews() {
        binding.btnBack.setOnClickListener { finish() }

        binding.tvName.text = login
        //binding.starCount.text = "$starCount Stars"
        binding.webView.loadUrl(htmlUrl)
        Glide.with(applicationContext).load(avatarUrl).into(binding.ownerAvatarImage)
    }
}