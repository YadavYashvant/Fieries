package com.yashvant.fieries.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.yashvant.fieries.R
import com.yashvant.fieries.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val passwd = binding.passET.text.toString()

            if(email.isNotEmpty() && passwd.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(this, UserActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "Please fill empty fields!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}