package com.example.supabase

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supabase.ViewModel.Referral.ReferralViewModel
import com.example.supabase.adapter.ReferralAdapter

class ReferralActivity : AppCompatActivity() {

    private lateinit var referralViewModel: ReferralViewModel
    private lateinit var referralAdapter: ReferralAdapter

    private lateinit var rvReferrals: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView

    /** دالة تهيئ شاشة الإحالات وتبدأ التحميل */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)

        referralViewModel = ViewModelProvider(this)[ReferralViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupObservers()

        referralViewModel.loadReferrals()
    }

    /** دالة تربط عناصر الواجهة */
    private fun initViews() {
        rvReferrals = findViewById(R.id.rvReferrals)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
    }

    /** دالة تجهز قائمة الإحالات */
    private fun setupRecyclerView() {
        referralAdapter = ReferralAdapter()
        rvReferrals.apply {
            layoutManager = LinearLayoutManager(this@ReferralActivity)
            adapter = referralAdapter
        }
    }

    /** دالة تراقب حالة الـ ViewModel */
    private fun setupObservers() {
        referralViewModel.loading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
        }

        referralViewModel.referrals.observe(this) { referrals ->
            val hasData = !referrals.isNullOrEmpty()
            tvEmptyState.isVisible = !hasData
            rvReferrals.isVisible = hasData

            referralAdapter.submitList(referrals ?: emptyList())
        }

        referralViewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrBlank()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}