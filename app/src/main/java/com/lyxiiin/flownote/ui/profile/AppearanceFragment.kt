package com.lyxiiin.flownote.ui.profile

import android.app.UiModeManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lyxiiin.flownote.FnApplication
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.FragmentProfileLanguageAndAppearanceBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppearanceFragment: Fragment(R.layout.fragment_profile_language_and_appearance) {
    private var _binding: FragmentProfileLanguageAndAppearanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppearanceViewModel by viewModels {
        val app = requireActivity().application as FnApplication
        AppearanceViewModelFactory(app.settingsRepository)
    }

    private lateinit var adapter: AppearanceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileLanguageAndAppearanceBinding.bind(view)

        binding.topBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.topBar.tvTopBarTitle.text = getString(R.string.setting_appearance)

        binding.topBar.btnFinish.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    viewModel.saveThemeMode()
                }finally {
                    findNavController().popBackStack()
                }
            }
        }


        adapter = AppearanceAdapter(
            onItemClick = { option ->
                viewModel.selectThemeMode(option.value)
                adapter.selectedThemeMode = option.value
                adapter.notifyDataSetChanged()
            }
        )

        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.adapter = adapter

        adapter.submitList(
            listOf(
                // 存储值统一使用 UiModeManager 值域：0=跟随系统 1=浅色 2=深色，与 setApplicationNightMode 入参一致
                AppearanceOption(getString(R.string.appearance_follow_system), UiModeManager.MODE_NIGHT_AUTO),
                AppearanceOption(getString(R.string.appearance_light_mode), UiModeManager.MODE_NIGHT_NO),
                AppearanceOption(getString(R.string.appearance_dark_mode), UiModeManager.MODE_NIGHT_YES),
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val theme = viewModel.themeMode.asFlow().first()
            adapter.selectedThemeMode = theme
            adapter.notifyDataSetChanged()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}