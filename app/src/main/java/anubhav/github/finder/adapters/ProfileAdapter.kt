package anubhav.github.finder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import anubhav.github.finder.R
import anubhav.github.finder.helpers.Utils
import anubhav.github.finder.data.Profile
import anubhav.github.finder.databinding.RvLayProfilesBinding
import anubhav.github.finder.global.MyApp
import anubhav.github.finder.utils.copyToClipboard
import anubhav.github.finder.utils.showToast
import java.lang.RuntimeException

class ProfileAdapter(
    private val context: Context,
    private val onItemClicked: (Profile) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    // ArrayList of data items to be
    // displayed in the RecyclerView
    private var data = ArrayList<Profile>()

    private val profileBox = MyApp.boxStore.boxFor(Profile::class.java)

    // Function to update the data items in the RecyclerView
    // and notify the adapter of changes
    fun setData(data: List<Profile>?) {
        this.data = if (data == null) arrayListOf() else (data as ArrayList<Profile>)
        notifyDataSetChanged()
    }

    // ViewHolder class for a single item in the RecyclerView
    class ViewHolder(val binding: RvLayProfilesBinding) : RecyclerView.ViewHolder(binding.root)

    // Called when a new ViewHolder is needed, inflates the layout
    // for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvLayProfilesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Called for each item in the list, sets the values for
    // the various UI elements in the ViewHolder layout
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set the name of the repository in the ViewHolder layout

        val profile = data[position]
        holder.binding.apply {
            Glide.with(context).load(profile.avatarUrl).into(avatar)
            repoName.text = profile.login
            txtGithub.setOnClickListener { performGithubAction(profile, false) }
            txtGithub.setOnLongClickListener { performGithubAction(profile, true) }
            ivGithub.setOnClickListener { performGithubAction(profile, false) }
            ivGithub.setOnLongClickListener { performGithubAction(profile, true) }

            ivFav.setImageResource(if (profile.isSaved()) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline)
            ivFav.setOnClickListener {
                val mProfile = data[position]
                if (mProfile.isSaved()) {
                    profileBox.remove(mProfile)
                } else {
                    profileBox.put(mProfile)
                }
                notifyItemChanged(position)
            }
        }

        holder.itemView.setOnClickListener {
            //data[position].url
            onItemClicked(data[position])
        }
    }

    private fun Profile.isSaved(): Boolean {
        return try {
            profileBox.contains(profileBox.getId(this))
        } catch (e: RuntimeException) {
            false
        }
    }

    private fun performGithubAction(profile: Profile, copyToClipboard: Boolean): Boolean {
        if (copyToClipboard) {
            context.copyToClipboard(profile.htmlUrl?:"")
            context.showToast("Copied to clipboard.")
        } else {
            Utils.openLink(context, profile.htmlUrl?:"")
        }
        return true
    }

    // Returns the number of items in the data ArrayList
    override fun getItemCount() = data.size

}
