    package com.technia.quizgame

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.technia.quizgame.databinding.ActivityLoginBinding
import javax.xml.transform.Result

    class Login_Activity : AppCompatActivity() {
    lateinit var loginBinding: ActivityLoginBinding
    lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    val auth=FirebaseAuth.getInstance()
         lateinit var googleSignInClient: GoogleSignInClient

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding= ActivityLoginBinding.inflate(layoutInflater)
        val view=loginBinding.root
        setContentView(view)
        val textofgooglebutton=loginBinding.buttonGoogleSignIn.getChildAt(0)as TextView
        textofgooglebutton.text="Continue with google"
        textofgooglebutton.setTextColor(Color.BLACK)
        textofgooglebutton.textSize=18F
        registerActivityForGoogleSignIn()
        loginBinding.SignIn.setOnClickListener{
val userEmail=loginBinding.EditTextLoginEmail.text.toString()
            val userPassword=loginBinding.EditTextLoginPassword.text.toString()
            signInUser(userEmail,userPassword   )
        }
        loginBinding.buttonGoogleSignIn.setOnClickListener {

            siginInGoogle()
        }
        loginBinding.buttonSignUp.setOnClickListener {
val intent=Intent(this,Sign_Up::class.java)
            startActivity(intent)
        }
        loginBinding.ForgotPassword.setOnClickListener {
val intent=Intent(this,Forgot_Password::class.java)
            startActivity(intent)
        }
    }
        fun signInUser(userEmail:String,userPassword:String)
        {
            auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener { task->
                if(task.isSuccessful)
                {
                    Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_SHORT).show()
                    val intent=Intent(this@Login_Activity,MainActivity::class.java)
                    startActivity(intent)
                    finish()


                }
                else
                {
Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }
        }
        override fun onStart() {
            super.onStart()

            val user=auth.currentUser
            if(user!=null)
            {
                Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_SHORT).show()
                val intent=Intent(this@Login_Activity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        private fun siginInGoogle()
        {
            val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("887058225285-cmckbchbegi3k420fqimevd9fqs8ds9q.apps.googleusercontent.com")
                .requestEmail().build()
            googleSignInClient= GoogleSignIn.getClient(this,gso)
            signIn()
        }
        private fun signIn()
        {
            val signInIntent = googleSignInClient.signInIntent

            activityResultLauncher.launch(signInIntent)
        }
        private fun registerActivityForGoogleSignIn()
        {
activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
    ActivityResultCallback {result ->
        val resultCode=result.resultCode
        val data=result.data
        if(resultCode== RESULT_OK && data!=null)
        {
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            firebaseSignInWithGoogle(task)
        }
    })
        }

        private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
try {
val account:GoogleSignInAccount=task.getResult(ApiException::class.java)
    Toast.makeText(applicationContext,"Welcome to Quiz Game",Toast.LENGTH_SHORT).show()
    val intent  =Intent(this,MainActivity::class.java)
    startActivity(intent)
    firebaseGoogleAccount(account)
}
catch (e:ApiException)
{
    Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_SHORT).show()
}
        }
        private fun firebaseGoogleAccount(account: GoogleSignInAccount)
        {
            val authCredential=GoogleAuthProvider.getCredential(account.idToken,null)
            auth.signInWithCredential(authCredential).addOnCompleteListener { task->
                if(task.isSuccessful)
                {

                }
                else
                {

                }
            }
        }
    }