package com.ryanharter.daggertest

import com.ryanharter.daggertest.feature.main.MainModule
import com.ryanharter.daggertest.service.name.NameService
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        MainModule::class,
        AppModule::class
    ]
)
interface AppComponent {
    fun inject(app: DaggerTestApp)
}

@Module
object AppModule {
    @Provides
    @JvmStatic
    @Singleton
    fun provideNameService(): NameService = object : NameService {
        override val name: String get() = "Dagger Test!"
    }
}