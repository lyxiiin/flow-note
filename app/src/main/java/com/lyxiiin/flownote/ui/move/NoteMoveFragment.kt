package com.lyxiiin.flownote.ui.move

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lyxiiin.flownote.FnApplication
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.FragmentNoteMoveBinding
import kotlinx.coroutines.launch

class NoteMoveFragment : Fragment(R.layout.fragment_note_move) {
    private var _binding: FragmentNoteMoveBinding? = null
    private val binding get() = _binding!!

    private val noteId: Long by lazy {
        requireArguments().getLong("noteId", -1L)
    }

    private val viewModel: NoteMoveViewModel by viewModels {
        val app = requireActivity().application as FnApplication
        NoteMoveViewModelFactory(noteId, app.noteRepository, app.noteCategoryRepository)
    }
    private lateinit var adapter: NoteMoveAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteMoveBinding.bind(view)

        adapter = NoteMoveAdapter(
            onItemClick = { category ->
                // 先提交移动，等待异步结果返回后再关闭页面，
                // 避免 ViewModel 协程被销毁导致移动不生效
                viewModel.changeNoteCategory(category.id)
            }
        )
        binding.categoryRv.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryRv.adapter = adapter

        // 返回按钮
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 观察当前笔记，根据其所在分组显示对应的勾选图标
        viewModel.note.observe(viewLifecycleOwner) { note ->
            adapter.selectedCategoryId = note?.categoryId
            adapter.notifyDataSetChanged()
        }

        // 观察分组列表
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }

        // 观察移动结果：等待移动完成后返回上一页
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.moveResult.collect { success ->
                    if (!success) {
                        Toast.makeText(requireContext(), "移动失败，请重试", Toast.LENGTH_SHORT).show()
                    }
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}