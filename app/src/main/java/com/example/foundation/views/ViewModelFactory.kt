package com.example.foundation.views

import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.foundation.ARG_SCREEN
import com.example.foundation.BaseApplication
import java.lang.reflect.Constructor

inline fun <reified VM : ViewModel> BaseFragment.screenViewModel() = viewModels<VM> {
    val application = requireActivity().application as BaseApplication
    val screen = requireArguments().getSerializable(ARG_SCREEN) as BaseScreen

    // using Providers API directly for getting MainViewModel instance
    val activityScopeViewModel = (requireActivity() as FragmentsHolder).getActivityScopeViewModel()

    // forming the list of available dependencies:
    // - singleton scope dependencies (repositories) -> from App class
    // - activity VM scope dependencies -> from MainViewModel
    // - screen VM scope dependencies -> screen args
    val dependencies = listOf(screen, activityScopeViewModel) + application.repositories

    // creating factory
    ViewModelFactory(dependencies, this)
}


class ViewModelFactory(
    private val dependencies: List<Any>,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val constructors = modelClass.constructors
        val constructor = constructors.maxByOrNull { it.typeParameters.size }!!
        val dependenciesWithSavedState = dependencies + handle
        val arguments: List<Any> = findDependencies(constructor,dependenciesWithSavedState)
        return constructor.newInstance(*arguments.toTypedArray()) as T
    }

    private fun findDependencies(constructor: Constructor<*>, dependencies: List<Any>): List<Any> {
        val arg = mutableListOf<Any>()

        constructor.parameterTypes.forEach {typeParameters ->
            val dependency = dependencies.first {
                typeParameters.isAssignableFrom(it.javaClass)
            }
            arg.add(dependency)
        }
        return arg
    }
}

