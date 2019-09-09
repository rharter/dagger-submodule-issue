package com.ryanharter.daggertest.feature.main

import androidx.lifecycle.ViewModel
import com.ryanharter.daggertest.injection.PerActivity
import com.ryanharter.daggertest.service.name.NameService
import javax.inject.Inject

@PerActivity
class MainViewModel @Inject constructor(
    private val nameService: NameService
) : ViewModel() {

    val name: String get() = nameService.name

}