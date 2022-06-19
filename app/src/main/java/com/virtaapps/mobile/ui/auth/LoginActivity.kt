package com.virtaapps.mobile.ui.auth

import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.afollestad.vvalidator.form
import com.google.firebase.auth.FirebaseUser
import com.virtaapps.mobile.R
import com.virtaapps.mobile.base.BaseActivity
import com.virtaapps.mobile.databinding.ActivityLoginBinding
import com.virtaapps.mobile.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding =
        ActivityLoginBinding::inflate

    private val viewModel: AuthViewModel by viewModels()

    override fun setup() {
        if (viewModel.validateIsLoggedIn()) {
            goToMain()
        }

        form {
            useRealTimeValidation()
            input(binding.etEmail, name = null) {
                isNotEmpty().description("Email wajib diisi")
                isEmail().description("Silahkan masukan email yang valid!")
            }

            input(binding.etPassword, name = null) {
                isNotEmpty().description("Password wajib diisi")
            }

            submitWith(binding.btnLogin) {
                viewModel.login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                    .observe(this@LoginActivity, setLoginObserver())
            }
        }

        val spannable = SpannableStringBuilder("Belum punya akun? Daftar")
        spannable.setSpan(
            ForegroundColorSpan(getColorResource(R.color.orange_500)),
            18, // start
            24, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding.tvRegister.text = spannable
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setLoginObserver() = setObserver<FirebaseUser?>(
        onSuccess = {
            binding.progressBar.gone()
            showToast("Login berhasil")
            goToMain()
        },
        onError = {
            binding.progressBar.gone()
            showToast(it.message.toString())
        },
        onLoading = { binding.progressBar.visible() }
    )

    companion object {
        fun startActivity(ctx: Context) {
            ctx.startActivity(Intent(ctx, LoginActivity::class.java))
        }
    }
}