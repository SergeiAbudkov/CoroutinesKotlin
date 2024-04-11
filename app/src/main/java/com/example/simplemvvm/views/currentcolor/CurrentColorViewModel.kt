package com.example.simplemvvm.views.currentcolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.simplemvvm.R
import com.example.simplemvvm.model.colors.ColorListener
import com.example.simplemvvm.model.colors.ColorsRepository
import com.example.simplemvvm.model.colors.NamedColor
import com.example.foundation.navigator.Navigator
import com.example.foundation.uiactions.UiActions
import com.example.foundation.views.BaseViewModel
import com.example.simplemvvm.views.changecolor.ChangeColorFragment

class CurrentColorViewModel(
    private val navigator: Navigator,
    private val uiActions: UiActions,
    private val colorsRepository: ColorsRepository
) : BaseViewModel() {


    private val _currentColor = MutableLiveData<NamedColor>()
    val currentColor: LiveData<NamedColor> = _currentColor


    private val colorListener: ColorListener = {
        _currentColor.postValue(it)
    }

    // example of listening results view model layer

    init {
        colorsRepository.addListener(colorListener)
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
        val currentColor = _currentColor.value ?: return
        val screen = ChangeColorFragment.Screen(currentColorId = currentColor.id)
        navigator.launch(screen)
    }

}