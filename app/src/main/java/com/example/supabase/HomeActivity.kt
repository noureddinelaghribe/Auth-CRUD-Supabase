package com.example.supabase

import android.annotation.SuppressLint
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

class HomeActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var emailText: TextView
    private lateinit var signOutButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupObservers()
        setupClickListeners()
        loadUserData()
    }

    private fun initViews() {
        emailText = findViewById(R.id.emailText)
        signOutButton = findViewById(R.id.signOutButton)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupObservers() {
        // مراقبة حالة التحميل
        viewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            signOutButton.isEnabled = !isLoading
            signOutButton.text = if (isLoading) "" else "تسجيل الخروج"
        }

        // مراقبة نتيجة العملية
        viewModel.result.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Toast.makeText(this, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
                    navigateToSignIn()
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

        // مراقبة البريد الإلكتروني
        viewModel.userEmail.observe(this) { email ->
            emailText.text = email ?: "مستخدم"
        }
    }

    private fun setupClickListeners() {
        // زر تسجيل الخروج
        signOutButton.setOnClickListener {
            viewModel.signOut()
        }
    }

    private fun loadUserData() {
        // تحميل بيانات المستخدم من ViewModel
        // البريد الإلكتروني سيتم تحميله تلقائياً من خلال Observer
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        super.onBackPressed()
        // منع المستخدم من العودة إلى الشاشات السابقة
        // يمكن إضافة dialog للتأكيد من الخروج
        finishAffinity()
    }
}