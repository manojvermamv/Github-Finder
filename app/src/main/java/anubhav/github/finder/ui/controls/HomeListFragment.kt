package anubhav.github.finder.ui.controls

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import anubhav.github.finder.R
import anubhav.github.finder.adapters.ProfileAdapter
import anubhav.github.finder.data.SearchOrder
import anubhav.github.finder.databinding.FragHomeBinding
import anubhav.github.finder.global.MyApp
import anubhav.github.finder.ui.GitHubViewModel
import anubhav.github.finder.ui.ProfileDetailsActivity
import anubhav.github.finder.utils.initZoomView
import anubhav.github.finder.utils.isFirstItemVisible
import anubhav.github.finder.utils.systemBarsMargin
import kotlinx.coroutines.launch

class HomeListFragment() : Fragment() {

    companion object {
        private const val STATE_LAYOUT_MANAGER = "layoutManager"
        private const val EXTRA_SOURCE = "source"
    }


    private var _binding: FragHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<GitHubViewModel>()
    private var layoutManagerState: Parcelable? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: ProfileAdapter

    private val shortAnimationDuration by lazy { resources.getInteger(android.R.integer.config_shortAnimTime) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragHomeBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            isMotionEventSplittingEnabled = false
            setHasFixedSize(true)
            recyclerViewAdapter = ProfileAdapter(requireContext()) { profile, isImageClick ->
                if (isImageClick) {
                    binding.zoomView.initZoomView(profile.avatarUrl?:"")
                } else {
                    ProfileDetailsActivity.start(requireContext(), profile)
                }
            }
            adapter = recyclerViewAdapter
        }

        viewModel.onUpdateSearchQuery.observe(viewLifecycleOwner) { pair ->
            pair?.let {
                val (query, order) = pair
                fetchData(query, order)
            }
        }

        val fab = binding.scrollUp
        with(fab) {
            text = ""
            setIconResource(R.drawable.arrow_up)
            setOnClickListener { recyclerView.smoothScrollToPosition(0) }
            alpha = 0f
            visibility = View.VISIBLE
            systemBarsMargin(16)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            recyclerView.isFirstItemVisible.collect { showFab ->
                fab.animate()
                    .alpha(if (!showFab) 1f else 0f)
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManagerState = savedInstanceState?.getParcelable(STATE_LAYOUT_MANAGER)
        layoutManagerState?.let {
            layoutManagerState = null
            recyclerView.layoutManager?.onRestoreInstanceState(it)
        }

        fetchData("", MyApp.preference.searchOrderType())
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.observeProfilesData().observe(viewLifecycleOwner) { data ->
                        recyclerViewAdapter.setData(data)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        (layoutManagerState ?: recyclerView.layoutManager?.onSaveInstanceState())
            ?.let { outState.putParcelable(STATE_LAYOUT_MANAGER, it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchData(query: String, order: SearchOrder) {
        viewModel.getAllProfiles(query.trim(), order)
    }

    private fun showSortMenu(view: View, callback: (String) -> Unit) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.bottom_nav_menu, popup.menu)
        popup.menu.getItem(0).setChecked(true)
        view.setOnClickListener {
            popup.show()
            popup.setOnMenuItemClickListener { item: MenuItem ->
                for (i in 0 until popup.menu.size()) {
                    popup.menu.getItem(i).setChecked(false)
                }
                item.setChecked(true)
                callback.invoke(item.title.toString().trim())
                true
            }
        }
    }

}
