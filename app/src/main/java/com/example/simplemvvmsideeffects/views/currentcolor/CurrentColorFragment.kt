package com.example.simplemvvmsideeffects.views.currentcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foundation.views.BaseFragment
import com.example.foundation.views.BaseScreen
import com.example.foundation.views.screenViewModel
import com.example.simplemvvmsideeffects.databinding.FragmentCurrentColorBinding
import com.example.simplemvvmsideeffects.views.onTryAgain
import com.example.simplemvvmsideeffects.views.renderSimpleResult

class CurrentColorFragment : BaseFragment() {
    private lateinit var binding: FragmentCurrentColorBinding

    // no arguments for this screen
    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentColorBinding.inflate(layoutInflater, container, false)
        viewModel.currentColor.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                binding.root,
                result = result,
                onSuccess = {
                    binding.colorView.setBackgroundColor(it.value)
                })

        }
        binding.changeColorButton.setOnClickListener {
            viewModel.changeColor()
        }

        onTryAgain(binding.root) {
            viewModel.onTryAgain()
        }

        return binding.root
    }


}
