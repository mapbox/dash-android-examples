package com.mapbox.dash.example

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SpinnerAdapter
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.mapbox.dash.example.databinding.ActivityDrawerBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class DrawerActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDrawerBinding.inflate(layoutInflater) }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.drawerContent.addView(
            onCreateContentView(),
            0,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ),
        )
        binding.drawerMenuContent.addView(onCreateMenuView())
        setContentView(binding.root)

        binding.menuButton.setOnClickListener { openDrawer() }
    }

    abstract fun onCreateContentView(): View

    abstract fun onCreateMenuView(): View

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawers() {
        binding.drawerLayout.closeDrawers()
    }

    protected fun bindSwitch(
        switch: SwitchCompat,
        state: MutableStateFlow<Boolean>,
        onChange: (value: Boolean) -> Unit,
    ) {
        state.observeWhenStarted(lifecycleOwner = this) { isChecked ->
            switch.isChecked = isChecked
        }
        switch.setOnCheckedChangeListener { _, isChecked ->
            state.value = isChecked
            onChange(isChecked)
        }
    }

    protected fun bindButton(
        button: MaterialButton,
        onClick: suspend () -> Unit,
    ) {
        val spec = CircularProgressIndicatorSpec(this, null, 0, R.style.ProgressIndicator)
        val progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(this, spec)
        button.setOnClickListener {
            lifecycleScope.launch {
                val originalIcon = button.icon
                val originalText = button.text
                button.icon = progressIndicatorDrawable
                button.isEnabled = false
                button.text = null
                onClick()
                button.icon = originalIcon
                button.isEnabled = true
                button.text = originalText
            }
        }
    }

    protected fun bindSpinner(
        spinner: AppCompatSpinner,
        state: MutableStateFlow<String>,
        onSelected: (value: String) -> Unit,
    ) {
        state.observeWhenStarted(lifecycleOwner = this) { selection ->
            if (spinner.selectedItem != selection) {
                spinner.adapter.findItemPosition(selection)?.let { spinner.setSelection(it) }
            }
        }
        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    (parent.getItemAtPosition(position) as? String)?.let { selection ->
                        state.value = selection
                        onSelected(selection)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
    }

    private fun SpinnerAdapter.findItemPosition(item: Any): Int? {
        for (pos in 0 until count) {
            if (item == getItem(pos)) return pos
        }
        return null
    }

    protected fun <T> Flow<T>.observeWhenStarted(lifecycleOwner: LifecycleOwner, action: FlowCollector<T>): Job {
        return lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                collect(action)
            }
        }
    }
}