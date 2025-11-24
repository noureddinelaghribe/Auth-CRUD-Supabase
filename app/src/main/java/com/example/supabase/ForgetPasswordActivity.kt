package com.example.supabase

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.supabase.Repository.Auth.AuthResult
import com.example.supabase.ViewModel.Auth.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ForgetPasswordActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var emailEditText: TextInputEditText
    private lateinit var resetButton: MaterialButton
    private lateinit var signInText: TextView
    private lateinit var backButton: ImageView
    private lateinit var progressBar: ProgressBar

    /** دالة تهيئ شاشة استعادة كلمة المرور */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        initViews()
        setupObservers()
        setupClickListeners()
    }

    /** دالة تربط عناصر الواجهة */
    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        resetButton = findViewById(R.id.resetButton)
        signInText = findViewById(R.id.signInText)
        backButton = findViewById(R.id.backButton)
        progressBar = findViewById(R.id.progressBar)
    }

    /** دالة تراقب حالة التحميل ونتيجة الطلب */
    private fun setupObservers() {
        // مراقبة حالة التحميل
        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            resetButton.isEnabled = !isLoading
            resetButton.text = if (isLoading) "" else "إرسال رابط إعادة التعيين"
        }

        // مراقبة نتيجة العملية
        viewModel.result.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Toast.makeText(
                        this,
                        "تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك الإلكتروني",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.clearResult()

                    // العودة إلى صفحة تسجيل الدخول
                    finish()
                }
                is AuthResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    viewModel.clearResult()
                }
                null -> {
                    // لا توجد نتيجة بعد
                }
            }
        }
    }

    /** دالة تضبط أزرار الإرسال والرجوع */
    private fun setupClickListeners() {
        // زر إعادة التعيين
        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            viewModel.resetPassword(email)
        }

        // نص تسجيل الدخول
        signInText.setOnClickListener {
            finish() // العودة إلى صفحة تسجيل الدخول
        }

        // زر الرجوع
        backButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("GestureBackNavigation")
    /** دالة تغلق الشاشة عند الرجوع */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}