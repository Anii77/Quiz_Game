package com.technia.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.technia.quizgame.databinding.ActivityMainBinding
import com.technia.quizgame.databinding.ActivityResultBinding

class Result_Activity : AppCompatActivity() {
    lateinit var resultBinding: ActivityResultBinding
    val database=FirebaseDatabase.getInstance()
    val databaseRefrence=database.reference.child("scores")
    val auth=FirebaseAuth.getInstance()
    val user=auth.currentUser
    var userCorrect=""
    var userWrong=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding= ActivityResultBinding.inflate(layoutInflater)
        val view=resultBinding.root
        setContentView(view)
        databaseRefrence.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
user?.let {
    val userId=it.uid
    userCorrect=snapshot.child(userId).child("correct").value.toString()
    userWrong=snapshot.child(userId).child("wrong").value.toString()
resultBinding.textViewCorrectScore.text=userCorrect
    resultBinding.textViewWrongScore.text=userWrong

}
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        resultBinding.buttonPlayAgain.setOnClickListener {
val intent= Intent(this,MainActivity::class.java)
           startActivity(intent)
           finish()
        }
        resultBinding.buttonExit.setOnClickListener {
finish()
        }
    }
}