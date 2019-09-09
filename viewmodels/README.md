# ViewModel: Kotlin

This is a simple Kotlin-only dependency that provides the `androidx.lifecycle.ViewModel` abstract class without any
Android dependencies, allowing you to create Kotlin-only implementations of ViewModels that will be compatible with
AndroidX's `ViewModelProviders` when included in an Android module.

## Usage

In your Kotlin-only module, depend on this module as a `compileOnly` dependency. Consumers of your Kotlin-only module
must provide an actual implementation via one of the `androidx.lifecycle` dependencies.

In your Kotlin-only module:

```kotlin
plugins {
  kotlin("jvm")
}

dependencies {
  implementation(Deps.kotlin.stdlib)
  compileOnly(project(":viewmodel-kotlin"))

  // Since the dependency is compile only, it needs to be explicitly provided in the test configuration.
  testImplementation(project(":viewmodel-kotlin"))
}
```

In your consuming module:

```kotlin
plugins {
  id("com.android.library")
  kotlin("android")
}

dependencies {
  implementation(Deps.kotlin.stdlib)
  implementation(project(":core"))
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.0.0")
}
```
