package com.ryanharter.daggertest.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryanharter.daggertest.injection.PerActivity
import com.ryanharter.daggertest.service.name.NameService
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

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
