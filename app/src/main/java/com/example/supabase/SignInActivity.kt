package com.example.supabase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.supabase.Repository.Auth.AuthResult
import com.example.supabase.ViewModel.Auth.AuthViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class SignInActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var signInButton: MaterialButton
    private lateinit var signUpText: TextView
    private lateinit var forgetPasswordText: TextView
    private lateinit var progressBar: ProgressBar

    /** دالة تهيئ شاشة تسجيل الدخول */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initViews()
        setupObservers()
        setupClickListeners()
    }

    /** دالة تربط عناصر الواجهة بمتغيرات الكود */
    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.signInButton)
        signUpText = findViewById(R.id.signUpText)
        forgetPasswordText = findViewById(R.id.forgetPasswordText)
        progressBar = findViewById(R.id.progressBar)
    }

    /** دالة تراقب حالة التحميل ونتيجة المصادقة */
    private fun setupObservers() {
        // مراقبة حالة التحميل
        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            signInButton.isEnabled = !isLoading
            signInButton.text = if (isLoading) "" else "تسجيل الدخول"
        }

        // مراقبة نتيجة العملية
        viewModel.result.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Toast.makeText(this, "تم تسجيل الدخول بنجاح!", Toast.LENGTH_SHORT).show()
                    navigateToSplash()
                    viewModel.clearResult()
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

    /** دالة تضبط أزرار التسجيل والتنقل */
    private fun setupClickListeners() {
        // زر تسجيل الدخول
        signInButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            viewModel.signIn(email, password)
        }

        // نص إنشاء حساب
        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // نص نسيت كلمة المرور
        forgetPasswordText.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
    }

    /** دالة تنقل المستخدم إلى شاشة البداية */
    private fun navigateToSplash() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}