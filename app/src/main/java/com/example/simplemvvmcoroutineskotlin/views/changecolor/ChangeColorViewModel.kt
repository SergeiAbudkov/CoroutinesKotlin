package com.example.simplemvvmsideeffects.views.changecolor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.foundation.model.ErrorResult
import com.example.foundation.model.FinalResult
import com.example.foundation.model.PendingResult
import com.example.foundation.model.SuccessResult
import com.example.foundation.model.takeSuccess
import com.example.foundation.model.tasks.factories.TasksFactory
import com.example.foundation.model.tasks.dispatchers.Dispatcher
import com.example.simplemvvmsideeffects.model.colors.ColorsRepository
import com.example.simplemvvmsideeffects.model.colors.NamedColor
import com.example.foundation.sideeffects.navigator.Navigator
import com.example.foundation.sideeffects.resources.Resources
import com.example.foundation.sideeffects.toasts.Toasts
import com.example.foundation.views.BaseViewModel
import com.example.foundation.views.LiveResult
import com.example.foundation.views.MediatorLiveResult
import com.example.foundation.views.MutableLiveResult
import com.example.simplemvvmsideeffects.R
import com.example.simplemvvmsideeffects.views.changecolor.ChangeColorFragment.Screen
import java.lang.IllegalStateException

class ChangeColorViewModel(
    screen: Screen,
    private val navigator: Navigator,
    private val toasts: Toasts,
    private val resources: Resources,
    private val colorsRepository: ColorsRepository,
    private val tasksFactory: TasksFactory,
    savedStateHandle: SavedStateHandle,
    dispatcher: Dispatcher
) : BaseViewModel(dispatcher), ColorsAdapter.Listener {


    //input sources
    private val _availableColors = MutableLiveResult<List<NamedColor>>(PendingResult())
    private val _currentColorId =
        savedStateHandle.getLiveData("currentColorId", screen.currentColorId)
    private var _saveInProgress = MutableLiveData(false)

    //main destination (contains merged values from _availableColors & _currentColorId)
    private val _viewState = MediatorLiveResult<ViewState>()
    val viewState: LiveResult<ViewState> = _viewState


    //side destination, also the same result can be achieved by using Transformations.map() function.
    private val _screenTitle = MutableLiveData<String>()
    val screenTitle: LiveData<String> = _screenTitle

    init {
        load()
        //initializing MediatorLiveData
        _viewState.addSource(_availableColors) { mergeSources() }
        _viewState.addSource(_currentColorId) { mergeSources() }
        _viewState.addSource(_saveInProgress) { mergeSources() }
    }

    override fun onColorChosen(namedColor: NamedColor) {
        if (_saveInProgress.value == true) return
        _currentColorId.value = namedColor.id
    }

    private fun onSaved(result: FinalResult<NamedColor>) {
        _saveInProgress.value = false
        when (result) {
            is ErrorResult -> toasts.toast(resources.getString(R.string.error_happened))
            is SuccessResult -> navigator.goBack(result = result.data)
        }
    }

    fun onSavePressed() {
        _saveInProgress.postValue(true)

        tasksFactory.async {
            val currentColorId =
                _currentColorId.value ?: throw IllegalStateException("Color ID should not be NULL")
            val currentColor = colorsRepository.getById(currentColorId).await()
            colorsRepository.setCurrentColor(currentColor).await()
            return@async currentColor
        }.safeEnqueue { onSaved(it) }
    }

    fun onCancelPressed() {
        navigator.goBack()
    }

    /**
     * [MediatorLiveData] can listen other LiveData instances (even more than 1)
     * and combine their values.
     * Here we listen the list of available colors ([_availableColors] live-data) + current color id
     * ([_currentColorId] live-data), then we use both of these values in order to create a list of
     * [NamedColorListItem], it is a list to be displayed in RecyclerView.
     */

    private fun setScreenTitle() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val currentColor = colors.map {
            it.first { it.id == currentColorId }
        }
        val nameCurrentColor: String? = currentColor.takeSuccess()?.name
        _screenTitle.value =
            if (nameCurrentColor != null) {
                resources.getString(
                    R.string.change_color_screen_title,
                    nameCurrentColor
                )
            } else {
                resources.getString(R.string.change_color_screen_title_simple)
            }
    }

    private fun mergeSources() {
        val colors = _availableColors.value ?: return
        val currentColorId = _currentColorId.value ?: return
        val saveInProgress = _saveInProgress.value ?: return

        _viewState.value = colors.map { colorsList ->
            ViewState(
                colorsList.map { NamedColorListItem(it, currentColorId == it.id) },
                showSaveButton = !saveInProgress,
                showCancelButton = !saveInProgress,
                showSaveProgressBar = saveInProgress
            )
        }
        setScreenTitle()
    }

    data class ViewState(
        val colorList: List<NamedColorListItem>,
        val showSaveButton: Boolean,
        val showCancelButton: Boolean,
        val showSaveProgressBar: Boolean,
    )

    fun tryAgain() {
        load()
    }

    private fun load() {
        colorsRepository.getAvailableColors().into(_availableColors)
    }
}