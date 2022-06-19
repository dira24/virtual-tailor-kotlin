package com.virtaapps.mobile.ui.detect

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.afollestad.vvalidator.form
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.squareup.picasso.Picasso
import com.virtaapps.mobile.R
import com.virtaapps.mobile.base.BaseActivity
import com.virtaapps.mobile.base.BaseResponse
import com.virtaapps.mobile.data.model.DetectionResult
import com.virtaapps.mobile.data.model.MeasurementHistory
import com.virtaapps.mobile.databinding.ActivityDetectBinding
import com.virtaapps.mobile.ui.result.ResultActivity
import com.virtaapps.mobile.utils.BASE_URL
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File

@AndroidEntryPoint
class DetectActivity : BaseActivity<ActivityDetectBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityDetectBinding =
        ActivityDetectBinding::inflate

    private var file: File? = null
    private var type: String = ""
    private var result: DetectionResult? = null
    private val viewModel: DetectViewModel by viewModels()

    override fun setup() {
        setupBackButton()
        setTitle("Masukkan Data Pengukuran")
        setupListener()
        form {
            useRealTimeValidation()
            input(binding.etName, name = null) {
                isNotEmpty().description("Nama wajib diisi")
            }

            input(binding.etAddress, name = null) {
                isNotEmpty().description("Alamat wajib diisi")
            }

            submitWith(binding.btnDetect) {
                file?.let { data ->
                    viewModel.detectImage(data)
                        .observe(this@DetectActivity, setDetectObserver())
                }
            }
        }
    }

    private fun setDetectObserver() = setObserver<BaseResponse<DetectionResult>>(
        onSuccess = {
            binding.progressBar.gone()
            val (byteArr, bitmap) = setupByteArray()
            result = it.data?.data
            binding.imgBody.setImageBitmap(bitmap)
            viewModel.saveMeasurement(MeasurementHistory(
                "",
                binding.etName.text.toString(),
                binding.etAddress.text.toString(),
                type,
                result!!.bahu,
                result!!.tangan,
                result!!.badan,
                result!!.ukuran,
                BASE_URL + result!!.gambar
            )).observe(this, setSaveMeasurementObserver())
        },
        onError = {
            binding.progressBar.gone()
            showToast(it.message.toString())
        },
        onLoading = { binding.progressBar.visible() }
    )

    private fun setSaveMeasurementObserver() = setObserver<MeasurementHistory?>(
        onSuccess = {
            binding.progressBar.gone()
            it.data?.let { data ->
                ResultActivity.startActivity(this, data)
                finish()
            }
        },
        onError = {
            binding.progressBar.gone()
            showToast(it.message.toString())
        },
        onLoading = { binding.progressBar.visible() }
    )

    private fun setupByteArray(): Pair<ByteArray, Bitmap> {
        val bitmap = BitmapFactory.decodeFile(file?.absolutePath)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return Pair(baos.toByteArray(), bitmap)
    }

    private fun setupListener() {
        binding.imgBody.setOnClickListener {
            askPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                onAccepted = {
                    Pix.start(this@DetectActivity,
                        Options.init()
                            .setRequestCode(CAMERA_REQUEST_CODE)
                            .setMode(Options.Mode.Picture))
                }
            )
        }

        binding.rgType.setOnCheckedChangeListener { _, id ->
            when(id) {
                R.id.rb_children -> type = "Anak-anak"
                R.id.rb_adult -> type = "Dewasa"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val returnValue: ArrayList<String> = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS) as ArrayList<String>
            file = File(returnValue[0])
            Picasso.get().load(file!!).into(binding.imgBody)
            binding.tvInfo.visible()
        }
    }

    companion object {
        const val CAMERA_REQUEST_CODE = 101
        fun startActivity(ctx: Context) {
            ctx.startActivity(Intent(ctx, DetectActivity::class.java))
        }
    }
}