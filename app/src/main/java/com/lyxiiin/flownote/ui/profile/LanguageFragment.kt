package com.lyxiiin.flownote.ui.profile

import android.content.res.Resources
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
import java.util.Locale

class LanguageFragment: Fragment(R.layout.fragment_profile_language_and_appearance) {
    private var _binding: FragmentProfileLanguageAndAppearanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LanguageViewModel by viewModels {
        val app = requireActivity().application as FnApplication
        LanguageViewModelFactory(app.settingsRepository)
    }

    private lateinit var adapter: LanguageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileLanguageAndAppearanceBinding.bind(view)

        // 顶栏标题与返回
        binding.topBar.tvTopBarTitle.text = getString(R.string.setting_language)
        binding.topBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.topBar.btnFinish.setOnClickListener {
            // 等待 DataStore 写入完成后再返回上一页：saveLanguage 是挂起操作，
            // 若先 popBackStack，ViewModel 随页面销毁会取消写入协程，导致语言不生效
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    viewModel.saveLanguage()
                } finally {
                    findNavController().popBackStack()
                }
            }
        }

        adapter = LanguageAdapter(
            onItemClick = { option ->
                // 点击仅更新临时选中（勾选图标），持久化延迟到 Finish 时进行
                viewModel.selectLanguage(option.value)
                adapter.selectedLanguageValue = option.value
                adapter.notifyDataSetChanged()
            }
        )
        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.adapter = adapter
        // 写死的常用语言列表
        adapter.submitList(
            listOf(
                // 跟随系统：副标题展示当前系统语言名（以当前界面语言呈现）
                LanguageOption(
                    getString(R.string.language_follow_system),
                    "system",
                    Resources.getSystem().configuration.locales[0]
                        .getDisplayName(Locale.getDefault())
                ),
                LanguageOption("中文简体", "zh-CN"),
                LanguageOption("繁體中文", "zh-TW"),
                LanguageOption("English", "en-US"),
                LanguageOption("日本語", "ja-JP"),
                LanguageOption("한국어", "ko-KR"),
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            val language = viewModel.language.asFlow().first()
            adapter.selectedLanguageValue = language
            adapter.notifyDataSetChanged()
        }

//        // 观察当前语言设置，同步勾选图标
//        viewModel.language.observe(viewLifecycleOwner) { language ->
//            adapter.selectedLanguageValue = language
//            adapter.notifyDataSetChanged()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}