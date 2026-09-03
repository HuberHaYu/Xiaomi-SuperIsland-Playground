# Island

[中文](#中文) · [English](#english)

## 中文

Island 是一个用于创建和测试小米 HyperOS「超级岛」通知的 Android 项目，同时提供可以被其他应用直接集成的 SDK。

### 环境要求

- Android Studio（建议使用最新版稳定版）
- JDK 11 或更高版本
- Android API 31 或更高版本
- 小米 HyperOS 设备可使用完整的 Super Island 能力；其他 Android 设备会自动回退为普通通知，或 Live Update 通知

### 构建示例应用

在 Android Studio 中打开项目根目录，运行 `app` 模块。也可以在项目根目录执行：

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
```

请务必配合 HiddenApiBypass 依赖，并在构建时加入 keep 规则（具体 keep 可见开源包），避免混淆导致 Super Island 失效。

### 集成 SDK

SDK 的公共入口是 `IslandClient`，同时兼容 Java、Kotlin 和 Jetpack Compose；库的 manifest 已声明网络权限、通知权限并注册媒体控制接收器。使用时务必申请 `POST_NOTIFICATIONS` 权限。

#### 通过 JitPack

各位开发者可直接参考并使用如下的配置文件模板进行依赖安装：

```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.HuberHaYu:Xiaomi-SuperIsland-Playground:v1")
}
```

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

### 许可证

本项目采用 Apache License 2.0，详见 [LICENSE](LICENSE)。

## English

Island is an Android project for creating and testing Xiaomi HyperOS Super Island notifications. It also includes a reusable SDK that can be integrated into other Android applications.

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

Consumers do not need to generate or search for a dependency coordinate; they can use this fixed configuration directly:

```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.HuberHaYu:Xiaomi-SuperIsland-Playground:v1")
}
```

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

### License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE).
