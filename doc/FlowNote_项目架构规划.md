# FlowNote 项目架构规划

## 一、推荐目录结构

```
app/src/main/java/com/lyxiiin/flownote/
├── MainActivity.kt                  # 应用唯一入口 Activity（承载 NavHostFragment）
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
├── ui/                              # UI 层 —— 负责界面展示（XML + Fragment）
│   ├── home/
│   │   ├── HomeFragment.kt          # 首页 Fragment（含 TabLayout 切换笔记/待办）
│   │   └── HomeViewModel.kt         # 首页 ViewModel
│   ├── note/
│   │   ├── NoteListFragment.kt      # 笔记列表 Fragment
│   │   ├── NoteEditFragment.kt      # 笔记编辑/新建 Fragment
│   │   ├── NoteViewModel.kt         # 笔记 ViewModel
│   │   └── NoteListAdapter.kt       # 笔记列表 RecyclerView Adapter
│   ├── todo/
│   │   ├── TodoListFragment.kt      # 待办列表 Fragment
│   │   ├── TodoEditFragment.kt      # 待办编辑/新建 Fragment
│   │   ├── TodoViewModel.kt         # 待办 ViewModel
│   │   └── TodoListAdapter.kt       # 待办列表 RecyclerView Adapter
│   └── adapter/                     # 通用 Adapter 工具
│       └── AttachmentAdapter.kt     # 附件列表 Adapter
│
└── di/                              # 依赖注入（简单手动注入即可）
    └── AppContainer.kt              # 提供 Database、Repository 等单例

app/src/main/res/
├── layout/                          # XML 布局文件
│   ├── activity_main.xml            # MainActivity 布局（NavHostFragment）
│   ├── fragment_home.xml            # 首页布局（TabLayout + ViewPager2）
│   ├── fragment_note_list.xml       # 笔记列表布局（RecyclerView + FAB）
│   ├── fragment_note_edit.xml       # 笔记编辑布局（EditText + 附件区）
│   ├── fragment_todo_list.xml       # 待办列表布局（RecyclerView + FAB）
│   ├── fragment_todo_edit.xml       # 待办编辑布局
│   ├── item_note.xml                # 笔记列表单项布局
│   ├── item_todo.xml                # 待办列表单项布局
│   └── item_attachment.xml          # 附件单项布局
├── navigation/
│   └── nav_graph.xml                # Navigation Component 导航图
├── drawable/                        # (已有) 图标资源
├── values/                          # (已有) 颜色、字符串、主题
└── menu/
    └── home_menu.xml                # 首页选项菜单（如有需要）
```

## 二、需要添加的依赖

在 `gradle/libs.versions.toml` 中添加：

```toml
[versions]
room = "2.7.1"
navigationFragment = "2.9.0"
navigationUi = "2.9.0"
viewpager2 = "1.1.0"
ksp = "2.2.10-2.0.2"     # KSP 版本需匹配 Kotlin 版本

[libraries]
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-navigation-fragment-ktx = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigationFragment" }
androidx-navigation-ui-ktx = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigationUi" }
androidx-viewpager2 = { group = "androidx.viewpager2", name = "viewpager2", version.ref = "viewpager2" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
safeargs = { id = "androidx.navigation.safeargs.kotlin", version.ref = "navigationFragment" }
```

在 `app/build.gradle.kts` 中：
- 添加 `ksp` 和 `safeargs` 插件
- 添加 Room、Navigation、ViewPager2 依赖
- 启用 ViewBinding：`buildFeatures { viewBinding = true }`
- 移除 Compose 相关依赖（compose.bom, compose.ui, material3 等）

## 三、架构说明（MVVM 分层）

```
用户操作 --> Fragment(XML布局) --> ViewModel --> Repository --> Room DAO --> SQLite
                ^                      |
                |______________________|
                  LiveData/StateFlow 驱动 UI 更新
```

