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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.mapbox.dash.example.databinding.ActivityDrawerBinding
import com.mapbox.nav.gm.base.flow.observeWhenStarted
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class DrawerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawerBinding

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerBinding.inflate(layoutInflater)
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
        getValue: () -> Boolean,
        setValue: (v: Boolean) -> Unit,
    ) {
        switch.isChecked = getValue()
        switch.setOnCheckedChangeListener { _, isChecked -> setValue(isChecked) }
    }

    protected fun bindSwitch(
        switch: SwitchCompat,
        state: MutableStateFlow<Boolean>,
        onChange: (value: Boolean) -> Unit,
    ) {
        state.observeWhenStarted(this) {
            switch.isChecked = it
            onChange(it)
        }
        switch.setOnCheckedChangeListener { _, isChecked ->
            state.value = isChecked
        }
    }

    protected fun bindSwitch(
        switch: SwitchCompat,
        liveData: MutableLiveData<Boolean>,
        onChange: (value: Boolean) -> Unit,
    ) {
        liveData.observe(this) {
            switch.isChecked = it
            onChange(it)
        }
        switch.setOnCheckedChangeListener { _, isChecked ->
            liveData.value = isChecked
        }
    }

    protected fun bindButton(
        button: MaterialButton,
        onClick: suspend () -> Unit,
    ) {
        val spec = CircularProgressIndicatorSpec(this, null, 0, R.style.ProgressIndicator)
        val progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(this, spec)
        button.setOnClickListener {
            MainScope().launch {
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
        liveData: LiveData<String>,
        onSelected: (value: String?) -> Unit,
    ) {
        liveData.observe(this) { selection ->
            if (spinner.selectedItem != selection) {
                val position = spinner.adapter.findItemPosition(selection)
                position?.let { spinner.setSelection(it) }
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
                    val selection = parent.getItemAtPosition(position) as? String
                    selection?.let { onSelected(it) }
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
}