# Island

[中文](#中文) · [English](#english)

## 中文

Island 是一个用于创建和测试小米 HyperOS「超级岛」通知的 Android 项目，同时提供可以被其他应用直接集成的 SDK。

### 项目内容

```text
app/                  示例应用与可视化编辑界面
island-super-island/  可复用的 Super Island SDK
design/               App 图标设计源文件
```

示例应用包含分页式编辑界面、关于页面、平台入口、主题色配置以及 Super Island 发布与实时更新功能。App 图标继续使用 manifest 中原有的 `@mipmap/ic_launcher` 引用。

### 环境要求

- Android Studio（建议使用最新版稳定版）
- JDK 11 或更高版本
- Android API 31 或更高版本
- 小米 HyperOS 设备可使用完整的 Super Island 能力；其他 Android 设备会自动回退为普通通知

### 构建示例应用

在 Android Studio 中打开项目根目录，运行 `app` 模块。也可以在项目根目录执行：

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Release 构建已启用 R8 压缩、优化、资源压缩和混淆。XMSF 发布器、能力检测、媒体接收器以及 HiddenApiBypass 相关类已加入 keep 规则，避免混淆导致 Super Island 失效。

### 集成 SDK

SDK 的公共入口是 `IslandClient`，同时兼容 Java、Kotlin 和 Jetpack Compose。SDK 最低支持 Android API 31；库的 manifest 已声明网络权限、通知权限并注册媒体控制接收器。Android 13 及以上仍需由宿主应用在运行时申请 `POST_NOTIFICATIONS`。

#### 通过 JitPack

1. 将仓库公开到 GitHub。
2. 创建版本 Tag，例如 `v1.0.0`。
3. 打开 [JitPack](https://jitpack.io/)，输入 `Huber-HaYu/Island` 并构建该 Tag。
4. 复制 JitPack 为 `island-super-island` 模块生成的 Gradle 依赖坐标。多模块项目的坐标以 JitPack 页面自动生成的代码为准。

仓库配置通常如下：

```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

然后粘贴 JitPack 页面生成的 `implementation(...)` 依赖声明。

#### Kotlin

```kotlin
val island = IslandClient.get(this)

val draft = IslandDraftBuilder()
    .scene(IslandScene.GENERAL)
    .title("配送进度")
    .subtitle("即将送达")
    .source("Island")
    .progress(65)
    .build()

island.publish(draft) { outcome ->
    Log.d("Island", outcome.message)
}
```

#### Java

```java
IslandClient island = IslandClient.get(this);

IslandDraft draft = new IslandDraftBuilder()
        .title("配送进度")
        .subtitle("即将送达")
        .source("Island")
        .progress(65)
        .build();

island.publish(draft, outcome ->
        Log.d("Island", outcome.getMessage()));
```

#### Jetpack Compose

```kotlin
@Composable
fun IslandStatus() {
    val context = LocalContext.current
    val island = remember(context) { IslandClient.get(context) }
    val active by island.activeIslands.collectAsState()

    Button(onClick = {
        island.publish(
            IslandDraftBuilder()
                .title("同步中")
                .subtitle("65%")
                .progress(65)
                .build()
        ) { }
    }) {
        Text(if (active.isEmpty()) "发布" else "更新")
    }
}
```

使用 `updateActive(...)` 进行实时更新，使用 `cancel(notificationId)` 取消当前通知，使用 `activeIslands` 观察当前状态。SDK 会自动检测 XMSF 是否可用，并在每次验证结束后恢复 XMSF 状态。使用者不需要编写或维护 XMSF 内部逻辑。

#### 本地 Maven 测试

```bash
./gradlew :island-super-island:publishReleasePublicationToLocalBuildRepository
```

生成的本地仓库位于 `island-super-island/build/repo`，AAR 位于 `island-super-island/build/outputs/aar/island-super-island-release.aar`。

### GitHub 发布建议

代码应放在 GitHub Repository 中；GitHub Project 只用于管理 Issue、任务和开发进度，不能替代代码仓库。建议创建 `v1.0.0` Tag，并在 GitHub Release 中附上构建好的 AAR 文件。后续升级时使用新的 Tag，例如 `v1.1.0`。

建议的仓库结构：

```text
Island/
├── app/
├── island-super-island/
├── design/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── README.md
└── LICENSE
```

### 许可证

本项目采用 Apache License 2.0，详见 [LICENSE](LICENSE)。

## English

Island is an Android project for creating and testing Xiaomi HyperOS Super Island notifications. It also includes a reusable SDK that can be integrated into other Android applications.

### Project layout

```text
app/                  Sample application and visual editor
island-super-island/  Reusable Super Island SDK
design/               App icon design sources
```

The sample app includes a paged editor, an About page, platform links, theme configuration, and Super Island publishing and live-update features. The app icon continues to use the existing manifest reference, `@mipmap/ic_launcher`.

### Requirements

- Android Studio (latest stable version recommended)
- JDK 11 or newer
- Android API 31 or newer
- Xiaomi HyperOS devices can use the full Super Island path; other Android devices automatically fall back to a regular notification

### Build the sample app

Open the project root in Android Studio and run the `app` module, or execute:

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

Release builds enable R8 shrinking, optimization, resource shrinking, and obfuscation. The XMSF publisher, capability gate, media receiver, and HiddenApiBypass classes are kept so obfuscation does not break the Super Island path.

### Integrate the SDK

The public entry point is `IslandClient`. The SDK is usable from Java, Kotlin, and Jetpack Compose. It targets Android API 31+. Its manifest declares the required network and notification permissions and registers the media-control receiver. Host applications must still request `POST_NOTIFICATIONS` at runtime on Android 13 and newer.

#### JitPack

1. Make the repository public on GitHub.
2. Create a version tag such as `v1.0.0`.
3. Open [JitPack](https://jitpack.io/), enter `Huber-HaYu/Island`, and build that tag.
4. Copy the Gradle coordinate generated by JitPack for the `island-super-island` module. For a multi-module repository, always use the coordinate shown by JitPack.

The repository configuration normally looks like this:

```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

Then paste the `implementation(...)` declaration generated on the JitPack page.

#### Kotlin

```kotlin
val island = IslandClient.get(this)
val draft = IslandDraftBuilder()
    .scene(IslandScene.GENERAL)
    .title("Delivery")
    .subtitle("Arriving soon")
    .source("Island")
    .progress(65)
    .build()

island.publish(draft) { outcome ->
    Log.d("Island", outcome.message)
}
```

#### Java

```java
IslandClient island = IslandClient.get(this);
IslandDraft draft = new IslandDraftBuilder()
        .title("Delivery")
        .subtitle("Arriving soon")
        .source("Island")
        .progress(65)
        .build();

island.publish(draft, outcome ->
        Log.d("Island", outcome.getMessage()));
```

#### Jetpack Compose

```kotlin
@Composable
fun IslandStatus() {
    val context = LocalContext.current
    val island = remember(context) { IslandClient.get(context) }
    val active by island.activeIslands.collectAsState()

    Button(onClick = {
        island.publish(
            IslandDraftBuilder().title("Syncing").subtitle("65%").build()
        ) { }
    }) {
        Text(if (active.isEmpty()) "Publish" else "Update")
    }
}
```

Use `updateActive(...)` for live changes, `cancel(notificationId)` to remove the current notification, and `activeIslands` to observe the current state. The SDK detects XMSF availability automatically, restores the XMSF state after each validation transaction, and selects the regular-notification fallback when necessary. Consumers do not need to implement XMSF internals.

#### Local Maven testing

```bash
./gradlew :island-super-island:publishReleasePublicationToLocalBuildRepository
```

The local repository is generated at `island-super-island/build/repo`; the AAR is generated at `island-super-island/build/outputs/aar/island-super-island-release.aar`.

### GitHub release workflow

The source code must live in a GitHub Repository. A GitHub Project is only for organizing issues, tasks, and development progress; it cannot replace the repository. Create a `v1.0.0` tag and attach the built AAR to the GitHub Release. Use a new tag such as `v1.1.0` for later SDK releases.

### License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE).
