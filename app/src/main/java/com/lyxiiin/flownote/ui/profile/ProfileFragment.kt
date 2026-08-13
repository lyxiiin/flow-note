package com.lyxiiin.flownote.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.FragmentProfileBinding

class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        bindSetting()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }


    }

    fun bindSetting(){
        binding.settingLanguage.tvSettingTitle.text = getString(R.string.setting_language)
        binding.settingAppearance.tvSettingTitle.text = getString(R.string.setting_appearance)
        binding.settingNotification.tvSettingTitle.text = getString(R.string.setting_notification)
        binding.settingAbout.tvSettingTitle.text = getString(R.string.setting_about)

        binding.settingLanguage.ivSettingIcon.setImageResource(R.drawable.ic_language)
        binding.settingAppearance.ivSettingIcon.setImageResource(R.drawable.ic_palette)
        binding.settingNotification.ivSettingIcon.setImageResource(R.drawable.ic_notifications)
        binding.settingAbout.ivSettingIcon.setImageResource(R.drawable.ic_info)
        binding.settingNotification.llSettingItem.setOnClickListener {

        }
        binding.settingLanguage.llSettingItem.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_language)
        }
        binding.settingAppearance.llSettingItem.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_appearance)
        }
        binding.settingAbout.llSettingItem.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}