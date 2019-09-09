The Goal: Allow use of Dagger-android AndroidInjectors without needing to make all dependencies of the target public.

The Scenario: I have a modular app where each feature is a separate module (among others).  I have always hated making the ViewModels/ViewStates/ViewActions and other internal parts of the feature public, but that’s required with the current Dagger-android subcomponent approach.

Here’s my module setup:

[AppComponent.kt](app/src/main/java/com/ryanharter/daggertest/AppComponent.kt) lives in the `app` module, and depends on

```kotlin
// app/src/main/java/.../AppComponent.kt
@Component(modules = [MainModule::class, ...])
interface AppComponent {
 fun inject(app: DaggerTestApp)
}
```

[MainModule.kt](feature-main/android/src/main/java/com/ryanharter/daggertest/feature/main/MainModule.kt) lives in the `feature-main:android` module, and has an `implementaiton` dependency on `feature-main:core`, which is where the `MainViewModel` lives.

```kotlin
// feature-main/android/src/main/java/.../MainModule.kt
@Module
abstract class MainModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [Providers::class])
    abstract fun contributeMainActivityInjector(): MainActivity

    @Module
    internal object Providers {

        @Provides
        @JvmStatic
        @PerActivity
        fun provideStringFormatter(nameService: NameService): StringFormatter = StringFormatter(nameService, 4)

        @Provides
        @JvmStatic
        @PerActivity
        fun provideViewModelFactory(viewModel: MainViewModel): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    require(modelClass == MainViewModel::class.java) {
                        "Unknown ViewModel class: ${modelClass.canonicalName}"
                    }
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                }
            }
        }
    }
}
```

As long as the `feature-main:core` dependency is `implementation`, the `MainViewModel` is not accessible to downstream consumers, which is what I want, but that breaks Dagger.

The reason that dependencies of the generated subcomponent have to be public is that the subcomponent implementation uses the parent component directly to access the providers, meaning that the dependencies need to be provided by the parent component (which lives in the app module, in my case).

To solve this, the feature module can be updated to create a Component instead of a Subcomponent, providing the dependencies via the Component.Builder, and exposing only the items that should be made available publicly.

```kotlin
// Public dagger.Module that exposes public items that should be available
//  to other modules
@Module(includes = [MainModule.Providers::class])
abstract class MainModule {

 // Expose publicly available items
 @Binds abstract fun otherPublicItem(item: MyPublicItem): PublicItem

 @Module
 object Providers {

  // This is how we expose the automatically generated AndroidInjector
  //  for our Activities to the rest of the graph, so that the app
  //  level injector can take care of injection.
  @Provides
  @JvmStatic
  @IntoMap
  @ClassKey(MainActivity::class)
  fun bindAndroidInjectorFactory(component: MainComponent):
   AndroidInjector.Factory<*>
   	 = component.mainActivitySubcomponentFactory()

  // Using dagger to inject and construct the component's builder makes
  // this a lot easier.
  @Provides
  @JvmStatic
  fun provideMainComponent(nameService: NameService): MainComponent =
    MainComponent.builder()
      .nameService(nameService)
      .build()
 }

 @ActivityScope
 @Component(modules = [InternalMainModule::class])
 interface MainComponent {

  // The component has to explicitly make public whatever is internal to
  //  it's graph that should be available to the rest of the app (via the
  //  public module that contains this component). The key here is that,
  //  while the items exposed here need to be public, **their dependencies
  //  don't**, so the Activity that this subcomponent creates needs to be
  //  public (true for all activities), but it's ViewModel can be internal
  //  or otherwise hidden from other modules in the app.
  fun mainActivitySubcomponentFactory(): InternalMainModule_ContributesMainActivity.MainActivitySubcomponent.Factory

  // All dependencies used within the Component but provided elsewhere need
  // to be provided to the builder.
  @Component.Builder
  interface Builder {
   @BindsInstance fun nameService(nameService: NameService): Builder
   fun build(): MainComponent
  }

  companion object {
   fun builder(): Builder = DaggerMainModule_MainComponent.builder()
  }
 }
}

// Internal dagger.Module that's used to assemble a graph that relies on
//  components that aren't visible to the rest of the app (i.e. the
//  MainViewModel, in this case).  Dagger-android created Injectors
//  must be included here since their autogenerated Subcomponents are
//  created within the Component in which they're contained, meaning that
//  *all of their dependencies need to be visible to the containing Component*.
@Module
internal abstract class InternalMainModule {

  // The generated subcomponent only has access to the parent component,
  //  MainComponent in this case.
  @ContributesAndroidInjector(modules = [Internal::class])
  abstract fun contributesMainActivity(): MainActivity

  @Module
  internal abstract class Internal {
    // This is now actually internal
    @Binds abstract fun bindMainViewModel(impl: MainViewModel): ViewModel
  }
}
```
