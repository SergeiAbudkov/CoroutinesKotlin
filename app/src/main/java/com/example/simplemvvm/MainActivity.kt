package com.example.simplemvvm

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.foundation.ActivityScopeViewModel
import com.example.foundation.navigator.IntermediateNavigator
import com.example.foundation.navigator.StackFragmentNavigator
import com.example.foundation.uiactions.AndroidUiActions
import com.example.foundation.utils.viewModelCreator
import com.example.foundation.views.FragmentsHolder
import com.example.simplemvvm.databinding.ActivityMainBinding
import com.example.simplemvvm.views.currentcolor.CurrentColorFragment

/**
This application is a single-activity app. MainActivity is a container
 */
class MainActivity : AppCompatActivity(), FragmentsHolder {

    private lateinit var navigator: StackFragmentNavigator
    private val activityViewModel by viewModelCreator<ActivityScopeViewModel> {
        ActivityScopeViewModel(
            uiActions = AndroidUiActions(application),
            IntermediateNavigator()
        )
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        navigator = StackFragmentNavigator(
            this,
            R.id.fragmentContainer,
            defaultTitle = getString(R.string.app_name),
            animation = StackFragmentNavigator.Animations(
                R.anim.enter,
                R.anim.exit,
                R.anim.pop_enter,
                R.anim.pop_exit,
            ),
            initialScreenCreator = { CurrentColorFragment.Screen() }
        )

        navigator.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        navigator.onDestroy()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        //execute navigation actions only when activity is active
        activityViewModel.navigator.setTarget(navigator)
    }

    override fun onPause() {
        super.onPause()
        // postpone navigation actions if activity is not active
        activityViewModel.navigator.setTarget(null)
    }

    override fun notifyScreenUpdates() {
        navigator.notifyScreenUpdates()
    }

    override fun getActivityScopeViewModel(): ActivityScopeViewModel {
        return activityViewModel
    }

    override fun onBackPressed() {
        navigator.onBackPressed()
        super.onBackPressed()
    }


}