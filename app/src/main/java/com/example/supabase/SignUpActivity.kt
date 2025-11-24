package com.example.supabase


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.supabase.Repository.Auth.AuthResult
import com.example.supabase.ViewModel.Auth.AuthViewModel
import com.example.supabase.ViewModel.User.UserViewModel
import com.example.supabase.data.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

import android.provider.Settings
import android.util.Log
import com.example.supabase.ViewModel.Referral.ReferralViewModel
import com.example.supabase.ViewModel.ReferralCodes.ReferralCodeViewModel
import com.example.supabase.ViewModel.Subscription.SubscriptionViewModel
import com.example.supabase.ViewModel.UserDevice.UserDeviceViewModel
import com.example.supabase.data.Referral
import com.example.supabase.data.ReferralCode
import com.example.supabase.data.Subscription
import com.example.supabase.data.UserDevice
import com.example.supabase.utels.PreferencesHelper

class SignUpActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var userViewModel: UserViewModel
    private lateinit var referralCodeViewModel: ReferralCodeViewModel
    private lateinit var subscriptionViewModel: SubscriptionViewModel
    private lateinit var userDeviceViewModel: UserDeviceViewModel
    private lateinit var referralViewModel: ReferralViewModel


    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var referralCodeEditText: TextInputEditText
    private lateinit var signUpButton: MaterialButton
    private lateinit var signInText: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var prefsHelper: PreferencesHelper

    /** دالة تهيئ شاشة إنشاء الحساب وتربط المراقبين */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        prefsHelper = PreferencesHelper(this)

        initViews()
        setupClickListeners()
        setupObserversUserDevice()
        setupObserversAuth()
        setupObserversInsertUser()
        setupObserversReferralCode()
        setupObserversSubscription()
        setupObserversReferral()

    }

    /** دالة تربط عناصر الواجهة بمتغيرات الكود */
    private fun initViews() {
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        referralCodeEditText = findViewById(R.id.referralCodeEditText)
        signUpButton = findViewById(R.id.signUpButton)
        signInText = findViewById(R.id.signInText)
        progressBar = findViewById(R.id.progressBar)
    }


    /** دالة تضبط أزرار إنشاء الحساب والتنقل */
    fun setupClickListeners() {
        signUpButton.setOnClickListener {
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // التحقق من تطابق كلمة المرور
            // التحقق من أن الحقول غير فارغة
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال كلمة المرور وتأكيدها", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // التحقق من الطول الأدنى
            if (password.length < 6) {
                Toast.makeText(this, "يجب أن تكون كلمة المرور 6 أحرف على الأقل", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // التحقق من تطابق كلمة المرور
            if (password != confirmPassword) {
                Toast.makeText(this, "كلمتا المرور غير متطابقتين", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // (اختياري) التحقق من قوة كلمة المرور
//            val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}\$")
//            if (!password.matches(passwordPattern)) {
//                Toast.makeText(this, "كلمة المرور يجب أن تحتوي على أحرف وأرقام", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            //check id device if is alredy have account
            userDeviceViewModel.checkUserDevice(getAndroidId(this).trim())


        }

        // نص تسجيل الدخول
        signInText.setOnClickListener {
            //finish() // العودة إلى صفحة تسجيل الدخول
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    /** دالة تراقب حالة الأجهزة المسجلة */
    private fun setupObserversUserDevice(){

        userDeviceViewModel = ViewModelProvider(this).get(UserDeviceViewModel::class.java)

        userDeviceViewModel.loading.observe(this) { isLoading ->
            //progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        userDeviceViewModel.isExist.observe(this) { isExist ->
            if (isExist) {
                Toast.makeText(this, "تم العثور على جهاز مسجل", Toast.LENGTH_SHORT).show()
            } else {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString()
                authViewModel.signUp(email, password)
            }
        }

        userDeviceViewModel.insertSuccess.observe(this) { insertSuccess ->
            if (insertSuccess) {
                referralCodeViewModel.insertReferralCode(ReferralCode(user_id = prefsHelper.getUserId()))
            }
        }

        userDeviceViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }



    /** دالة تتابع نتيجة المصادقة من خادم Supabase */
    private fun setupObserversAuth() {
        // مراقبة حالة التحميل
        authViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            signUpButton.isEnabled = !isLoading
            signUpButton.text = if (isLoading) "" else "إنشاء حساب"
        }

        // مراقبة نتيجة العملية
        authViewModel.result.observe(this) { result ->
            Log.d("SignUpActivity","result: $result")
            when (result) {
                is AuthResult.Success -> {
                    authViewModel.clearResult()
                    userViewModel.addUser(User(
                        user_id = prefsHelper.getUserId(),
                        name = nameEditText.text.toString().trim(),
                        email = emailEditText.text.toString().trim(),
                        password_hash = passwordEditText.text.toString().trim(),
                    ))
                }
                is AuthResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                    authViewModel.clearResult()
                }
                null -> {
                    // لا توجد نتيجة بعد
                }
            }
        }
    }

    /** دالة تراقب عمليات حفظ المستخدم في قاعدة البيانات */
    private fun setupObserversInsertUser() {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        userViewModel.users.observe(this) { userList ->
            val createdUserId = userList.firstOrNull()?.user_id ?: return@observe
            userDeviceViewModel.insertUserDevice(
                UserDevice(
                    device_id = getAndroidId(baseContext),
                    user_id = createdUserId
                )
            )
        }

        userViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

    }


    /** دالة تتعامل مع كود الإحالة وإدخاله */
    private fun setupObserversReferralCode() {

        referralCodeViewModel = ViewModelProvider(this).get(ReferralCodeViewModel::class.java)

        referralCodeViewModel.referralCodeResult.observe(this) { result ->
            if (result.isNullOrEmpty()) {
                Toast.makeText(this, "تم إنشاء الحساب بنجاح!", Toast.LENGTH_SHORT).show()
                navigateToSplash()
            } else {
                val referrerId = result.first().user_id
                referralViewModel.insertReferral(
                    Referral(
                        referrer_id = referrerId,
                        referred_id = prefsHelper.getUserId(),
                        reward_given = false
                    )
                )
            }
        }

        referralCodeViewModel.insertSuccess.observe(this) { insertSuccess ->
            if (insertSuccess) {
                val referralCode = referralCodeEditText.text.toString().trim()
                referralCodeViewModel.validateReferralCode(referralCode)
            }
        }

        referralCodeViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }



    /** دالة تحفظ عمليات الإحالة والمكافآت */
    private fun setupObserversReferral(){

        referralViewModel = ViewModelProvider(this).get(ReferralViewModel::class.java)

        referralViewModel.loading.observe(this) { isLoading ->
            //progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        referralViewModel.successInsert.observe(this) { history ->

            //upgrade to base palne then signUp
            subscriptionViewModel.insertSubscription(
                Subscription(
                    plan_id = 2,
                    user_id = prefsHelper.getUserId()
                )
            )

        }

        referralViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

    }


    /** دالة تتابع نتيجة إنشاء الاشتراك الافتراضي */
    private fun setupObserversSubscription() {

        subscriptionViewModel = ViewModelProvider(this).get(SubscriptionViewModel::class.java)

        subscriptionViewModel.loading.observe(this) { isLoading ->
            //progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        subscriptionViewModel.successInsert.observe(this) { history ->
//            val email = emailEditText.text.toString().trim()
//            val password = passwordEditText.text.toString()
//            authViewModel.signUp(email, password)

            Toast.makeText(this, "تم إنشاء الحساب بنجاح!", Toast.LENGTH_SHORT).show()
            navigateToSplash()

        }

        subscriptionViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

    }





    /** دالة تعيد معرف الجهاز الفريد */
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    /** دالة تنقل المستخدم إلى شاشة البداية */
    fun navigateToSplash() {
        val intent = Intent(this, SplashActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @SuppressLint("GestureBackNavigation")
    /** دالة تغلق الشاشة عند العودة */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}