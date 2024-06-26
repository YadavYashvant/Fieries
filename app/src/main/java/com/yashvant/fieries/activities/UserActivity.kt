package com.yashvant.fieries.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.yashvant.fieries.MainActivity
import com.yashvant.fieries.R
import com.yashvant.fieries.adapters.TodoAdapter
import com.yashvant.fieries.databinding.ActivityUserBinding
import com.yashvant.fieries.models.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.greetingText.text = "WELCOME BACK"

        binding.signOutBtn.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            Toast.makeText(this, "Signed Out!!", Toast.LENGTH_SHORT).show()
        }

        binding.addtaskbtn.setOnClickListener {
            val todo = binding.taskEt.text.toString()
            val todoDate = binding.taskDateEt.text.toString()
            val isDone = false           // FOR NOW
            addToFirebase(todo, todoDate, isDone, this)
        }


        val taskdb: FirebaseFirestore = FirebaseFirestore.getInstance()
        val taskCollection = taskdb.collection("tasks")

        lifecycleScope.launch {
            try {
                val result = taskCollection.get().await()
                val taskList = mutableListOf<Task?>()

                for (document in result.documents) {
                    val task = document.toObject(Task::class.java)
                    task?.let { taskList.add(it) }
                }

                Log.d("UserActivity", "Tasks -> $taskList")

                // Set RecyclerView adapter after fetching data successfully
                binding.recyclerView.adapter = TodoAdapter(taskList)
                binding.recyclerView.layoutManager = LinearLayoutManager(this@UserActivity)
                binding.recyclerView.setHasFixedSize(true)
            } catch (e: Exception) {
                Log.e("UserActivity", "Error getting documents", e)
            }
        }

    }
}


fun addToFirebase(
    todo: String,
    date: String,
    isDone: Boolean,
    context: Context
) {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val dbTasks: CollectionReference = db.collection("tasks")
    val todos = Task(todo, date, isDone)

    if(todo.isNotEmpty() && date.isNotEmpty()){
        dbTasks.add(todos)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    context,
                    "Task added successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Error adding task",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }else{
        Toast.makeText(
            context,
            "Please fill all fields",
            Toast.LENGTH_SHORT
        ).show()
    }

}