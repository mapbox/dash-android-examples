package com.mapbox.dash.showcase.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.AdapterView
import android.widget.SpinnerAdapter
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.mapbox.dash.showcase.app.databinding.ActivityDrawerBinding
import kotlinx.coroutines.flow.MutableStateFlow

abstract class DrawerActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDrawerBinding.inflate(layoutInflater) }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.drawerContent.addView(
            onCreateContentView(),
            0,
            ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT),
        )
        binding.drawerMenuContent.addView(onCreateMenuView())
        setContentView(binding.root)

        binding.menuButton.setOnClickListener { openDrawer() }
    }

    abstract fun onCreateContentView(): View

    abstract fun onCreateMenuView(): View

    private fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawers() {
        binding.drawerLayout.closeDrawers()
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
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val value = parent.getItemAtPosition(position) as? String ?: return
                state.value = value
                onSelected(value)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun SpinnerAdapter.findItemPosition(item: Any): Int? {
        for (pos in 0..<count) {
            if (item == getItem(pos)) return pos
        }
        return null
    }
}
