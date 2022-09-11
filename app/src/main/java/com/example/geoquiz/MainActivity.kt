package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlin.math.round
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.geoquiz.databinding.ActivityMainBinding
import kotlin.math.roundToInt

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var answerIsTrue = false
    private val quizViewModel : QuizViewModel by viewModels()
    var answeredRight = 0
    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {result ->
        if(result.resultCode == Activity.RESULT_OK){
            quizViewModel.isCheater =
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)?: false
                result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)?: false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        binding.trueButton.setOnClickListener{ view: View ->
            checkAnswer(true)
            quizViewModel.setHasBeenAnswered()
        }

        binding.falseButton.setOnClickListener{ view: View ->
            checkAnswer(false)
            quizViewModel.setHasBeenAnswered()
        }

        binding.previousButton.setOnClickListener {
            quizViewModel.movetoPrev()
            updateQuestion()
        }

        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()

            updateQuestion()
        }

        binding.cheatButton.setOnClickListener{
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivity(intent)
            cheatLauncher.launch(intent)
        }

        updateQuestion()
    }

    override fun onStart(){
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop(){
        super.onStop()
        Log.d(TAG,"onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy() called")
    }

    private fun updateQuestion(){
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean){

        //If question has been previously answered, show toast and return.
        if(quizViewModel.currentHasBeenAnswered == true){
            Toast.makeText(this, R.string.has_been_answered, Toast.LENGTH_SHORT)
                .show()
            return
        }
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when{
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        //Increment score if  answer is right
        if(userAnswer == correctAnswer){
            ++answeredRight
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show()

        gradedDisplay()
    }

    //Challenge 3 Check if all questions are answered and if so. display score
    private fun gradedDisplay(){
        if(quizViewModel.totalAnswered == (quizViewModel.questionBankSize - 1)){
            val score = answeredRight.toDouble()/quizViewModel.questionBankSize
            Toast.makeText(this,String.format("Your score is: %.2f" , score*100) + "%", Toast.LENGTH_LONG)
                .show()
        }
    }

}