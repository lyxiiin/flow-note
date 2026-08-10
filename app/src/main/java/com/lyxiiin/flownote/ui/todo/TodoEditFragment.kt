package com.lyxiiin.flownote.ui.todo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.lyxiiin.flownote.FnApplication
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.FragmentTodoEditBinding
import com.lyxiiin.flownote.util.toDateTimeString
import com.lyxiiin.flownote.util.toUtcStartOfDayMillis
import com.lyxiiin.flownote.util.utcMillisToLocalDate
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

class TodoEditFragment: Fragment(R.layout.fragment_todo_edit) {
    private var _binding: FragmentTodoEditBinding? = null
    private val binding get() = _binding!!

    /** 回填 EditText 期间抑制 TextWatcher 回写 ViewModel，防止 observe↔setText 无限循环 */
    private var isUpdating = false

    private val todoId: Long by lazy {
        requireArguments().getLong("todoId",-1L)
    }

    private val viewModel: TodoEditViewModel by viewModels {
        val app = requireActivity().application as FnApplication
        TodoEditViewModelFactory(todoId, app.todoRepository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTodoEditBinding.bind(view)

        viewModel.title.observe(viewLifecycleOwner){ title ->
            // 仅在文本真正变化时回填，避免输入过程中 setText 将光标重置到文本开头
            if (binding.etTitle.text.toString() != title) {
                isUpdating = true
                binding.etTitle.setText(title)
                // 回填后光标置于文本末尾，防止输入内容插入到最前面
                binding.etTitle.setSelection(title.length)
                isUpdating = false
            }
            updateSaveButtonState(title)
        }

        viewModel.description.observe(viewLifecycleOwner){
            if (binding.etDescription.text.toString() != it) {
                isUpdating = true
                binding.etDescription.setText(it)
                binding.etDescription.setSelection(it.length)
                isUpdating = false
            }
        }

        viewModel.dueDate.observe(viewLifecycleOwner) { due->
            binding.tvDueValue.text = due?.toDateTimeString() ?: "设置截止时间"
            binding.tvDueValue.setTextColor(ContextCompat.getColor(requireContext(),
                if (due  == null) R.color.text_hint else R.color.text_primary))
            binding.btnClearDue.visibility = if (due == null) View.GONE else View.VISIBLE
        }
        binding.etTitle.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (isUpdating) return
                viewModel.updateTitle(p0.toString())
                updateSaveButtonState(p0.toString())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        binding.etDescription.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if (isUpdating) return
                viewModel.updateDescription(p0.toString())
            }
        })


        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }
        binding.btnSave.setOnClickListener { viewModel.save() }
        // 截止时间行：点击整行弹出日期+时间选择器；点击清除按钮置空截止时间
        binding.llDue.setOnClickListener { showDueDatePicker() }
        binding.btnClearDue.setOnClickListener { viewModel.updateDueDate(null) }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.saveResult.collect { success ->
                    if (!success) Toast.makeText(requireContext(), "保存失败，请重试", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun showDueDatePicker() {
        val current = viewModel.dueDate.value
        val datePicker = MaterialDatePicker.Builder.datePicker().apply {
            setTitleText("选择日期")
            // 先把当前时间戳按系统时区拆出日历日期，再按 UTC 0 点组装，供 DatePicker 精确定位选中日期
            current?.let { setSelection(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toUtcStartOfDayMillis()) }
        }.build()
        datePicker.addOnPositiveButtonClickListener { utcMillis ->
            // DatePicker 返回值按 UTC 解释取日历日期，避免东八区日期偏移一天
            val date = utcMillis.utcMillisToLocalDate()
            // 已有截止时间则保留原时刻，否则默认当前时间
            val time = current?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime() }
                ?: LocalTime.now()
            val timePicker = MaterialTimePicker.Builder().setHour(time.hour).setMinute(time.minute).build()
            timePicker.addOnPositiveButtonClickListener {
                // 按系统时区合并日期与时间，转回毫秒时间戳存储
                viewModel.updateDueDate(date.atTime(timePicker.hour, timePicker.minute)
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            }
            timePicker.show(childFragmentManager, "time_picker")
        }
        datePicker.show(childFragmentManager, "date_picker")
    }

    private fun updateSaveButtonState(title: String? = null){
        val t = title ?: binding.etTitle.text?.toString().orEmpty()
        binding.btnSave.isEnabled = t.trim().isNotEmpty()
    }
}