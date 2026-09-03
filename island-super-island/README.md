# Island Super Island SDK

Reusable Android library for publishing Xiaomi HyperOS Super Island notifications.

## Gradle

Add the JitPack repository and fixed module dependency directly:

```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.HuberHaYu.Xiaomi-SuperIsland-Playground:island-super-island:v1.0.0")
}
```

For a later release, replace `v1.0.0` with the corresponding Git tag. To test locally, publish the module with:

```bash
./gradlew :island-super-island:publishReleasePublicationToLocalBuildRepository
```

The AAR is produced at `build/outputs/aar/island-super-island-release.aar`.

## Kotlin, Java, and Compose

```kotlin
val island = IslandClient.get(context)
val draft = IslandDraftBuilder()
    .title("Syncing")
    .subtitle("65%")
    .build()
island.publish(draft) { outcome -> println(outcome.message) }
```

```java
IslandClient island = IslandClient.get(this);
IslandDraft draft = new IslandDraftBuilder()
        .title("Syncing")
        .subtitle("65%")
        .build();
island.publish(draft, outcome -> Log.d("Island", outcome.getMessage()));
```

In Compose, obtain the client with `remember { IslandClient.get(LocalContext.current) }` and collect `activeIslands` with `collectAsState()`. `IslandClient` performs XMSF capability detection and the Xiaomi fallback automatically; callers only provide an `IslandDraft`.
