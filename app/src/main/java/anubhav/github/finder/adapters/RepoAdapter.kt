package anubhav.github.finder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import anubhav.github.finder.data.Repo
import anubhav.github.finder.databinding.RvLayReposBinding

class RepoAdapter(private val onItemClicked: (Repo) -> Unit) :
    RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

    // ArrayList of data items to be
    // displayed in the RecyclerView
    private var data = ArrayList<Repo>()

    // Function to update the data items in the RecyclerView
    // and notify the adapter of changes
    fun setData(data: List<Repo>) {
        this.data = data as ArrayList<Repo>
        notifyDataSetChanged()
    }

    // ViewHolder class for a single item in the RecyclerView
    class ViewHolder(val binding: RvLayReposBinding) : RecyclerView.ViewHolder(binding.root)

    // Called when a new ViewHolder is needed, inflates the layout
    // for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvLayReposBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Called for each item in the list, sets the values for
    // the various UI elements in the ViewHolder layout
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set the name of the repository in the ViewHolder layout
        holder.binding.repoName.text = data[position].name

        // Set the number of stars for the
        // repository in the ViewHolder layout
        var max = 0
        if (data[position].stargazers_count > max) {
            max = data[position].stargazers_count
            holder.binding.noOfStars.text = max.toString()
        }

        // Set the description of the repository in the ViewHolder layout
        holder.binding.description.text = data[position].description

        // Set an OnClickListener on the ViewHolder item view,
        // which triggers the setOnClickListener() function
        // on the setOnGitHubRepoClickListener interface
        // when the item is clicked
        holder.itemView.setOnClickListener {
            onItemClicked(data[position])
        }
    }

    // Returns the number of items
    // in the data ArrayList
    override fun getItemCount(): Int {
        return data.size
    }
}
