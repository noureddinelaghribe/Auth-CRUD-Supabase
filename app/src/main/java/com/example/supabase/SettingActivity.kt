package com.example.supabase

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.supabase.data.ApiDetail
import com.example.supabase.utels.PreferencesHelper

class SettingActivity : AppCompatActivity() {

    private lateinit var tvVersion: TextView
    private lateinit var tvUpdateMode: TextView
    private lateinit var tvUpdateUrl: TextView
    private lateinit var tvMaintenanceMode: TextView
    private lateinit var tvMaintenanceMessage: TextView
    private lateinit var layoutApis: LinearLayout


    @SuppressLint("MissingInflatedId")
    /** دالة تهيئ شاشة الإعدادات وتعرض القيم المحفوظة */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        tvVersion = findViewById(R.id.tvVersion)
        tvUpdateMode = findViewById(R.id.tvUpdateMode)
        tvUpdateUrl = findViewById(R.id.tvUpdateUrl)
        tvMaintenanceMode = findViewById(R.id.tvMaintenanceMode)
        tvMaintenanceMessage = findViewById(R.id.tvMaintenanceMessage)
        layoutApis = findViewById(R.id.layoutApis)

        val prefsHelper = PreferencesHelper(this)

        val settings = prefsHelper.getSetting()

        tvVersion.text = "Version: ${settings.verstion}"
        tvUpdateMode.text = "Update Mode: ${if (settings.updateMode) "Enabled" else "Disabled"}"
        tvUpdateUrl.text = "Update URL: ${settings.updateUrl}"
        tvMaintenanceMode.text = "Maintenance Mode: ${if (settings.maintenanceMode) "Enabled" else "Disabled"}"
        tvMaintenanceMessage.text = "Maintenance Message: ${settings.maintenanceMessage}"

        settings.apisJson.forEach { apiItem ->
            // Check each API in the ApiItem
            apiItem.openAi?.let { api ->
                addApiView("OpenAI", api)
            }
            apiItem.claude?.let { api ->
                addApiView("Claude", api)
            }
            apiItem.grok?.let { api ->
                addApiView("Grok", api)
            }
            apiItem.deepseek?.let { api ->
                addApiView("DeepSeek", api)
            }
            apiItem.llama?.let { api ->
                addApiView("Llama", api)
            }
            apiItem.huggingFace?.let { api ->
                addApiView("HuggingFace", api)
            }
        }





    }

    /** دالة تضيف عنصر نصي لكل API مفعل */
    private fun addApiView(apiName: String, api: ApiDetail) {
        val tvApi = TextView(this)
        tvApi.text = "$apiName - Enabled: ${if (api.enabled) "Yes" else "No"}, Model: ${api.model}"
        tvApi.textSize = 16f
        tvApi.setPadding(0, 8, 0, 8)
        layoutApis.addView(tvApi)
    }
}