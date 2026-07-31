package com.lyxiiin.flownote.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lyxiiin.flownote.FnApplication
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home){
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val app = requireActivity().application as FnApplication
        HomeViewModelFactory(app.noteCategoryRepository, app.noteRepository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
//        // 设置避让
//        val root = view.findViewById<View>(R.id.fragment_container)
//        ViewCompat.setOnApplyWindowInsetsListener(root){ view, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            view.setPadding(systemBars.left,systemBars.right,systemBars.top,systemBars.bottom)
//            insets
//        }

        binding.viewPager.adapter = MyPagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
            tab.text = when(position) {
                0 -> "随心记"
                1 -> "待办"
                else -> ""
            }
        }.attach()
        binding.btnAddNoteCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        binding.fabAdd.setOnClickListener {
            // 处理新建笔记/待办的逻辑
//            Toast.makeText(requireContext(), "点击了新建按钮", Toast.LENGTH_SHORT).show()
            val currentPosition = binding.viewPager.currentItem
            if (currentPosition == 0){
                val bundle = bundleOf("noteId" to -1L)
                findNavController().navigate(R.id.action_home_to_note_detail, bundle)
            }else{
                // TODO 待办逻辑
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.insertResult.collect { result ->
                    when (result) {
                        is InsertResult.Success -> Toast.makeText(requireContext(), "分组已添加", Toast.LENGTH_SHORT).show()
                        is InsertResult.Duplicate -> Toast.makeText(requireContext(), "分组名称已存在", Toast.LENGTH_SHORT).show()
                        is InsertResult.Empty -> Toast.makeText(requireContext(), "分组名称不能为空", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAddCategoryDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "请输入分组名称"
            setPadding(64, 32, 64, 32)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("新建分组")
            .setView(editText)
            .setPositiveButton("确定") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.insertCategory(name)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

}

class MyPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NoteFragment()
            1 -> TodoFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}