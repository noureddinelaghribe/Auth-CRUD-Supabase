package com.example.supabase


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

class SignUpActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var signUpButton: MaterialButton
    private lateinit var signInText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        signUpButton = findViewById(R.id.signUpButton)
        signInText = findViewById(R.id.signInText)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupObservers() {
        // مراقبة حالة التحميل
        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            signUpButton.isEnabled = !isLoading
            signUpButton.text = if (isLoading) "" else "إنشاء حساب"
        }

        // مراقبة نتيجة العملية
        viewModel.result.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Toast.makeText(
                        this,
                        "تم إنشاء الحساب بنجاح! يرجى التحقق من بريدك الإلكتروني.",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.clearResult()

                    // الانتقال إلى صفحة تسجيل الدخول
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

    private fun setupClickListeners() {
        // زر إنشاء الحساب
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // التحقق من تطابق كلمة المرور
            if (password != confirmPassword) {
                Toast.makeText(this, "كلمات المرور غير متطابقة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signUp(email, password)
        }

        // نص تسجيل الدخول
        signInText.setOnClickListener {
            finish() // العودة إلى صفحة تسجيل الدخول
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}