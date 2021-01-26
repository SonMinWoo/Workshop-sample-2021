package page.chungjungsoo.to_dosample.todo

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.w3c.dom.Text
import page.chungjungsoo.to_dosample.R
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


class TodoListViewAdapter (context: Context, var resource: Int, var items: MutableList<Todo> ) : ArrayAdapter<Todo>(context, resource, items){
    private lateinit var db: TodoDatabaseHelper

    override fun getView(position: Int, convertView: View?, p2: ViewGroup): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = layoutInflater.inflate(resource , null )
        val title : TextView = view.findViewById(R.id.listTitle)
        val description : TextView = view.findViewById(R.id.listDesciption)
        val edit : Button = view.findViewById(R.id.editBtn)
        val delete : Button = view.findViewById(R.id.delBtn)
        val finish : TextView = view.findViewById(R.id.finished)
        val duedate : TextView = view.findViewById(R.id.dueDate)
//        val duetime : TextView = view.findViewById(R.id.dueTime)

        db = TodoDatabaseHelper(this.context)

        // Get to-do item
        var todo = items[position]

        // Load title and description to single ListView item
        title.text = todo.title
        description.text = todo.description
        if(todo.finished) {
            finish.text = "Done!"
        } else {
            finish.text = "Not finished.."
        }
        duedate.text = convertLongToTime(todo.date)
//        duetime.text = todo.times.toString()

        // OnClick Listener for edit button on every ListView items
        edit.setOnClickListener {
            // Very similar to the code in MainActivity.kt
            val builder = AlertDialog.Builder(this.context)
            val dialogView = layoutInflater.inflate(R.layout.add_todo_dialog, null)
            val titleToAdd = dialogView.findViewById<EditText>(R.id.todoTitle)
            val desciptionToAdd = dialogView.findViewById<EditText>(R.id.todoDescription)
            val ime = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val radioBtn1 = dialogView.findViewById<RadioButton>(R.id.isFinished)
            val radioBtn2 = dialogView.findViewById<RadioButton>(R.id.notFinished)
            val dateToAdd = dialogView.findViewById<CalendarView>(R.id.todoDuedate)
            val timeToAdd = dialogView.findViewById<TimePicker>(R.id.toDotime)
            var timeval = 0

            titleToAdd.setText(todo.title)
            desciptionToAdd.setText(todo.description)
            if(todo.finished) {
                radioBtn1.isChecked = true
            } else {
                radioBtn2.isChecked = true
            }
            dateToAdd.setOnDateChangeListener { view, year, month, dayOfMonth ->
                // set the calendar date as calendar view selected date
                val calendar = Calendar.getInstance()
                calendar.set(year,month,dayOfMonth)

                // set this date as calendar view selected date
                dateToAdd.date = calendar.timeInMillis
            }
            dateToAdd.date = todo.date
            timeToAdd.setOnTimeChangedListener(TimePicker.OnTimeChangedListener { timePicker, hour, minute ->
                timeval = hour
            })

            titleToAdd.requestFocus()
            ime.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

            builder.setView(dialogView)
                .setPositiveButton("수정") { _, _ ->
                    val tmp = Todo(
                        titleToAdd.text.toString(),
                        desciptionToAdd.text.toString(),
                        radioBtn1.isChecked,
                        dateToAdd.date,
                        timeval.toLong()
                    )

                    val result = db.updateTodo(tmp, position)
                    if (result) {
                        todo.title = titleToAdd.text.toString()
                        todo.description = desciptionToAdd.text.toString()
                        todo.finished = radioBtn1.isChecked
                        todo.date = dateToAdd.date
                        todo.times = timeval.toLong()
                        notifyDataSetChanged()
                        ime.hideSoftInputFromWindow(titleToAdd.windowToken, 0)
                    }
                    else {
                        Toast.makeText(this.context, "수정 실패! :(", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton("취소") {_, _ ->
                    // Cancel Btn. Do nothing. Close keyboard.
                    ime.hideSoftInputFromWindow(titleToAdd.windowToken, 0)
                }
                .show()
        }

        // OnClick Listener for X(delete) button on every ListView items
        delete.setOnClickListener {
            val result = db.delTodo(position)
            if (result) {
                items.removeAt(position)
                notifyDataSetChanged()
            }
            else {
                Toast.makeText(this.context, "삭제 실패! :(", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }
        }


        return view
    }
    fun convertLongToTime (time: Long): String {
        try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(time)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

}