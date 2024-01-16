package com.technia.quizgame

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.technia.quizgame.databinding.ActivityQuizBinding
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    private lateinit var quizBinding: ActivityQuizBinding
    private val database = FirebaseDatabase.getInstance()
    private val databaseReference = database.reference.child("questions")
    private var questions = ""
    private var a = ""
    private var b = ""
    private var c = ""
    private var d = ""
    private var correctAnswer = ""
    private var questionNumber = 0
    private var userAnswer = ""
    private var userCorrect = 0
    private var userWrong = 0
    private lateinit var timer: CountDownTimer
    private val totalTime = 25000L
    private var timeContinue = false
    private var leftTime = totalTime
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val scoreRef = database.reference.child("scores")
    private val questionSet = mutableSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(quizBinding.root)

        initializeQuestionSet()

        quizBinding.buttonNext.setOnClickListener {
            resetTimer()
            gameLogic()
        }

        quizBinding.buttonFinish.setOnClickListener {
            showResultDialog()
        }

        setOptionClickListener(quizBinding.textViewA, "a")
        setOptionClickListener(quizBinding.textViewB, "b")
        setOptionClickListener(quizBinding.textViewC, "c")
        setOptionClickListener(quizBinding.textViewD, "d")

        gameLogic()
    }

    private fun initializeQuestionSet() {
        do {
            val number = Random.nextInt(0, 37)
            questionSet.add(number)
        } while (questionSet.size < 20)
    }

    private fun setOptionClickListener(view: View, option: String) {
        view.setOnClickListener {
            pauseTimer()
            userAnswer = option

            if (correctAnswer == userAnswer) {
                view.setBackgroundColor(Color.GREEN)
                userCorrect++
                quizBinding.textViewCorrect.text = userCorrect.toString()
            } else {
                view.setBackgroundColor(Color.RED)
                userWrong++
                quizBinding.textViewWrong.text = userWrong.toString()
                findAnswer()
            }

            disableClickableOptions()
        }
    }

    private fun gameLogic() {
        restoreOptions()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (questionNumber < questionSet.size) {
                    val questionData = snapshot.child(questionSet.elementAt(questionNumber).toString())
                    questions = questionData.child("q").value.toString()
                    a = questionData.child("a").value.toString()
                    b = questionData.child("b").value.toString()
                    c = questionData.child("c").value.toString()
                    d = questionData.child("d").value.toString()
                    correctAnswer = questionData.child("answer").value.toString()

                    quizBinding.textViewQuestion.text = questions
                    quizBinding.textViewA.text = a
                    quizBinding.textViewB.text = b
                    quizBinding.textViewC.text = c
                    quizBinding.textViewD.text = d

                    quizBinding.progressBar.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQuestions.visibility = View.VISIBLE
                    quizBinding.linearLayoutButton.visibility = View.VISIBLE

                    startTimer()
                } else {
                    showResultDialog()
                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showResultDialog() {
        val dialogMessage = AlertDialog.Builder(this@QuizActivity)
        dialogMessage.setTitle("Quiz Game")
        dialogMessage.setMessage("Congratulations!! You have answered all the questions. Do you want to see the result?")
        dialogMessage.setCancelable(false)
        dialogMessage.setPositiveButton("See Result") { _, _ ->
            sendScore()
        }
        dialogMessage.setNegativeButton("Play Again") { _, _ ->
            val intent = Intent(this@QuizActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        dialogMessage.create().show()
    }

    private fun findAnswer() {
        when (correctAnswer) {
            "a" -> quizBinding.textViewA.setBackgroundColor(Color.GREEN)
            "b" -> quizBinding.textViewB.setBackgroundColor(Color.GREEN)
            "c" -> quizBinding.textViewC.setBackgroundColor(Color.GREEN)
            "d" -> quizBinding.textViewD.setBackgroundColor(Color.GREEN)
        }
    }

    private fun disableClickableOptions() {
        quizBinding.textViewA.isClickable = false
        quizBinding.textViewB.isClickable = false
        quizBinding.textViewC.isClickable = false
        quizBinding.textViewD.isClickable = false
    }

    private fun restoreOptions() {
        quizBinding.textViewA.setBackgroundColor(Color.WHITE)
        quizBinding.textViewB.setBackgroundColor(Color.WHITE)
        quizBinding.textViewC.setBackgroundColor(Color.WHITE)
        quizBinding.textViewD.setBackgroundColor(Color.WHITE)

        quizBinding.textViewA.isClickable = true
        quizBinding.textViewB.isClickable = true
        quizBinding.textViewC.isClickable = true
        quizBinding.textViewD.isClickable = true
    }

    private fun startTimer() {
        timer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                disableClickableOptions()
                resetTimer()
                updateCountDownText()
                quizBinding.textViewQuestion.text = "Sorry, Time is up! Continue with the next question"
                timeContinue = false
            }
        }.start()
        timeContinue = true
    }

    private fun updateCountDownText() {
        val remainingTime: Int = (leftTime / 1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()
    }

    private fun pauseTimer() {
        timer.cancel()
        timeContinue = false
    }

    private fun resetTimer() {
        pauseTimer()
        leftTime = totalTime
        updateCountDownText()
    }

    private fun sendScore() {
        user?.let {
            val userUID = it.uid
            scoreRef.child(userUID).child("correct").setValue(userCorrect)
            scoreRef.child(userUID).child("wrong").setValue(userWrong)
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Score sent to Database Successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@QuizActivity, Result_Activity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }
}
