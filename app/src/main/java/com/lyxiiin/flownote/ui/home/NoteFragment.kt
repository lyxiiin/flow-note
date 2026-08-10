package com.lyxiiin.flownote.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lyxiiin.flownote.FnApplication
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import com.lyxiiin.flownote.databinding.FragmentNoteBinding
import com.lyxiiin.flownote.ui.widget.ActionMenuDialog
import com.lyxiiin.flownote.ui.widget.showRenameDialog

class NoteFragment : Fragment(R.layout.fragment_note) {
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val app = requireActivity().application as FnApplication
        HomeViewModelFactory(app.noteCategoryRepository, app.noteRepository,app.todoRepository)
    }

    private lateinit var adapter: NoteListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteBinding.bind(view)

        adapter = NoteListAdapter(
            onNoteClick = { note ->
                val bundle = bundleOf("noteId" to note.id)
                findNavController().navigate(R.id.action_home_to_note_detail,bundle)
            },
            onCategoryClick = { category ->
                val bundle = bundleOf("categoryId" to category.id)
                findNavController().navigate(R.id.action_home_to_note_category_detail, bundle)
            },
            onCategoryMenuClick = {category ->
                ActionMenuDialog.Builder(requireContext())
                    .setTitle(category.name)
                    .addItem(R.drawable.ic_edit, "重命名"){
                        showRenameDialog(
                            dialogTitle = "重命名分组",
                            hint = "请输入新的分组名称",
                            currentName = category.name
                        ) { newName ->
                            viewModel.renameCategory(
                                category.copy(name = newName, updatedAt = System.currentTimeMillis())
                            )
                        }
                    }
                    .addItem(R.drawable.ic_dissolve, "解散分组"){
                        viewModel.dismissCategory(category)
                    }
                    .addDangerItem(R.drawable.ic_delete, "删除"){
                        viewModel.deleteCategory(category.id)
                    }
                    .show()
            },
            // 笔记菜单：重命名 + 删除
            onNoteMenuClick = { note ->
                ActionMenuDialog.Builder(requireContext())
                    .setTitle(note.title)
                    .addItem(R.drawable.ic_edit, "重命名") {
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
                    .addItem(R.drawable.ic_move, "移动") {
                        val bundle = bundleOf("noteId" to note.id)
                        findNavController().navigate(R.id.action_home_to_note_move,bundle)
                    }
                    .addDangerItem(R.drawable.ic_delete, "删除笔记") {
                        viewModel.deleteNote(note.id)
                    }
                    .show()
            }
        )
        binding.noteRv.layoutManager = LinearLayoutManager(requireContext())
        binding.noteRv.adapter = adapter

        val categories = viewModel.allNoteCategories
        val notes = viewModel.ungroupedNote

        var latestCategories: List<NoteCategoryWithCount> = emptyList()
        var latestNotes: List<Note> = emptyList()

        fun margeAndSubmit(){
            val items = latestCategories.map { NoteListItem.Category(it) } +
                    latestNotes.map { NoteListItem.UngroupedNote(it) }
            adapter.submitList(items)
            binding.noteRv.visibility = if(items.isEmpty()) View.GONE else View.VISIBLE
            binding.tvEmpty.visibility = if(items.isEmpty()) View.VISIBLE else View.GONE
        }


        categories.observe(viewLifecycleOwner) { list ->
            latestCategories = list.orEmpty()
            margeAndSubmit()
        }
        notes.observe(viewLifecycleOwner){ list ->
            latestNotes = list.orEmpty()
            margeAndSubmit()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}