package com.example.geoquiz


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewTreeLifecycleOwner.set
import org.junit.Assert.assertEquals
import org.junit.Test


const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle): ViewModel(){
    @Test
    fun providesExpectedQuestionText(){
        val savedStateHandle = SavedStateHandle()
        val quizViewModel = QuizViewModel(savedStateHandle)
        assertEquals(R.string.question_australia, quizViewModel.currentQuestionText)
    }

    @Test
    fun wrapsAroundQuestionBank(){
        val savedStateHandle = SavedStateHandle(mapOf(CURRENT_INDEX_KEY to 5))
        val quizViewModel = QuizViewModel(savedStateHandle)
        assertEquals(R.string.question_asia, quizViewModel.currentQuestionText)
        quizViewModel.moveToNext()
        assertEquals(R.string.question_australia, quizViewModel.currentQuestionText)
    }

    private val questionBank = listOf(
        Question(R.string.question_australia,true,false),
        Question(R.string.question_oceans,true,false),
        Question(R.string.question_mideast,false,false),
        Question(R.string.question_africa,false,false),
        Question(R.string.question_americas,true,false),
        Question(R.string.question_asia, true,false)
    )

    var isCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY) ?: false
        set(value) = savedStateHandle.set(IS_CHEATER_KEY, value)


    private var currentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY) ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer


    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext(){
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun movetoPrev() {
        if(currentIndex == 0){
            //If on the first question loop to the last index in the question list.
            currentIndex = questionBank.size - 1
        }else {
            currentIndex = (currentIndex - 1) % questionBank.size
        }
    }

    //Challenge 2. Once the function is called, the question will be marked as answered.
    fun setHasBeenAnswered(){
        questionBank[currentIndex].hasBeenAnswered = true
    }

    val currentHasBeenAnswered: Boolean
        get() = questionBank[currentIndex].hasBeenAnswered

    //Checks to see if question has already been answered.
    fun checkMultiAnswer() : Boolean{
        if(questionBank[currentIndex].hasBeenAnswered == true){
            return true
        }
        return false
    }
}