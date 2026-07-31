# FlowNote 项目架构规划

## 一、推荐目录结构

```
app/src/main/java/com/lyxiiin/flownote/
├── MainActivity.kt                  # 应用唯一入口 Activity
├── FlowNoteApp.kt                   # 自定义 Application 类（全局初始化）
│
├── data/                            # 数据层 —— 负责数据存取
│   ├── local/
│   │   ├── AppDatabase.kt           # Room 数据库定义
│   │   ├── Converters.kt            # Room 类型转换器（如 Date <-> Long）
│   │   ├── dao/
│   │   │   ├── NoteDao.kt           # 笔记表 DAO
│   │   │   └── TodoDao.kt           # 待办表 DAO
│   │   └── entity/
│   │       ├── Note.kt              # 笔记实体（id, title, content, createdAt, updatedAt）
│   │       ├── Todo.kt              # 待办实体（id, title, isDone, dueDate, noteId?）
│   │       └── Attachment.kt        # 附件实体（id, noteId, filePath, type, createdAt）
│   └── repository/
│       ├── NoteRepository.kt        # 笔记仓库（封装 DAO 调用）
│       └── TodoRepository.kt        # 待办仓库
│
├── ui/                              # UI 层 —— 负责界面展示
│   ├── theme/                       # (已有) 主题配色
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── navigation/
│   │   └── NavGraph.kt              # Navigation Compose 路由定义
│   ├── screens/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt        # 首页（显示"随小记"和"待办"两个 Tab）
│   │   │   └── HomeViewModel.kt     # 首页 ViewModel
│   │   ├── note/
│   │   │   ├── NoteListScreen.kt    # 笔记列表页
│   │   │   ├── NoteEditScreen.kt    # 笔记编辑/新建页
│   │   │   └── NoteViewModel.kt     # 笔记 ViewModel
│   │   ├── todo/
│   │   │   ├── TodoListScreen.kt    # 待办列表页
│   │   │   ├── TodoEditScreen.kt    # 待办编辑/新建页
│   │   │   └── TodoViewModel.kt     # 待办 ViewModel
│   │   └── components/              # 可复用的 UI 组件
│   │       ├── NoteCard.kt          # 笔记卡片
│   │       ├── TodoItem.kt          # 待办条目
│   │       └── AttachmentPreview.kt # 附件预览组件
│   └── util/                        # UI 工具类
│       └── DateFormat.kt            # 日期格式化工具
│
└── di/                              # 依赖注入（简单手动注入即可）
    └── AppContainer.kt              # 提供 Database、Repository 等单例
```

## 二、需要添加的依赖

在 `gradle/libs.versions.toml` 中添加：

```toml
[versions]
room = "2.7.1"
navigationCompose = "2.9.0"
ksp = "2.2.10-2.0.2"     # KSP 版本需匹配 Kotlin 版本

[libraries]
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

在 `app/build.gradle.kts` 中添加 `ksp` 插件和 Room 依赖。

## 三、架构说明（MVVM 分层）

```
用户操作 --> Screen(Compose UI) --> ViewModel --> Repository --> Room DAO --> SQLite
                ^                      |
                |______________________|
                     StateFlow/LiveData 驱动 UI 更新
```

- **Entity（实体）**: 定义数据库表结构
- **DAO（数据访问对象）**: 定义 SQL 增删改查
- **Repository（仓库）**: 封装 DAO，给 ViewModel 提供干净的 API
- **ViewModel**: 持有 UI 状态（StateFlow），调用 Repository，不直接操作数据库
- **Screen（界面）**: 纯 Compose 函数，观察 ViewModel 的状态来渲染

## 四、核心业务流程

### 流程 1：启动应用
```
MainActivity.onCreate()
  -> setContent { FlowNoteTheme { NavGraph } }
    -> NavGraph 默认路由到 HomeScreen
      -> HomeScreen 显示两个 Tab："随小记" | "待办"
```

### 流程 2：新建/编辑笔记
```
HomeScreen 点击"+"按钮
  -> 导航到 NoteEditScreen(noteId = null)
    -> NoteViewModel.loadNote(null)  // 空，表示新建
    -> 用户输入标题、内容、添加附件
    -> 点击保存 -> NoteViewModel.save()
      -> NoteRepository.insert(note) + AttachmentRepository.insert(attachments)
        -> Room DAO 写入数据库
      -> 导航回 NoteListScreen，列表自动刷新（StateFlow）
```

### 流程 3：管理待办
```
HomeScreen 切换到"待办"Tab
  -> TodoListScreen 显示待办列表（按日期排序）
    -> 点击勾选 -> TodoViewModel.toggleDone(todo)
      -> TodoRepository.update(todo.copy(isDone = !isDone))
    -> 左滑删除 -> TodoViewModel.delete(todo)
```

### 流程 4：添加附件
```
NoteEditScreen 点击附件按钮
  -> 调用系统文件选择器 (ActivityResultLauncher)
    -> 获取文件 URI -> 复制到应用内部存储
    -> 创建 Attachment 实体关联到当前 Note
```

## 五、导航结构

```
NavGraph:
  home          -> HomeScreen（主页，含 Tab 切换笔记/待办列表）
  note_edit/{id} -> NoteEditScreen（笔记编辑，id=0 表示新建）
  todo_edit/{id} -> TodoEditScreen（待办编辑，id=0 表示新建）
```

## 六、关于 home_screen.xml

当前 `res/layout/home_screen.xml` 是传统 XML 布局文件。由于项目已选用 Jetpack Compose 作为 UI 方案，这个 XML 文件将不再使用。首页界面会在 `HomeScreen.kt` 中用 Compose 重写，XML 文件可以删除或保留作为设计参考。

## 七、实施顺序

1. **Task 1**: 添加 Room、Navigation Compose、KSP 等依赖
2. **Task 2**: 创建 data 层（Entity、DAO、Database、Repository）
3. **Task 3**: 创建 di/AppContainer 手动依赖注入
4. **Task 4**: 创建 navigation/NavGraph 导航框架
5. **Task 5**: 创建 HomeScreen（主页 + Tab 切换）
6. **Task 6**: 创建 NoteListScreen + NoteEditScreen + NoteViewModel
7. **Task 7**: 创建 TodoListScreen + TodoEditScreen + TodoViewModel
8. **Task 8**: 实现附件功能（文件选择、存储、预览）
9. **Task 9**: 清理不再使用的 XML 布局和模板代码
