package com.example.simplemvvm.views

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import com.example.foundation.views.BaseFragment
import com.example.foundation.model.Result
import com.example.simplemvvm.R
import com.example.simplemvvm.databinding.PartResultBinding


fun <T> BaseFragment.renderSimpleResult(
    root: ViewGroup,
    result: Result<T>,
    onSuccess: (T) -> Unit
) {
    val binding = PartResultBinding.bind(root)
    renderResult(
        root = root,
        result = result,
        onError = {
            binding.errorContainer.visibility = View.VISIBLE
        },
        onPending = {
            binding.progressBar.visibility = View.VISIBLE
        },
        {successData ->
            root.children
                .filter { it.id != R.id.errorContainer && it.id != R.id.progressBar }
                .forEach { it.visibility = View.VISIBLE }
            onSuccess(successData)
        }
    )
}

fun BaseFragment.onTryAgain(root: View, onTryAgainPressed: () -> Unit) {
    root.findViewById<Button>(R.id.tryAgainButton).setOnClickListener { onTryAgainPressed() }
}


