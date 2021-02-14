package com.example.vm

/**
 * Extension functions and Binding Adapters.
 */

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.example.vm.models.Status
import com.example.vm.models.Status.NONE
import com.example.vm.models.Status.PENDING
import com.example.vm.models.getMathOperator
import com.example.vm.ui.Event
import com.example.vm.ui.MainViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */
fun View.showSnackbar(snackbarText: String, timeLength: Int) {
    Snackbar.make(this, snackbarText, timeLength).run {
        show()
    }
}


/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackBar(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<Int>>,
    timeLength: Int
) {

    snackbarEvent.observe(lifecycleOwner, { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(context.getString(it), timeLength)
        }
    })
}


fun Activity.hideKeyboard(){
    val imm: InputMethodManager =
        getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager

    var view: View? = currentFocus
    if (view == null)
        view = View(this)

    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

@BindingAdapter("status")
fun handleStatus(textView: TextView, status: Status) {
    when (status) {
        NONE -> textView.text = textView.context.getString(R.string.status_all_done)
        PENDING -> textView.text = PENDING.toString()
    }
}

@BindingAdapter("setupWithMainViewModel")
fun setSpinnerEntries(spinner: Spinner, viewModel: MainViewModel) {
    ArrayAdapter.createFromResource(
        spinner.context,
        R.array.operators_array,
        android.R.layout.simple_spinner_item
    ).also { adapter ->
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.mathOperator.value = getMathOperator(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //Ignore
            }
        }
        spinner.setSelection(0)
    }
}


