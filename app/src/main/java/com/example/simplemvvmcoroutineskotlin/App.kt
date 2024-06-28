package com.example.simplemvvmcoroutineskotlin

import android.app.Application
import com.example.foundation.BaseApplication
import com.example.foundation.model.coroutines.IoDispatcher
import com.example.foundation.model.coroutines.WorkerDispatcher
import com.example.simplemvvmcoroutineskotlin.model.colors.InMemoryColorsRepository
import kotlinx.coroutines.Dispatchers

/**
 * Here we store instances of model layer classes.
 */
class App : Application(), BaseApplication {

    private var ioDispatcher = IoDispatcher(Dispatchers.IO)
    private var workerDispatcher = WorkerDispatcher(Dispatchers.Default)

    /**
     * Place your singleton scope dependencies here
     */
    override val singletonScopeDependencies: List<Any> = listOf(
        InMemoryColorsRepository(ioDispatcher)
    )

}