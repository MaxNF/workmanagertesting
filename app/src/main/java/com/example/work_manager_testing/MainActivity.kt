package com.example.work_manager_testing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData(MainViewModel.WORK_UNIQUE_NAME)
            .observe(this) { workInfo ->
                if(workInfo?.all { it.state == WorkInfo.State.SUCCEEDED } == true) {
                    Toast.makeText(this, "Work complete", Toast.LENGTH_SHORT).show()
                }
            }
    }
}