package com.lyxiiin.flownote.ui.note

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.text.TextWatcher
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lyxiiin.flownote.FnApplication
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.FragmentNoteDetailBinding
import com.lyxiiin.flownote.ui.widget.ActionMenuDialog
import com.lyxiiin.flownote.util.toDateTimeString


class NoteDetailFragment: Fragment(R.layout.fragment_note_detail) {
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!

    private var isEditMode = false

    private val noteId: Long by lazy {
        requireArguments().getLong("noteId",-1L)
    }

    private val categoryId: Long by lazy {
        requireArguments().getLong("categoryId",-1L)
    }

    private val viewModel: NoteDetailViewModel by viewModels {
        val app = requireActivity().application as FnApplication
        NoteDetailViewModelFactory(noteId,categoryId,app.noteRepository)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoteDetailBinding.bind(view)
        setUpToolbar()
        setupEditModeToggle()

        viewModel.note.observe(viewLifecycleOwner){ note ->
            note?.let { 
                binding.etTitle.setText(it.title)
                binding.etContent.setText(it.content)
                binding.tvChangedTimeWordCount.text = "${it.updatedAt.toDateTimeString()}  |  ${it.content.length}字"
            }
        }
        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateTitle(s.toString())
            }
        })

        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateContent(s.toString())
            }
        })
    }

    override fun onDestroyView() {
        viewModel.onExit()
        super.onDestroyView()
        _binding = null
    }

    private fun setUpToolbar() {
        binding.btnBack.setOnClickListener {
            if (isEditMode){
                exitEditMode()
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        binding.btnBackgroundChange.setOnClickListener {

        }
        binding.btnMore.setOnClickListener {
            ActionMenuDialog.Builder(requireContext())
                .addItem(R.drawable.ic_move, "移动"){
                    val bundle = bundleOf("noteId" to noteId)
                    findNavController().navigate(R.id.action_note_detail_to_note_move, bundle)
                }
                .addDangerItem(R.drawable.ic_delete, "删除"){
                    viewModel.deleteNote()
                    findNavController().popBackStack()
                }
                .show()
        }
        binding.btnConfirm.setOnClickListener {
            exitEditMode()
        }
    }

    private fun setupEditModeToggle(){
        val clickListener = View.OnClickListener {
            if(!isEditMode){
                enterEditMode()
            }
        }
        binding.etTitle.setOnClickListener(clickListener)
        binding.etContent.setOnClickListener(clickListener)
    }

    private fun enterEditMode() {
        if(isEditMode) return
        isEditMode = true

        binding.toolbarReadMode.visibility = View.GONE
        binding.toolbarEditMode.visibility = View.VISIBLE

        binding.etTitle.isFocusableInTouchMode = true
        binding.etContent.isFocusableInTouchMode = true

        binding.etContent.requestFocus()
        showKeyBoard(binding.etContent)
    }

    private fun exitEditMode() {
        if (!isEditMode) return
        isEditMode = false

        binding.etTitle.clearFocus()
        binding.etContent.clearFocus()
        binding.etTitle.isFocusableInTouchMode = false
        binding.etContent.isFocusableInTouchMode = false

        hideKeyboard()

        binding.toolbarReadMode.visibility = View.VISIBLE
        binding.toolbarEditMode.visibility = View.GONE
    }

    private fun showKeyBoard(view: View){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}