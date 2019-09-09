package com.ryanharter.daggertest.feature.main

import com.ryanharter.daggertest.injection.PerActivity
import com.ryanharter.daggertest.service.name.NameService
import javax.inject.Inject

@PerActivity
internal class StringFormatter @Inject constructor(
    private val nameService: NameService,
    private val count: Int
) {
    val formattedName: String get() = "$count ${nameService}s"
}