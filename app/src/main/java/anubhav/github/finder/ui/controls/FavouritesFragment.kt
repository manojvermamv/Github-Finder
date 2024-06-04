package anubhav.github.finder.ui.controls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import anubhav.github.finder.adapters.ProfileAdapter
import anubhav.github.finder.databinding.FragFavouritesBinding
import anubhav.github.finder.ui.GitHubViewModel
import anubhav.github.finder.ui.ProfileDetails

class FavouritesFragment() : Fragment() {

    private var _binding: FragFavouritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<GitHubViewModel>()
    private val profileAdapter by lazy {
        ProfileAdapter(requireContext()) { profile ->
            ProfileDetails.start(requireContext(), profile)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragFavouritesBinding.inflate(inflater, container, false)

        // Initialize the views with the values
        initialViews()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getSavedProfiles.collect  { list ->
                    profileAdapter.setData(list)
                }
            }
        }

        return binding.root
    }

    // This function initializes the views with the values
    private fun initialViews() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = profileAdapter
        }
    }

}