package com.ryanharter.daggertest

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class DaggerTestApp : Application(), HasAndroidInjector {

    private lateinit var component: AppComponent

    @Inject lateinit var _injector: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> = _injector

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.create().inject(this)
    }
}