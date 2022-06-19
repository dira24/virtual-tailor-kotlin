package com.virtaapps.mobile.ui.main

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.virtaapps.mobile.R
import com.virtaapps.mobile.base.BaseActivity
import com.virtaapps.mobile.data.model.MeasurementHistory
import com.virtaapps.mobile.databinding.ActivityMainBinding
import com.virtaapps.mobile.ui.auth.LoginActivity
import com.virtaapps.mobile.ui.detect.DetectActivity
import com.virtaapps.mobile.ui.result.ResultActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding =
        ActivityMainBinding::inflate

    private lateinit var historyAdapter: HistoryAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun setup() {
        setupRecyclerView()
        setupListener()
        setupSwiper()
        showCustomViewDialog(BottomSheet(LayoutMode.WRAP_CONTENT))
        viewModel.getHistories().observe(this, setHistoryObserver())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                viewModel.logout().observe(this) {
                    LoginActivity.startActivity(this)
                }
                return true
            }
        }
        return false
    }

    private fun setHistoryObserver() = setObserver<List<MeasurementHistory>?>(
        onSuccess = {
            historyAdapter.differ.submitList(it.data)
            if (it.data?.isNotEmpty() == true) binding.tvNoData.gone()
        }
    )

    private fun setupSwiper() {
        binding.swiper.setOnRefreshListener {
            viewModel.getHistories().observe(this, setHistoryObserver())
        }
    }

    private fun setupListener() {
        binding.btnAdd.setOnClickListener {
            DetectActivity.startActivity(this)
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        with(binding.rvHistory) {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        historyAdapter.setOnItemClickListener {
            ResultActivity.startActivity(this, it)
        }
    }

    private fun showCustomViewDialog(dialogBehavior: DialogBehavior = ModalDialog) {
        MaterialDialog(this, dialogBehavior).show {
            title(R.string.welcome)
            customView(R.layout.dialog_todo, scrollable = true, horizontalPadding = true)
            positiveButton(R.string.ok) { dialog ->
                dialog.cancel()
            }
            lifecycleOwner(this@MainActivity)
        }
    }
}