- **Entity（实体）**: 定义数据库表结构，使用 `@Entity` 注解
- **DAO（数据访问对象）**: 定义 SQL 增删改查，使用 `@Dao` 接口
- **Repository（仓库）**: 封装 DAO，给 ViewModel 提供干净的 API
- **ViewModel**: 持有 UI 状态（LiveData），调用 Repository，不直接操作数据库
- **Fragment（界面）**: 通过 ViewBinding 操作 XML 布局，观察 ViewModel 的 LiveData 来更新 UI
- **Adapter**: RecyclerView 的适配器，负责将数据列表渲染到 XML 布局中

## 四、核心业务流程

### 流程 1：启动应用
```
MainActivity.onCreate()
  -> setContentView(R.layout.activity_main)
    -> activity_main.xml 包含 NavHostFragment
      -> nav_graph.xml 定义起始目的地为 HomeFragment
        -> HomeFragment 显示 TabLayout："随小记" | "待办"
          -> ViewPager2 切换 NoteListFragment / TodoListFragment
```

### 流程 2：新建/编辑笔记
```
NoteListFragment 点击 FAB（悬浮"+"按钮）
  -> Navigation navigate() 跳转到 NoteEditFragment（args: noteId = 0）
    -> NoteViewModel.loadNote(0)  // id=0 表示新建
    -> 用户在 EditText 输入标题、内容，添加附件
    -> 点击保存按钮 -> NoteViewModel.save()
      -> NoteRepository.insert(note)
        -> Room DAO 写入数据库
      -> navigateUp() 返回 NoteListFragment
        -> LiveData 自动刷新列表
```

### 流程 3：管理待办
```
HomeFragment 切换到"待办"Tab
  -> TodoListFragment 显示待办列表（RecyclerView，按日期排序）
    -> 点击 CheckBox -> TodoViewModel.toggleDone(todo)
      -> TodoRepository.update(todo.copy(isDone = !isDone))
    -> 长按弹出菜单 -> 删除待办
```

### 流程 4：添加附件
```
NoteEditFragment 点击附件按钮
  -> 调用系统文件选择器 (ActivityResultContracts.GetContent)
    -> 获取文件 URI -> 复制到应用内部存储 (context.filesDir)
    -> 创建 Attachment 实体关联到当前 Note
    -> AttachmentAdapter 刷新显示附件预览列表
```

## 五、导航结构

```
nav_graph.xml:
  homeFragment        -> HomeFragment（主页，含 Tab 切换）
  noteListFragment    -> NoteListFragment（笔记列表）
  noteEditFragment    -> NoteEditFragment（参数: noteId: Long，0=新建）
  todoEditFragment    -> TodoEditFragment（参数: todoId: Long，0=新建）
```

导航使用 Navigation Component + SafeArgs 插件，在 nav_graph.xml 中可视化定义页面跳转和参数传递。

## 六、关于现有的 home_screen.xml

当前 `res/layout/home_screen.xml` 可以直接作为 HomeFragment 的布局基础，在此基础上改造为 `fragment_home.xml`，添加 TabLayout + ViewPager2 来承载笔记和待办两个子页面。

## 七、实施顺序

1. **Task 1**: 重构 build.gradle.kts —— 移除 Compose，添加 Room、Navigation、ViewBinding、KSP 等依赖
2. **Task 2**: 创建 data 层（Entity、DAO、Database、Repository）
3. **Task 3**: 创建 di/AppContainer 手动依赖注入
4. **Task 4**: 创建 XML 布局文件和 nav_graph.xml 导航图
5. **Task 5**: 创建 MainActivity + activity_main.xml（NavHostFragment）
6. **Task 6**: 创建 HomeFragment（TabLayout + ViewPager2 切换）
7. **Task 7**: 创建 NoteListFragment + NoteEditFragment + NoteViewModel + Adapter
8. **Task 8**: 创建 TodoListFragment + TodoEditFragment + TodoViewModel + Adapter
9. **Task 9**: 实现附件功能（文件选择、存储、预览）
