package com.example.travelapp.ui



import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.travelapp.R
import com.example.travelapp.ui.fragments.DatePickerFragment
import java.text.DateFormat
import java.util.Calendar


class NewPlanActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private var textView: TextView? = null
    var currentDayString: String? = null
    var dateTextView: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_plan)

        var startTextView: TextView = findViewById(R.id.date_start)
        startTextView.setOnClickListener {
            dateTextView = R.id.date_start
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "date picker")

        }

        var endTextView: TextView = findViewById(R.id.date_end)
        endTextView.setOnClickListener {
            dateTextView = R.id.date_end
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "date picker")

        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.YEAR, p1)
        c.set(Calendar.MONTH, p2)
        c.set(Calendar.DAY_OF_MONTH, p3)
        currentDayString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime())

        textView = findViewById(dateTextView)
        textView?.setText(currentDayString)


    }
}