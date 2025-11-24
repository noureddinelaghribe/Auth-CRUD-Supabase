package com.example.supabase.data

data class ApiItem(
    val openAi: ApiDetail? = null,
    val claude: ApiDetail? = null,
    val grok: ApiDetail? = null,
    val deepseek: ApiDetail? = null,
    val llama: ApiDetail? = null,
    val huggingFace: ApiDetail? = null
)