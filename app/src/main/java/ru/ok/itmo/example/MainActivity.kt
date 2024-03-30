package ru.ok.itmo.example

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btnCorStart : Button = findViewById(R.id.startCor)
        val btnFlowStart : Button = findViewById(R.id.startFlow)
        val btnReset : Button = findViewById(R.id.resetBtn)
        val btnAlarm : Button = findViewById(R.id.set_alarm)
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val textProgress : TextView = findViewById(R.id.progressToText)

        progressBar.min = 0
        progressBar.max = 100
        btnReset.isEnabled = false

        fun useCorotines() {
            var count = 0
            btnCorStart.isEnabled = false
            btnFlowStart.isEnabled = false
            MainScope().launch(Dispatchers.IO) {
                while (count < 100) { withContext(Dispatchers.Main) {
                        count++
                        progressBar.progress = count
                        textProgress.text = count.toString()
                    if (count == 100) {
                        btnReset.isEnabled = true
                    }
                }
                    delay(100)
                }
            }
        }

        fun useFlow() {
            var count = 0
            btnCorStart.isEnabled = false
            btnFlowStart.isEnabled = false
            MainScope().launch(Dispatchers.Main) {
                flow {
                    while (count < 100) {
                        delay(100)
                        count++
                        emit(count)
                    }
                }.flowOn(Dispatchers.IO).collect {
                    progressBar.progress = it
                    textProgress.text = it.toString()
                    if (it == 100) {
                        btnReset.isEnabled = true
                    }
                }
            }
        }

        btnCorStart.setOnClickListener {
            useCorotines()
        }

        btnFlowStart.setOnClickListener {
            useFlow()
        }

        btnReset.setOnClickListener {
            progressBar.progress = 0
            textProgress.text = progressBar.progress.toString()
            btnReset.isEnabled = false
            btnCorStart.isEnabled = true
            btnFlowStart.isEnabled = true
        }

        btnAlarm.setOnClickListener {
            val cal = Calendar.getInstance()
            val h = cal.get(Calendar.HOUR_OF_DAY)
            val min = cal.get(Calendar.MINUTE)
            val timePicker = TimePickerDialog(this,
                { _, selectedHour, selectedMinute ->
                    setAlarm(selectedHour, selectedMinute)
                },
                h, min, true)
            timePicker.show()
        }

    }

    private fun setAlarm(h: Int, min: Int) {
        val alarmTime = Calendar.getInstance()
        alarmTime[Calendar.HOUR_OF_DAY] = h
        alarmTime[Calendar.MINUTE] = min
        alarmTime[Calendar.SECOND] = 0
        AlarmWorker.constructAlarm(this, alarmTime.timeInMillis)
    }

}