package com.example.supabase

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supabase.ViewModel.History.HistoryViewModel
import com.example.supabase.adapter.HistoryAdapter
import com.example.supabase.data.History
import com.example.supabase.data.HistoryRequest
import com.example.supabase.utels.PreferencesHelper
import com.google.android.material.textfield.TextInputEditText

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var btnCreate: Button
    private lateinit var btnDelete: Button

    private lateinit var etActionType: TextInputEditText
    private lateinit var etInputText: TextInputEditText
    private lateinit var etOutputText: TextInputEditText
    private lateinit var etLanguage: TextInputEditText

    private lateinit var historyAdapter: HistoryAdapter
    //private val historyList = mutableListOf<History>()
    private var selectedHistory: History? = null

    private lateinit var historyViewModel: HistoryViewModel

    private lateinit var prefsHelper: PreferencesHelper

    /** دالة تهيئ واجهة الشاشة ومراقبة البيانات */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        prefsHelper = PreferencesHelper(this)

        initViews()
        setupObservers()
        //setupRecyclerView()
        setupClickListeners()
    }

    /** دالة تربط عناصر الواجهة بمتغيرات الكود */
    private fun initViews() {
        rvHistory = findViewById(R.id.rvHistory)
        btnCreate = findViewById(R.id.btnCreate)
        btnDelete = findViewById(R.id.btnDelete)

        etActionType = findViewById(R.id.etActionType)
        etInputText = findViewById(R.id.etInputText)
        etOutputText = findViewById(R.id.etOutputText)
        etLanguage = findViewById(R.id.etLanguage)
    }

    /** دالة تهيئ الـ ViewModel وتتابع حالته */
    private fun setupObservers() {

        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        historyViewModel.loadHistory()

        historyViewModel.loading.observe(this) { /* يمكن إظهار مؤشر تحميل */ }
        historyViewModel.history.observe(this) { history ->
            setupRecyclerView(history)
        }
        historyViewModel.error.observe(this) { /* معالجة الأخطاء إن وجدت */ }
        historyViewModel.successInsert.observe(this) { success ->
            Toast.makeText(this, "Create: $success", Toast.LENGTH_SHORT).show()
            clearFields()
        }
        historyViewModel.successDelete.observe(this) { success ->
            Toast.makeText(this, "Delete: ${selectedHistory?.id}", Toast.LENGTH_SHORT).show()
            clearFields()
            selectedHistory = null
        }

    }

    /** دالة تجهز قائمة السجلات وتتعامل مع النقر على العناصر */
    private fun setupRecyclerView(historyList: List<History>) {
        historyAdapter = HistoryAdapter(historyList)
        rvHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }

        // Handle item click to populate fields for update
        historyAdapter.onItemClick = { history ->
            selectedHistory = history
            etActionType.setText(history.action_type)
            etInputText.setText(history.input_text)
            etOutputText.setText(history.output_text)
            etLanguage.setText(history.language)
            Toast.makeText(this, "Selected: ${history.action_type}", Toast.LENGTH_SHORT).show()
        }
    }

    /** دالة تضبط أزرار الإضافة والحذف */
    private fun setupClickListeners() {
        btnCreate.setOnClickListener {
            val actionType = etActionType.text.toString()
            val inputText = etInputText.text.toString()
            val outputText = etOutputText.text.toString()
            val language = etLanguage.text.toString()

            if (actionType.isNotEmpty()) {
                historyViewModel.insertHistory(HistoryRequest(
                    user_id = prefsHelper.getUserId(),
                    language = language,
                    action_type = actionType,
                    input_text = inputText,
                    output_text = outputText
                ))

            } else {
                Toast.makeText(this, "Please fill Action Type", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            if (selectedHistory != null) {
                selectedHistory?.id?.let { id ->
                    historyViewModel.deleteHistory(id)
                }

            } else {
                Toast.makeText(this, "Please select an item first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** دالة تنظف الحقول بعد التنفيذ */
    private fun clearFields() {
        etActionType.text?.clear()
        etInputText.text?.clear()
        etOutputText.text?.clear()
        etLanguage.text?.clear()
    }
}