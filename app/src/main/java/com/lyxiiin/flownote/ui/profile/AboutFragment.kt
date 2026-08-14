package com.lyxiiin.flownote.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lyxiiin.flownote.BuildConfig
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.FragmentAboutBinding
import androidx.core.net.toUri

class AboutFragment : Fragment(R.layout.fragment_about) {
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutBinding.bind(view)

        // 顶栏：标题"关于"，本页无完成动作，隐藏右侧按钮
        binding.topBar.tvTopBarTitle.text = getString(R.string.setting_about)
        binding.topBar.btnFinish.visibility = View.GONE
        binding.topBar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 版本号动态读取 BuildConfig，升级版本后自动跟随，无需改文案
        binding.tvAppVersion.text = getString(R.string.about_version_format, BuildConfig.VERSION_NAME)

        bindAboutItems()
    }

    private fun bindAboutItems() {
        binding.aboutPrivacyPolicy.tvSettingTitle.text = getString(R.string.about_privacy_policy)
        binding.aboutPrivacyPolicy.ivSettingIcon.setImageResource(R.drawable.ic_info)
        binding.aboutPrivacyPolicy.llSettingItem.setOnClickListener {
            openBrowser(BuildConfig.PRIVACY_POLICY_URL)
        }

        binding.aboutTermsOfService.tvSettingTitle.text = getString(R.string.about_terms_of_service)
        binding.aboutTermsOfService.ivSettingIcon.setImageResource(R.drawable.ic_description)
        binding.aboutTermsOfService.llSettingItem.setOnClickListener {
            openBrowser(BuildConfig.TERMS_OF_SERVICE_URL)
        }

        binding.aboutOpenSourceLicenses.tvSettingTitle.text = getString(R.string.about_open_source_licenses)
        binding.aboutOpenSourceLicenses.ivSettingIcon.setImageResource(R.drawable.ic_code)
        binding.aboutOpenSourceLicenses.llSettingItem.setOnClickListener {
            openBrowser(BuildConfig.SOURCE_REPO_URL)
        }

        binding.aboutRateUs.tvSettingTitle.text = getString(R.string.about_rate_us)
        binding.aboutRateUs.ivSettingIcon.setImageResource(R.drawable.ic_star)
        binding.aboutRateUs.llSettingItem.setOnClickListener {
            openBrowser(BuildConfig.STORE_PAGE_URL)
        }

        binding.aboutShare.tvSettingTitle.text = getString(R.string.about_share)
        binding.aboutShare.ivSettingIcon.setImageResource(R.drawable.ic_share)
        binding.aboutShare.llSettingItem.setOnClickListener {
            shareApp()
        }

        binding.aboutContactDeveloper.tvSettingTitle.text = getString(R.string.about_contact_developer)
        binding.aboutContactDeveloper.ivSettingIcon.setImageResource(R.drawable.ic_mail)
        binding.aboutContactDeveloper.llSettingItem.setOnClickListener {
            openBrowser("mailto:${BuildConfig.SUPPORT_EMAIL}")
        }
    }

    /** 打开外部链接：地址占位为空或无法解析时静默失败 */
    private fun openBrowser(url: String) {
        val uri = url.toUri()
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    /** 分享应用：系统分享面板发送推荐文案 */
    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.about_share_text))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.about_share)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
