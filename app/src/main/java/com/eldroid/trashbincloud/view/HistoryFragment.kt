package com.eldroid.trashbincloud.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.HistoryContract
import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.model.entity.EventType
import com.eldroid.trashbincloud.presenter.HistoryPresenter
import java.time.LocalDate
import java.time.LocalTime

class HistoryFragment : Fragment(R.layout.fragment_history), HistoryContract.View {

    private lateinit var presenter: HistoryPresenter
    private lateinit var filterButton: ImageView
    private lateinit var allEventsButton: TextView
    private lateinit var autoOpenButton: TextView
    private lateinit var manualOpenButton: TextView
//    private lateinit var binSpinner: Spinner
    private lateinit var dateEditText: EditText
    //private lateinit var weeklyChart: BarChart
    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var activityAdapter: HistoryAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
//        initViews(view)
//        setupRecyclerView()
//        setupPresenter()
//        //setupChart()
//        setupListeners()
        return view
    }

//    class HistoryFragment : Fragment(R.layout.fragment_history) {

//        private lateinit var recyclerView: RecyclerView
//        private lateinit var adapter: HistoryAdapter

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            initViews(view)
            setupRecyclerView()
            setupPresenter()
            //setupChart()
            setupListeners()

//            recyclerView = view.findViewById(R.id.activityRecyclerView)
//            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            //sample data only
            val sampleActivities = mutableListOf(
                ActivityEvent(
                    type = EventType.AUTO_OPEN,
                    iconResource = R.drawable.ic_auto_open,
                    time = LocalTime.of(14, 34),
                    detail = "Motion detected",
                    description = "Kitchen Bin opened automatically",
                    title = "Auto Open",
                    date = LocalDate.now()
                ),
                ActivityEvent(
                    type = EventType.MANUAL_OPEN,
                    iconResource = R.drawable.ic_manual_open,
                    time = LocalTime.of(15, 10),
                    detail = "Button pressed",
                    description = "Kitchen Bin opened manually",
                    title = "Manual Open",
                    date = LocalDate.now()
                ),
                ActivityEvent(
                    type = EventType.BIN_FULL,
                    iconResource = R.drawable.ic_bin_full,
                    time = LocalTime.of(16, 20),
                    detail = "Bin is full",
                    description = "Kitchen Bin needs emptying",
                    title = "Bin Full Alert",
                    date = LocalDate.now()
                ),
                ActivityEvent(
                    type = EventType.HOLD_MODE,
                    iconResource = R.drawable.ic_hold_mode,
                    time = LocalTime.of(17, 45),
                    detail = "Hold mode active",
                    description = "Bin will not open automatically",
                    title = "Hold Mode",
                    date = LocalDate.now()
                )
            )
            activityAdapter = HistoryAdapter(sampleActivities)
            activityRecyclerView.adapter = activityAdapter
        }
//    }


    private fun initViews(view: View) {
        filterButton = view.findViewById(R.id.filterButton)
        allEventsButton = view.findViewById(R.id.allEventsButton)
        autoOpenButton = view.findViewById(R.id.autoOpenButton)
        manualOpenButton = view.findViewById(R.id.manualOpenButton)
        //ako sani gi comment ang spinner kay di niya ma detect sa history fragment kay wala daw sa xml.
        //wako kahibaw asa e butang ang spinner
//        binSpinner = view.findViewById(R.id.binSpinner)
        dateEditText = view.findViewById(R.id.dateEditText)
        //weeklyChart = view.findViewById(R.id.weeklyChart)
        activityRecyclerView = view.findViewById(R.id.activityRecyclerView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPresenter() {
        presenter = HistoryPresenter()
        presenter.attachView(this)
    }

    //for the bar chart ni siya
    private fun setupChart() {
//        weeklyChart.apply {
//            description.isEnabled = false
//            legend.isEnabled = false
//            setTouchEnabled(false)
//            isDragEnabled = false
//            setScaleEnabled(false)
//            axisRight.isEnabled = false
//
//            axisLeft.apply {
//                setDrawGridLines(false)
//                axisMinimum = 0f
//                axisMaximum = 20f
//                setLabelCount(3, true)
//                textColor = resources.getColor(R.color.text_secondary, null)
//            }
//
//            xAxis.apply {
//                position = XAxis.XAxisPosition.BOTTOM
//                setDrawGridLines(false)
//                textColor = resources.getColor(R.color.text_secondary, null)
//                valueFormatter = IndexAxisValueFormatter(
//                    arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
//                )
//            }
//        }
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
        filterButton.setOnClickListener { presenter.onFilterClicked() }
        allEventsButton.setOnClickListener { presenter.onEventTypeSelected("all") }
        autoOpenButton.setOnClickListener { presenter.onEventTypeSelected("auto") }
        manualOpenButton.setOnClickListener { presenter.onEventTypeSelected("manual") }

//        binSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selectedBin = parent?.getItemAtPosition(position) as String
//                presenter.onBinSelected(selectedBin)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
    }

    //for the bar chart ni siya
    override fun showWeeklyData(data: List<Int>) {
//        val entries = data.mapIndexed { index, value ->
//            BarEntry(index.toFloat(), value.toFloat())
//        }
//
//        val dataSet = BarDataSet(entries, "Weekly Activity").apply {
//            color = resources.getColor(R.color.primary_teal, null)
//            setDrawValues(false)
//        }
//
//        weeklyChart.data = BarData(dataSet).apply {
//            barWidth = 0.6f
//        }
//        weeklyChart.invalidate()
    }

    override fun showActivityList(activities: List<ActivityEvent>) {
        activityAdapter.updateActivities(activities)
    }

    override fun updateFilterButtons(selectedFilter: String) {
        // TODO: same code as before to highlight buttons
    }

    //Ako sa gi comment kay mao ni naka cause og error nga di ma acccess ni nga fragment
    override fun setupBinSpinner(bins: List<String>) {
//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bins)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binSpinner.adapter = adapter
    }

    override fun showLoading() { /* TODO */ }

    override fun hideLoading() { /* TODO */ }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun setTouchEnabled(bool: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
