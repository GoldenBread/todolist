package eu.epitech.levisse.thierry.todolist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.task_row.view.*
import android.support.v7.widget.RecyclerView.ViewHolder
import android.Manifest.permission_group.SMS
import android.widget.TextView
import android.Manifest.permission_group.SMS
import android.net.Uri
import android.widget.ImageView
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue


/**
 * Created by thierry on 31/01/18.
 */

class TaskAdapter(private val items: MutableList<Task>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view: View? = null
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.task_row, parent, false) //View.inflate(parent.context, R.layout.task_row, parent)
                viewHolder = TaskViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.header_row, parent, false) //View.inflate(parent.context, R.layout.header_row, parent)
                viewHolder = HeaderViewHolder(view)
            }
        }

        return viewHolder!!


        //return VH(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.bind(items[position])
        val viewType: Int = holder.itemViewType
        val task: Task = items[position]
        when (viewType) {
            0 -> {
                (holder as TaskViewHolder).showTaskDetails(task)
            }
            1 -> {
                (holder as HeaderViewHolder).showHeaderDetails(task)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (items.get(position).frequence == -1)
            return 1
        return 0
    }

    override fun getItemCount(): Int = items.size

    fun addItem(task: Task) {
        items.add(task)
        notifyItemInserted(items.size)
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeAll() {
        while (items.size > 0)
            removeAt(0)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title_text: TextView = itemView.findViewById(R.id.title_text) as TextView
        private val description_text: TextView = itemView.findViewById(R.id.description_text) as TextView
        private val remaining_time: TextView = itemView.findViewById(R.id.remaining_time) as TextView
        private val imageView: ImageView = itemView.findViewById(R.id.imageView) as ImageView


        fun showTaskDetails(task: Task) {
            title_text.text = task.title
            description_text.text = task.description
            if (task.deadlineDate.after(Date(10)))
                remaining_time.text = remainingTimeText(task.deadlineDate)
            else
                remaining_time.text = ""
            imageView.setImageURI(Uri.parse(task.imageLocation))
        }

        fun remainingTimeText(deadline: Date): String {
            val currentDate: Date = Calendar.getInstance().time

            var timeDiff = currentDate.time - deadline.time

            var hours = TimeUnit.MILLISECONDS.toHours(timeDiff)

            var minusPlus = '-'
            if (hours > 0)
                minusPlus = '+'

            hours = hours.absoluteValue
            timeDiff = timeDiff.absoluteValue

            if (hours > 24 * 365)
                return "Y${minusPlus}${TimeUnit.MILLISECONDS.toDays(timeDiff) / 365}"
            else if (hours > 24)
                return "D${minusPlus}${TimeUnit.MILLISECONDS.toDays(timeDiff)}"
            return "H${minusPlus}${hours}"
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val header_title: TextView = itemView.findViewById(R.id.header_title) as TextView


        fun showHeaderDetails(task: Task) {
            header_title.text = task.title
        }
    }
}