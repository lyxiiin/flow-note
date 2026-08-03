package com.lyxiiin.flownote.ui.category

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lyxiiin.flownote.FnApplication
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.FragmentNoteCategoryDetailBinding
import com.lyxiiin.flownote.ui.widget.ActionMenuDialog
import com.lyxiiin.flownote.ui.widget.showRenameDialog

class NoteCategoryDetailFragment: Fragment(R.layout.fragment_note_category_detail) {
    private var _binding: FragmentNoteCategoryDetailBinding? = null
    private val binding get() = _binding!!
    private val categoryId: Long by lazy {
        requireArguments().getLong("categoryId", -1L)
    }
    private val viewModel: NoteCategoryDetailViewModel by viewModels{
        val app = requireActivity().application as FnApplication
        NoteCategoryDetailViewModelFactory(categoryId,
            app.noteCategoryRepository,
            app.noteRepository)
    }

    private lateinit var adapter: NoteCategoryDetailAdapter

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteCategoryDetailBinding.bind(view)
        adapter = NoteCategoryDetailAdapter(
            onNoteClick = { note ->
                val bundle = bundleOf("noteId" to note.id, "categoryId" to categoryId)
                findNavController().navigate(R.id.action_note_Category_to_note_Detail, bundle)
            },
            onNoteMenuClick = { note ->
                ActionMenuDialog.Builder(requireContext())
                    .setTitle(note.title)
                    .addItem(R.drawable.ic_edit,"重命名"){
                        showRenameDialog(
                            dialogTitle = "重命名笔记",
                            hint = "请输入新的笔记标题",
                            currentName = note.title
                        ) { newName ->
                            viewModel.renameNote(
                                note.copy(title = newName, updatedAt = System.currentTimeMillis())
                            )
                        }
                    }
                    .addItem(R.drawable.ic_move,"移动"){

                    }
                    .addDangerItem(R.drawable.ic_delete, "删除"){
                        viewModel.deleteNote(note.id)
                    }
                    .show()

            }
        )
        binding.rvCategoryNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategoryNotes.adapter = adapter

        // 设置title
        viewModel.category.observe(viewLifecycleOwner) { category ->
            binding.tvCategoryTitle.text = category?.name ?: "Unknown"
        }

        // 观察笔记列表，更新列表、数量和空状态
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
            binding.tvNoteCount.text = "共 ${notes.size} 篇笔记"
            binding.tvEmpty.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
        }

        // 点击更多按钮
        binding.btnCategoryMore.setOnClickListener {
            ActionMenuDialog.Builder(requireContext())
                .addItem(R.drawable.ic_dissolve, "解散分组"){
                    viewModel.dismissCategory()
                }
                .addDangerItem(R.drawable.ic_delete, "删除"){
                    viewModel.deleteCategory()
                }
                .show()
        }

        // 处理返回按钮
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.fabAddNote.setOnClickListener {
            val bundle = bundleOf("noteId" to -1L, "categoryId" to categoryId)
            findNavController().navigate(R.id.action_note_Category_to_note_Detail, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
