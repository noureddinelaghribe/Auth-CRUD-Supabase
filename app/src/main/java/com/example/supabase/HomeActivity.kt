package com.example.supabase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class HomeActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel
    private val viewModel: AuthViewModel by viewModels()

//    private lateinit var userText: TextView
//    private lateinit var signOutButton: MaterialButton
//    private lateinit var progressBar: ProgressBar

    // Views
    private lateinit var welcomeText: TextView
    private lateinit var editToggleButton: MaterialButton
    private lateinit var saveButton: MaterialButton
    private lateinit var signOutButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    // EditText fields
    private lateinit var userIdEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var deviceIdEditText: TextInputEditText
    private lateinit var roleDropdown: AutoCompleteTextView

    private lateinit var btn_history: Button
    private lateinit var btn_setting: Button
    private lateinit var btn_plan: Button
    private lateinit var btn_Referral: Button

    // Data
    private var currentUser: User? = null
    private var isEditMode = false

    /** دالة تهيئ شاشة المنزل وتربط المراقبين */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupRoleDropdown()
        setupObservers()
        setupClickListeners()
        loadUserData()
    }

    /** دالة تربط عناصر الواجهة المختلفة */
    private fun initViews() {
        // TextViews
        welcomeText = findViewById(R.id.welcomeText)

        // Buttons
        editToggleButton = findViewById(R.id.editToggleButton)
        saveButton = findViewById(R.id.saveButton)
        signOutButton = findViewById(R.id.signOutButton)

        // ProgressBar
        progressBar = findViewById(R.id.progressBar)

        // EditText fields
        userIdEditText = findViewById(R.id.userIdEditText)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        deviceIdEditText = findViewById(R.id.deviceIdEditText)
        roleDropdown = findViewById(R.id.roleDropdown)

        btn_history = findViewById(R.id.button4)
        btn_setting = findViewById(R.id.button2)
        btn_plan = findViewById(R.id.button3)
        btn_Referral = findViewById(R.id.button)

    }

    /** دالة تهيئ قائمة الأدوار للمستخدم */
    private fun setupRoleDropdown() {
        val roles = arrayOf("USER", "ADMIN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        roleDropdown.setAdapter(adapter)
    }

    /** دالة تراقب بيانات المستخدم من الـ ViewModel */
    private fun setupObservers() {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // observe users list
        userViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        userViewModel.users.observe(this) { userList ->
            // هنا استقبل البيانات و اعرضها في RV مثلاً
            Log.d("","Users = ${userList}")
            currentUser = userList.firstOrNull()
            displayUserData()
        }
        userViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Log.d("","Error = ${errorMessage}")
                navigateToSignIn()
            }

        }

    }


    /** دالة تضبط المستمعين للأزرار */
    private fun setupClickListeners() {
        // Edit toggle button
        editToggleButton.setOnClickListener {
            toggleEditMode()
        }

        // Save button
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveUserData()
            }
        }

        // Sign out button
        signOutButton.setOnClickListener {
            signOut()
        }

        btn_history.setOnClickListener {
            navigateToHistory()
        }

        btn_setting.setOnClickListener {
            navigateToSetting()
        }

        btn_plan.setOnClickListener {
            navigateToPlan()
        }

        btn_Referral.setOnClickListener {
            navigateToReferral()
        }

    }


    /** دالة تطلب بيانات المستخدم الحالية */
    private fun loadUserData() {
        // تحميل بيانات المستخدم من ViewModel
        // البريد الإلكتروني سيتم تحميله تلقائياً من خلال Observer
        userViewModel.fetchUsers()
    }


    /** دالة تعرض بيانات المستخدم في الحقول */
    private fun displayUserData() {
        currentUser?.let { user ->
            welcomeText.text = "مرحباً ${user.name}!"

            userIdEditText.setText(user.user_id ?: "")
            nameEditText.setText(user.name)
            emailEditText.setText(user.email)
            passwordEditText.setText(user.password_hash)
            //deviceIdEditText.setText(user.device_id ?: "")
            roleDropdown.setText(user.role ?: "USER", false)
        }
    }

    /** دالة تبدل بين وضع القراءة والتحرير */
    private fun toggleEditMode() {
        isEditMode = !isEditMode

        // Enable/Disable fields (except user_id which is always read-only)
        nameEditText.isEnabled = isEditMode
        emailEditText.isEnabled = isEditMode
        passwordEditText.isEnabled = isEditMode
        deviceIdEditText.isEnabled = isEditMode
        roleDropdown.isEnabled = isEditMode

        // Show/Hide save button
        saveButton.visibility = if (isEditMode) View.VISIBLE else View.GONE

        // Change button text and icon
        if (isEditMode) {
            editToggleButton.text = "إلغاء"
            editToggleButton.setIconResource(android.R.drawable.ic_menu_close_clear_cancel)
        } else {
            editToggleButton.text = "تعديل"
            editToggleButton.setIconResource(android.R.drawable.ic_menu_edit)
            // Reload data to cancel changes
            displayUserData()
        }
    }

    /** دالة تتحقق من صحة الحقول قبل الحفظ */
    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate name
        if (nameEditText.text.isNullOrBlank()) {
            nameEditText.error = "الاسم مطلوب"
            isValid = false
        }

        // Validate email
        val email = emailEditText.text.toString()
        if (email.isBlank()) {
            emailEditText.error = "البريد الإلكتروني مطلوب"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "البريد الإلكتروني غير صحيح"
            isValid = false
        }

        // Validate password
        if (passwordEditText.text.isNullOrBlank()) {
            passwordEditText.error = "كلمة المرور مطلوبة"
            isValid = false
        }

        return isValid
    }

    /** دالة تحفظ التعديلات المحدثة للمستخدم */
    private fun saveUserData() {
        showLoading(true)

        // Create updated user object
        val updatedUser = User(
            name = nameEditText.text.toString().trim(),
            email = emailEditText.text.toString().trim(),
            password_hash = passwordEditText.text.toString(),
            //device_id = deviceIdEditText.text.toString().trim().ifEmpty { null },
            role = roleDropdown.text.toString()
        )

        userViewModel.updateUser(currentUser?.user_id ?: "", updatedUser)

        // TODO: Save to database
        // Simulate network delay
        saveButton.postDelayed({
            currentUser = updatedUser
            displayUserData()
            toggleEditMode()
            showLoading(false)

            Snackbar.make(findViewById(android.R.id.content), "تم حفظ التغييرات بنجاح", Snackbar.LENGTH_SHORT).show()
        }, 1500)
    }

    /** دالة تنفذ عملية تسجيل الخروج */
    private fun signOut() {
        Snackbar.make(findViewById(android.R.id.content), "جاري تسجيل الخروج...", Snackbar.LENGTH_SHORT).show()
        viewModel.signOut()
        navigateToSignIn()
    }

    /** دالة مساعدة لإظهار أو إخفاء مؤشر التحميل */
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        saveButton.isEnabled = !show
        editToggleButton.isEnabled = !show
        signOutButton.isEnabled = !show
    }


    /** دالة تنقل المستخدم إلى شاشة تسجيل الدخول */
    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    /** دالة تفتح شاشة السجل */
    private fun navigateToHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    /** دالة تفتح شاشة الإعدادات */
    private fun navigateToSetting() {
        val intent = Intent(this, SettingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    /** دالة تفتح شاشة الباقات */
    private fun navigateToPlan() {
        val intent = Intent(this, PlanActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    /** دالة تفتح شاشة الإحالات */
    private fun navigateToReferral() {
        val intent = Intent(this, ReferralActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    @SuppressLint("GestureBackNavigation")
    /** دالة تغلق كل الشاشات عند الرجوع */
    override fun onBackPressed() {
        super.onBackPressed()
        // منع المستخدم من العودة إلى الشاشات السابقة
        // يمكن إضافة dialog للتأكيد من الخروج
        finishAffinity()
    }
}