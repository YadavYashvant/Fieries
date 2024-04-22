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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.yashvant.fieries.MainActivity
import com.yashvant.fieries.R
import com.yashvant.fieries.databinding.ActivityUserBinding
import com.yashvant.fieries.models.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

var taskList = mutableListOf<Task?>()

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

        GlobalScope.launch(Dispatchers.IO) {
            taskCollection.get().addOnSuccessListener { result ->
                val list = result.documents
                for (d in list) {
                    val u = d.toObject(Task::class.java)
                    taskList.add(u)
                }
                Log.d("UserActivity", "Tasks -> $taskList")
            }.addOnFailureListener {exception ->
                Log.w("UserActivity", "Error getting documents", exception)
                }

            withContext(Dispatchers.Main){

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