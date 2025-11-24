package com.example.supabase


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.supabase.ViewModel.Auth.AuthViewModel
import com.example.supabase.ViewModel.Setting.SettingViewModel
import com.example.supabase.ViewModel.User.UserViewModel
import com.example.supabase.data.User
import com.example.supabase.utels.PreferencesHelper

class SplashActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var prefsHelper: PreferencesHelper

    private val TAG = "TAG-SplashActivity"

    /** دالة تهيئ شاشة البداية وتحدد وجهة المستخدم */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        settingViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

        prefsHelper = PreferencesHelper(this)

        if (prefsHelper.isAccessTokenExcest()){
            Log.d(TAG,"acess token is not null")
            userViewModel.fetchUsers()
        }else{
            Log.d(TAG,"acess token is null")
            navigateToSignUp()
        }

        observeUserState()
        observeSettingState()


    }


    /** دالة تراقب استرجاع بيانات المستخدم */
    fun observeUserState() {

        // observe users list
        userViewModel.loading.observe(this) { isLoading ->
            //progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        userViewModel.users.observe(this) { userList ->
            // هنا استقبل البيانات و اعرضها في RV مثلاً
            Log.d(TAG,"Users = ${userList}")
            settingViewModel.fetchSettings()
        }
        userViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Log.d(TAG,"errorMessage "+errorMessage)
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }


    /** دالة تراقب تحميل إعدادات التطبيق */
    fun observeSettingState() {

        settingViewModel.loading.observe(this) { isLoading ->

        }

        settingViewModel.settings.observe(this) { setting ->
            Log.d(TAG, "observeSettingState: "+setting.toString())
            prefsHelper.saveSetting(setting)
            navigateToHome()
        }

        settingViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Log.d(TAG, "observeSettingState: "+errorMessage)
            }
        }

    }


    /** دالة تنقل المستخدم لشاشة التسجيل بعد تأخير بسيط */
    private fun navigateToSignUp() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 2000) // 2 ثانية تأخير
    }

    /** دالة تنقل المستخدم للواجهة الرئيسية بعد التأكد من الإعدادات */
    private fun navigateToHome() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }, 2000) // 2 ثانية تأخير

    }



}
