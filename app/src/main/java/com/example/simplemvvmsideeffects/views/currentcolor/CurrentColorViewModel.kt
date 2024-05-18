package com.example.simplemvvmsideeffects.views.currentcolor

import com.example.foundation.model.SuccessResult
import com.example.foundation.model.takeSuccess
import com.example.foundation.model.tasks.dispatchers.Dispatcher
import com.example.foundation.navigator.Navigator
import com.example.foundation.uiactions.UiActions
import com.example.foundation.views.BaseViewModel
import com.example.foundation.views.LiveResult
import com.example.foundation.views.MutableLiveResult
import com.example.simplemvvmsideeffects.R
import com.example.simplemvvmsideeffects.model.colors.ColorListener
import com.example.simplemvvmsideeffects.model.colors.ColorsRepository
import com.example.simplemvvmsideeffects.model.colors.NamedColor
import com.example.simplemvvmsideeffects.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository,
    dispatcher: Dispatcher
) : BaseViewModel(dispatcher) {


    private val _currentColor = MutableLiveResult<NamedColor>()
    val currentColor: LiveResult<NamedColor> = _currentColor


    private val colorListener: ColorListener = {
        _currentColor.postValue(SuccessResult(it))
    }


    // example of listening results view model layer

    init {
        colorsRepository.addListener(colorListener)
        load()
    }

    override fun onCleared() {
        super.onCleared()
        colorsRepository.removeListener(colorListener)
    }

    // --- example of listening results directly from the screen

    override fun onResult(result: Any) {
        super.onResult(result)
        if (result is NamedColor) {
            val message = uiActions.getString(R.string.change_color_screen_title, result.name)
            uiActions.toast(message)
        }
    }

    // ---

    fun changeColor() {
        val currentColor = _currentColor.value.takeSuccess() ?: return
        val screen = ChangeColorFragment.Screen(currentColorId = currentColor.id)
        navigator.launch(screen)
    }

    fun onTryAgain() {
        load()

    }

    private fun load() {
        colorsRepository.getCurrentColor().into(_currentColor)
    }

}