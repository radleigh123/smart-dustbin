package com.eldroid.trashbincloud.view.userguide

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.eldroid.trashbincloud.contract.userguide.UserGuideContract
import com.eldroid.trashbincloud.databinding.ActivityUserGuideBinding
import com.eldroid.trashbincloud.presenter.userguide.UserGuidePresenter
import com.eldroid.trashbincloud.utils.ThemePreferences

class UserGuideActivity : AppCompatActivity(), UserGuideContract.View {

    private lateinit var binding: ActivityUserGuideBinding
    private lateinit var presenter: UserGuideContract.Presenter
    private val steps = mutableListOf<StepItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemePreferences.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityUserGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = UserGuidePresenter(this)

//        steps.add(StepItem(binding.step1Header, binding.step1Content, binding.step1Arrow))
//        steps.add(StepItem(binding.step2Header, binding.step2Content, binding.step2Arrow))
//        steps.add(StepItem(binding.step3Header, binding.step3Content, binding.step3Arrow))
//        steps.add(StepItem(binding.step4Header, binding.step4Content, binding.step4Arrow))
//        steps.add(StepItem(binding.step5Header, binding.step5Content, binding.step5Arrow))

        setupListeners()
    }


    private fun setupListeners() {
        // Back button
        binding.backButton?.setOnClickListener {
            presenter.onBackPressed()
        }

        // Step toggle listeners
        steps.forEachIndexed { index, step ->
            step.header.setOnClickListener {
                val isExpanded = step.content.visibility == View.VISIBLE
                presenter.onStepClicked(index, isExpanded) // Pass CURRENT state
            }
        }

        // Contact support button
        binding.btnContactSupport?.setOnClickListener {
            presenter.onContactSupportClicked()
        }

        // View FAQs button
        binding.btnViewFaqs?.setOnClickListener {
            presenter.onViewFAQsClicked()
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun toggleStep(stepIndex: Int, expand: Boolean) {
        if (stepIndex !in steps.indices) return
        val step = steps[stepIndex]

        if (expand) step.content.expand()
        else step.content.collapse()

        // Rotate arrow
        step.arrow.animate().rotation(if (expand) 90f else 270f).setDuration(250).start()
    }

    override fun openEmailClient(email: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

        try {
            startActivity(Intent.createChooser(intent, "Send email"))
        } catch (e: Exception) {
            showMessage("No email app found")
        }
    }

    override fun finishActivity() {
        finish()
    }
}

data class StepItem(
    val header: View,
    val content: View,
    val arrow: View
)
fun View.expand(duration: Long = 250) {
    measure(
        View.MeasureSpec.makeMeasureSpec(
            (parent as ViewGroup).width - (parent as ViewGroup).paddingStart - (parent as ViewGroup).paddingEnd,
            View.MeasureSpec.EXACTLY
        ),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    val targetHeight = measuredHeight
    layoutParams.height = 0
    visibility = View.VISIBLE

    val animator = ValueAnimator.ofInt(0, targetHeight)
    animator.addUpdateListener {
        layoutParams.height = it.animatedValue as Int
        requestLayout()
    }
    animator.duration = duration
    animator.start()
}

fun View.collapse(duration: Long = 250) {
    val initialHeight = height
    val animator = ValueAnimator.ofInt(initialHeight, 0)
    animator.addUpdateListener {
        layoutParams.height = it.animatedValue as Int
        requestLayout()
    }
    animator.duration = duration
    animator.start()
    animator.doOnEnd { visibility = View.GONE }
}