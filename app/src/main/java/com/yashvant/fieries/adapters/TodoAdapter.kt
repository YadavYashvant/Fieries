package com.yashvant.fieries.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yashvant.fieries.R
import com.yashvant.fieries.models.Task

class TodoAdapter(private val todoList: MutableList<Task?>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTextView: TextView = itemView.findViewById(R.id.todoTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val isDoneCheckBox: CheckBox = itemView.findViewById(R.id.isDoneCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TodoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentItem = todoList[position]
        if (currentItem != null) {
            holder.todoTextView.text = currentItem.todo
        }
        if (currentItem != null) {
            holder.dateTextView.text = currentItem.date
        }
        if (currentItem != null) {
            holder.isDoneCheckBox.isChecked = currentItem.isDone
        }
    }

    override fun getItemCount() = todoList.size
}
