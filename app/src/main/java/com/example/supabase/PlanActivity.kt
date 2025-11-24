package com.example.supabase

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.supabase.ViewModel.Plan.PlanViewModel
import com.example.supabase.ViewModel.Subscription.SubscriptionViewModel
import com.example.supabase.data.Plan
import com.example.supabase.data.Subscription
import com.example.supabase.adapter.SubscriptionAdapter
import com.example.supabase.utels.PreferencesHelper
import com.google.android.material.button.MaterialButton

class PlanActivity : AppCompatActivity() {

    private lateinit var planViewModel: PlanViewModel
    private lateinit var subscriptionViewModel: SubscriptionViewModel

    private lateinit var prefsHelper: PreferencesHelper

    private lateinit var freePlanFeatures: TextView
    private lateinit var basicPlanFeatures: TextView
    private lateinit var proPlanFeatures: TextView
    private lateinit var ultraPlanFeatures: TextView

    private lateinit var selectBasicPlan: MaterialButton
    private lateinit var selectProPlan: MaterialButton
    private lateinit var selectUltraPlan: MaterialButton

    private lateinit var rvSubscriptions: RecyclerView
    private var subscriptionAdapter: SubscriptionAdapter? = null


    /** دالة تهيئ واجهة شاشة الباقات */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)

        prefsHelper = PreferencesHelper(this)

        initViews()
        setupObservers()
        setupObserversSubscription()

        planViewModel.loadPlans()
        subscriptionViewModel.loadSubscriptions()


    }

    /** دالة تربط عناصر الواجهة */
    private fun initViews() {
        freePlanFeatures = findViewById(R.id.freePlanFeatures)
        basicPlanFeatures = findViewById(R.id.basicPlanFeatures)
        proPlanFeatures = findViewById(R.id.proPlanFeatures)
        ultraPlanFeatures = findViewById(R.id.ultraPlanFeatures)

        selectBasicPlan = findViewById(R.id.selectBasicPlan)
        selectProPlan = findViewById(R.id.selectProPlan)
        selectUltraPlan = findViewById(R.id.selectUltraPlan)

        rvSubscriptions = findViewById(R.id.rvSubscriptions)
        rvSubscriptions.layoutManager = LinearLayoutManager(this)
        subscriptionAdapter = SubscriptionAdapter(emptyList())
        rvSubscriptions.adapter = subscriptionAdapter
    }

    /** دالة تراقب بيانات الباقات وتعرضها */
    private fun setupObservers() {
        planViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[PlanViewModel::class.java]

        planViewModel.plans.observe(this) { plans -> updatePlans(plans) }
        planViewModel.error.observe(this) { msg ->
            msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    /** دالة توزع مزايا كل باقة */
    private fun updatePlans(plans: List<Plan>) {
        val free = plans.find { it.name.equals("free", ignoreCase = true) }
        val basic = plans.find { it.name.equals("basic", ignoreCase = true) }
        val pro = plans.find { it.name.equals("pro", ignoreCase = true) }
        val ultra = plans.find { it.name.equals("ultra", ignoreCase = true) }

        free?.let { plan ->
            freePlanFeatures.text = buildFeatures(plan)
        }

        basic?.let { plan ->
            basicPlanFeatures.text = buildFeatures(plan)
            selectBasicPlan.setOnClickListener {
                Toast.makeText(this, "${plan.planId} Selected Basic (${plan.price})", Toast.LENGTH_SHORT).show()
                subscriptionViewModel.insertSubscription(Subscription(
                    plan_id = plan.planId,
                    user_id = prefsHelper.getUserId()
                ))
            }
        }

        pro?.let { plan ->
            proPlanFeatures.text = buildFeatures(plan)
            selectProPlan.setOnClickListener {
                Toast.makeText(this, "${plan.planId} Selected Pro (${plan.price})", Toast.LENGTH_SHORT).show()
                subscriptionViewModel.insertSubscription(Subscription(
                    plan_id = plan.planId,
                    user_id = prefsHelper.getUserId()
                ))
            }
        }

        ultra?.let { plan ->
            ultraPlanFeatures.text = buildFeatures(plan)
            selectUltraPlan.setOnClickListener {
                Toast.makeText(this, "${plan.planId} Selected Ultra (${plan.price})", Toast.LENGTH_SHORT).show()
                subscriptionViewModel.insertSubscription(Subscription(
                    plan_id = plan.planId,
                    user_id = prefsHelper.getUserId()
                ))
            }
        }
    }

    /** دالة تنسق نص المزايا */
    private fun buildFeatures(plan: Plan): String {
        val lines = listOfNotNull(plan.description)
            .filter { it.isNotBlank() }
        return lines.joinToString("\n")
    }


    /** دالة تراقب بيانات الاشتراكات الحالية */
    private fun setupObserversSubscription() {

        subscriptionViewModel = ViewModelProvider(this).get(SubscriptionViewModel::class.java)

        subscriptionViewModel.loading.observe(this) { /* يمكن استخدام مؤشر تحميل */ }
        subscriptionViewModel.subscriptions.observe(this) { list ->
            subscriptionAdapter?.submitList(list)
        }
        subscriptionViewModel.error.observe(this) { /* عرض الخطأ عند الحاجة */ }

    }

}