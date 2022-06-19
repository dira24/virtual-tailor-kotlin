package com.virtaapps.mobile.ui.result

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import com.virtaapps.mobile.base.BaseActivity
import com.virtaapps.mobile.data.model.MeasurementHistory
import com.virtaapps.mobile.databinding.ActivityResultBinding
import com.virtaapps.mobile.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ResultActivity : BaseActivity<ActivityResultBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityResultBinding =
        ActivityResultBinding::inflate

    private var id = ""

    override fun setup() {
        setupBackButton()
        setTitle("Hasil Pengukuran")
        val data = intent.getParcelableExtra<MeasurementHistory>(EXTRA_DATA)
        populateView(data)
        setupListener()
    }

    private fun setupListener() {
        binding.btnCopy.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("id_virta", id)
            clipboardManager.setPrimaryClip(clipData)
            showToast("ID berhasil disalin")
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun populateView(data: MeasurementHistory?) {
        with(binding) {
            id = data?.id.orEmpty()
            tvId.text = data?.id
            tvName.text = data?.name
            tvAddress.text = data?.address
            tvType.text = data?.type
            tvBahu.text = data?.bahu
            tvTangan.text = data?.tangan
            tvBadan.text = data?.badan
            tvUkuran.text = data?.ukuran
            Picasso.get().load(data?.gambar).into(imgResult)
        }
    }

    companion object {
        const val EXTRA_DATA = "data"
        fun startActivity(ctx: Context, data: MeasurementHistory) {
            val intent = Intent(ctx, ResultActivity::class.java)
            intent.putExtra(EXTRA_DATA, data)
            ctx.startActivity(intent)
        }
    }
}