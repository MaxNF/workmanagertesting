package com.example.work_manager_testing

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkInfo
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var textView: TextView

   private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)
        textView.setOnClickListener {
            Log.d(TAG, "button clicked")
            mainViewModel.planWork(this)
            observeWork()
        }

    }

    private fun observeWork() {
       mainViewModel.getWorkInfosLiveData(this).observe(this) { workInfo ->
                if(workInfo?.all { it.state == WorkInfo.State.SUCCEEDED } == true) {
                    Toast.makeText(this, "Work complete", Toast.LENGTH_SHORT).show()
                }
            }
    }
}