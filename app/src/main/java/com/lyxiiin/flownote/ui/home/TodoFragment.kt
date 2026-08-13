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
import com.lyxiiin.flownote.databinding.FragmentTodoBinding
import com.lyxiiin.flownote.ui.widget.ActionMenuDialog

class TodoFragment: Fragment(R.layout.fragment_todo) {
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val app = requireActivity().application as FnApplication
        HomeViewModelFactory(app.noteCategoryRepository, app.noteRepository,app.todoRepository)
    }

    private lateinit var adapter: TodoListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTodoBinding.bind(view)

        adapter = TodoListAdapter(
            onCheckBoxClick = { todo ->
                viewModel.updateTodo(todo)
            },
            onTodoClick = {todo ->
                val bundle = bundleOf("todoId" to todo.id)
                findNavController().navigate(R.id.action_home_to_todo_edit,bundle)
            },
            onTodoMenuClick = { todo ->
                ActionMenuDialog.Builder(requireContext())
                    .setTitle(todo.title)
                    .addDangerItem(R.drawable.ic_delete, getString(R.string.common_delete)) {
                        viewModel.deleteTodo(todo.id)
                    }
                    .show()
            }
        )
        binding.todoRv.layoutManager = LinearLayoutManager(requireContext())
        binding.todoRv.adapter = adapter

        viewModel.todoList.observe(viewLifecycleOwner){ items ->
            adapter.submitList(items)
            binding.todoRv.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
            binding.tvEmpty.visibility = if(items.isEmpty()) View.VISIBLE else View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}