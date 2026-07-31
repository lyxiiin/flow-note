// 插件声明：指定本模块需要使用的 Gradle 插件
plugins {
    alias(libs.plugins.android.application)       // Android 应用插件
    alias(libs.plugins.kotlin.compose)            // Kotlin Compose 插件（已内含 Kotlin 支持）
    alias(libs.plugins.ksp)                       // KSP 注解处理插件（Room 编译期代码生成需要）
}

// Android 构建配置
android {
    namespace = "com.lyxiiin.flownote"            // 包命名空间（用于生成 R 类）
    compileSdk = 37                               // 编译使用的 SDK 版本

    defaultConfig {
        applicationId = "com.lyxiiin.flownote"    // 应用唯一标识
        minSdk = 36                               // 最低支持的 Android 版本
        targetSdk = 36                            // 目标 Android 版本
        versionCode = 1                           // 内部版本号（每次发布递增）
        versionName = "1.0"                       // 用户可见的版本名称

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 构建类型配置
    buildTypes {
        release {
            isMinifyEnabled = false               // 是否启用代码混淆（暂关闭）
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    // Java 源码兼容性
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // 构建特性开关
    buildFeatures {
        compose = true                            // 启用 Jetpack Compose
        viewBinding = true                        // 启用 ViewBinding（XML 布局绑定）
    }
}

// 依赖声明：本模块使用的所有外部库
dependencies {
    // --- AndroidX 基础库 ---
    implementation(libs.androidx.core.ktx)        // AndroidX Core Kotlin 扩展
    implementation(libs.androidx.appcompat)       // AppCompat 兼容库（提供 AppCompatActivity 等）
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle 协程扩展（如 viewModelScope 等）

    // --- Jetpack Compose UI ---
    implementation(libs.androidx.activity.compose)      // Activity 与 Compose 集成
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM：统一所有 Compose 组件版本
    implementation(libs.androidx.compose.ui)            // Compose UI 基础
    implementation(libs.androidx.compose.ui.graphics)   // Compose 图形相关
    implementation(libs.androidx.compose.ui.tooling.preview) // Compose 预览支持
    implementation(libs.androidx.compose.material3)     // Material3 组件库

    // --- XML 布局相关库 ---
    implementation("com.google.android.material:material:1.12.0")   // Material Design 组件（TabLayout 等）
    implementation("androidx.viewpager2:viewpager2:1.1.0")          // ViewPager2 滑动页面
    implementation("androidx.fragment:fragment-ktx:1.8.5")          // Fragment Kotlin 扩展

    // --- Room 数据库 ---
    implementation(libs.room.runtime)  // Room 核心运行时（必须）
    ksp(libs.room.compiler)           // Room 注解处理器：KSP 在编译时生成数据库访问代码（必须）
    implementation(libs.room.ktx)     // Room Kotlin 协程扩展：支持 suspend DAO 方法和 Flow 查询（建议）

    // --- Navigation ---
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // --- 单元测试 ---
    testImplementation(libs.junit)    // JUnit4 单元测试框架

    // --- Android 测试（Instrumentation Test） ---
    androidTestImplementation(libs.androidx.junit)               // AndroidX JUnit 扩展
    androidTestImplementation(libs.androidx.espresso.core)       // Espresso UI 测试
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM（测试环境）
    androidTestImplementation(libs.androidx.compose.ui.test.junit4) // Compose UI 测试

    // --- Debug 专用（仅调试构建可用，不打进发布包） ---
    debugImplementation(libs.androidx.compose.ui.tooling)        // Compose 实时预览工具
    debugImplementation(libs.androidx.compose.ui.test.manifest)  // Compose 测试 Manifest
}
