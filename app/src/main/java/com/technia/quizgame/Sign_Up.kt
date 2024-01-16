package com.technia.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.technia.quizgame.databinding.ActivitySignUpBinding

class Sign_Up : AppCompatActivity() {
    lateinit var signUpBinding: ActivitySignUpBinding
    val auth:FirebaseAuth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding= ActivitySignUpBinding.inflate(layoutInflater)
        val view=signUpBinding.root
        setContentView(view)
        signUpBinding.signup.setOnClickListener {
val email=signUpBinding.EditTextSignUpEmail.text.toString()
            val password=signUpBinding.EditTextSignUpPassword.text.toString()
            signupWithFirebase(email, password)
        }
    }
    fun signupWithFirebase(email:String,password:String)
    {
signUpBinding.progressBar.visibility=View.VISIBLE
        signUpBinding.signup.isClickable=false
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful)
            {
                Toast.makeText(applicationContext,"Your account has been created",Toast.LENGTH_SHORT).show()
                finish()
                signUpBinding.progressBar.visibility=View.INVISIBLE
                signUpBinding.signup.isClickable=true


            }
            else
            {
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }
}