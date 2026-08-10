package com.lyxiiin.flownote.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyxiiin.flownote.data.local.entity.Todo
import com.lyxiiin.flownote.data.repository.TodoRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TodoEditViewModel(
    private val todoId: Long,
    private val todoRepository: TodoRepository
): ViewModel() {
    companion object {
        /** 优先级：低 */
        const val TODO_PRIORITY_LOW = 0

        /** 优先级：中 */
        const val TODO_PRIORITY_MEDIUM = 1

        /** 优先级：高 */
        const val TODO_PRIORITY_HIGH = 2
    }

    /** todoId == -1L 表示新建模式 */
    val isNewTodo = todoId == -1L

    /** 编辑模式的原始数据副本，保存时 copy() 使用；新建模式为 null */
    private var originalTodo: Todo? = null

    // ---- 表单状态（LiveData 驱动 UI 刷新，配置变更后自动恢复）----
    private val _title = MutableLiveData("")
    val title: LiveData<String> = _title

    private val _description = MutableLiveData("")
    val description: LiveData<String> = _description

    /** 截止时间（毫秒），null 表示无截止日期；新建默认当前时间 */
    private val _dueDate = MutableLiveData<Long?>(
        if (isNewTodo) System.currentTimeMillis() else null
    )
    val dueDate: LiveData<Long?> = _dueDate

    /** 优先级，默认中（与布局默认选中"中"一致） */
    private val _priority = MutableLiveData(TODO_PRIORITY_MEDIUM)
    val priority: LiveData<Int> = _priority

    /** 保存中标志，防止重复提交 */
    private val _isSaving = MutableLiveData(false)

    /** 保存结果一次性事件：true 表示保存成功 */
    private val _saveResult = Channel<Boolean>(Channel.BUFFERED)
    val saveResult: Flow<Boolean> = _saveResult.receiveAsFlow()

    init {
        // 编辑模式：一次性读取原任务并回填表单（first() 避免 Flow 重放覆盖用户正在编辑的输入）
        if (!isNewTodo) {
            viewModelScope.launch {
                originalTodo = todoRepository.getTodoById(todoId).first()
                originalTodo?.let {
                    _title.value = it.title
                    _description.value = it.description
                    _dueDate.value = it.dueDate
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _title.value = title
    }

    fun updateDescription(description: String) {
        _description.value = description
    }

    fun updateDueDate(time: Long?) {
        _dueDate.value = time
    }

    fun updatePriority(priority: Int) {
        _priority.value = priority
    }

    fun save() {
        val title = _title.value.orEmpty().trim()
        if (title.isEmpty()) return
        if (_isSaving.value == true) return
        _isSaving.value = true

        // NonCancellable：防止用户保存后立即返回导致写入协程被取消、数据丢失
        viewModelScope.launch(NonCancellable) {
            val success = if (isNewTodo) {
                todoRepository.insertTodo(
                    Todo(
                        title = title,
                        description = _description.value.orEmpty().trim(),
                        dueDate = _dueDate.value
                    )
                ).isSuccess
            } else {
                originalTodo?.let { t ->
                    todoRepository.updateTodo(
                        t.copy(
                            title = title,
                            description = _description.value.orEmpty().trim(),
                            dueDate = _dueDate.value,
                            updatedAt = System.currentTimeMillis()
                        )
                    ).isSuccess
                } ?: false
            }
            _saveResult.send(success)
        }
    }
}
