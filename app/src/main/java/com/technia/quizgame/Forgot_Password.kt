package com.technia.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.technia.quizgame.databinding.ActivityForgotPasswordBinding
import com.technia.quizgame.databinding.ActivityMainBinding

class Forgot_Password : AppCompatActivity() {
    lateinit var forgotBinding: ActivityForgotPasswordBinding
    val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotBinding= ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view=forgotBinding.root
        setContentView(view)
        forgotBinding.reset.setOnClickListener {
            val userEmail=forgotBinding.EditTextForgotEmail.text.toString()
            auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    Toast.makeText(applicationContext,"We sent a password reset link to your Email Address ",Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                {
                    Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}