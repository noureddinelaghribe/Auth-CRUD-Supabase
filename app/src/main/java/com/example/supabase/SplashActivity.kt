package com.example.supabase


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.supabase.Repository.Auth.AuthState
import com.example.supabase.ViewModel.Auth.AuthViewModel
import com.example.supabase.ViewModel.User.UserViewModel

class SplashActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        //userViewModel.fetchUsers()

        // observe users list
        userViewModel.users.observe(this) { userList ->
            // هنا استقبل البيانات و اعرضها في RV مثلاً
            Log.d("","Users = $userList")
        }


        // التحقق من حالة المصادقة بعد تأخير قصير
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthAndNavigate()
        }, 2000) // 2 ثانية تأخير
    }

    private fun checkAuthAndNavigate() {
        val authState = authViewModel.checkAuth()

        val intent = when (authState) {
            is AuthState.LoggedIn -> {

                //val user = userViewModel.fetchUsers()
                //Log.d("Users", user.toString())

                // المستخدم مسجل الدخول، انتقل إلى الصفحة الرئيسية
                Intent(this, HomeActivity::class.java)
            }
            is AuthState.LoggedOut -> {
                // المستخدم غير مسجل الدخول، انتقل إلى صفحة تسجيل الدخول
                Intent(this, SignInActivity::class.java)
            }
        }

        //startActivity(intent)
        //finish() // إغلاق شاشة البداية
    }
}