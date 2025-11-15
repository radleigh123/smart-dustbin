package com.eldroid.trashbincloud.view

import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.HistoryContract
import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.presenter.HistoryPresenter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryFragment : Fragment(R.layout.fragment_history), HistoryContract.View {

    private lateinit var presenter: HistoryPresenter
    private lateinit var filterButton: ImageView
    private lateinit var filterSection: ConstraintLayout
    private lateinit var allEventsButton: TextView
    private lateinit var autoOpenButton: TextView
    private lateinit var autoCloseButton: TextView
    private lateinit var binFullButton: TextView
    private lateinit var binDropdown: ConstraintLayout
    private lateinit var binDropdownText: TextView
    private lateinit var dateInput: ConstraintLayout
    private lateinit var dateInputText: TextView
    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var activityAdapter: HistoryAdapter

    private lateinit var noActivityTextView: TextView

    private var selectedBin: String = "All Bins"
    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupPresenter()
        setupListeners()
    }

    private fun initViews(view: View) {
        filterButton = view.findViewById(R.id.filterButton)
        filterSection = view.findViewById(R.id.filterSection)
        allEventsButton = view.findViewById(R.id.allEventsButton)
        autoOpenButton = view.findViewById(R.id.autoOpenButton)
        autoCloseButton = view.findViewById(R.id.autoCloseButton)
        binFullButton = view.findViewById(R.id.binFullButton)
        binDropdown = view.findViewById(R.id.binDropdown)
        binDropdownText = view.findViewById(R.id.binDropdownText)
        dateInput = view.findViewById(R.id.dateInput)
        dateInputText = view.findViewById(R.id.dateInputText)
        activityRecyclerView = view.findViewById(R.id.activityRecyclerView)
        noActivityTextView = view.findViewById(R.id.noActivityTextView)

        // Initially hide filter section
        filterSection.visibility = View.GONE

        // Set initial dropdown text
        binDropdownText.text = selectedBin
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPresenter() {
        presenter = HistoryPresenter()
        presenter.attachView(this)
    }

    private fun setupRecyclerView() {
        activityAdapter = HistoryAdapter(mutableListOf())
        activityRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activityAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        // Filter button - toggles filter section
        filterButton.setOnClickListener {
            presenter.onFilterClicked()
        }

        // Event type filter buttons
        allEventsButton.setOnClickListener {
            presenter.onEventTypeSelected("all")
        }

        autoOpenButton.setOnClickListener {
            presenter.onEventTypeSelected("open")
        }

        autoCloseButton.setOnClickListener {
            presenter.onEventTypeSelected("close")
        }

        binFullButton.setOnClickListener {
            presenter.onEventTypeSelected("full")
        }

        // Bin dropdown
        binDropdown.setOnClickListener {
            showBinSelectionDialog()
        }

        // Date picker
        dateInput.setOnClickListener {
            showDatePicker()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showBinSelectionDialog() {
        val bins = arrayOf("All Bins", "Kitchen Bin", "Bathroom Bin", "Bedroom Bin", "Living Room Bin")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Bin")
            .setItems(bins) { dialog, which ->
                selectedBin = bins[which]
                binDropdownText.text = selectedBin
                presenter.onBinSelected(selectedBin)
                dialog.dismiss()
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Detect if night mode is active
        val isNightMode = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        // Choose theme based on mode
        val datePickerTheme = if (isNightMode) {
            R.style.DatePickerTheme_Dark
        } else {
            R.style.DatePickerTheme_Light
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            datePickerTheme,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                dateInputText.text = selectedDate!!.format(formatter)
                dateInputText.setTextColor(
                    if (isNightMode) resources.getColor(R.color.white, null)
                    else resources.getColor(R.color.black, null)
                )
                presenter.onDateSelected(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // Clear button
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear") { _, _ ->
            selectedDate = null
            dateInputText.text = "mm/dd/yyyy"
            dateInputText.setTextColor(
                if (isNightMode) resources.getColor(R.color.white, null)
                else resources.getColor(R.color.black, null)
            )
            presenter.onDateSelected(null)
        }

        datePickerDialog.show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun toggleFilterSection(show: Boolean) {
        if (show) {
            filterSection.expand()
        } else {
            filterSection.collapse()

            // Reset filters
            selectedDate = null
            selectedBin = "All Bins"
            dateInputText.text = "mm/dd/yyyy"
            dateInputText.setTextColor(resources.getColor(R.color.white_50, null))
            binDropdownText.text = selectedBin

            // Reset filter buttons in UI
            updateFilterButtons("all")

            // Notify presenter to reset filters
            presenter.onEventTypeSelected("all")  // resets type filter
            presenter.onBinSelected(selectedBin)   // resets bin filter
            presenter.onDateSelected(null)         // resets date filter
        }
    }


    override fun showWeeklyData(data: List<Int>) {
        // Bar chart implementation (already in layout)
    }

    override fun showActivityList(activities: List<ActivityEvent>) {
        if (activities.isEmpty()) {
            activityRecyclerView.visibility = View.GONE
            noActivityTextView.visibility = View.VISIBLE
        } else {
            activityRecyclerView.visibility = View.VISIBLE
            noActivityTextView.visibility = View.GONE
            activityAdapter.updateActivities(activities)
        }
    }

    override fun updateFilterButtons(selectedFilter: String) {
        // Reset all buttons to inactive state
        allEventsButton.setBackgroundResource(R.drawable.button_filter_inactive)
        allEventsButton.setTextColor(resources.getColor(R.color.text_secondary, null))

        autoOpenButton.setBackgroundResource(R.drawable.button_filter_inactive)
        autoOpenButton.setTextColor(resources.getColor(R.color.text_secondary, null))

        autoCloseButton.setBackgroundResource(R.drawable.button_filter_inactive)
        autoCloseButton.setTextColor(resources.getColor(R.color.text_secondary, null))

        binFullButton.setBackgroundResource(R.drawable.button_filter_inactive)
        binFullButton.setTextColor(resources.getColor(R.color.text_secondary, null))


        // Set active state for selected button
        when (selectedFilter) {
            "all" -> {
                allEventsButton.setBackgroundResource(R.drawable.button_filter_active)
                allEventsButton.setTextColor(resources.getColor(R.color.white, null))
            }
            "open" -> {
                autoOpenButton.setBackgroundResource(R.drawable.button_filter_active)
                autoOpenButton.setTextColor(resources.getColor(R.color.white, null))
            }
            "close" -> {
                autoCloseButton.setBackgroundResource(R.drawable.button_filter_active)
                autoCloseButton.setTextColor(resources.getColor(R.color.white, null))
            }
            "full" -> {
                binFullButton.setBackgroundResource(R.drawable.button_filter_active)
                binFullButton.setTextColor(resources.getColor(R.color.white, null))
            }
        }
    }

    override fun setupBinSpinner(bins: List<String>) {
        // Already handled by dialog
    }

    override fun showLoading() { /* TODO */ }

    override fun hideLoading() { /* TODO */ }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}

// Extension functions for smooth animations
fun View.expand(duration: Long = 300) {
    visibility = View.VISIBLE

    measure(
        View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    val targetHeight = measuredHeight

    layoutParams.height = 0

    val animator = ValueAnimator.ofInt(0, targetHeight)
    animator.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        requestLayout()
    }
    animator.duration = duration
    animator.start()
}

fun View.collapse(duration: Long = 300) {
    val initialHeight = measuredHeight

    val animator = ValueAnimator.ofInt(initialHeight, 0)
    animator.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        requestLayout()
    }
    animator.doOnEnd {
        visibility = View.GONE
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }
    animator.duration = duration
    animator.start()
}