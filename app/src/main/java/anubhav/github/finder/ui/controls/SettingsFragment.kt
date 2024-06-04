package anubhav.github.finder.ui.controls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import anubhav.github.finder.databinding.FragFavouritesBinding

class SettingsFragment() : Fragment() {

    private var _binding: FragFavouritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragFavouritesBinding.inflate(inflater, container, false)

        return binding.root
    }

}