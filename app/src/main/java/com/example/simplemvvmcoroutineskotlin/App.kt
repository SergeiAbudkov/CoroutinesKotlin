package com.example.simplemvvmcoroutineskotlin

import android.app.Application
import com.example.foundation.BaseApplication
import com.example.simplemvvmcoroutineskotlin.model.colors.InMemoryColorsRepository

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    /**
     * Place your singleton scope dependencies here
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        InMemoryColorsRepository()
    )

}