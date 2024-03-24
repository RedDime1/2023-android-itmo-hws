package ru.ok.itmo.example

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var gapTime = 1000L
    private var running = false //чтобы reset могло остановить
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btnThRun : Button = findViewById(R.id.startThRunBtn)
        val btnRxJava : Button = findViewById(R.id.startThRunBtn)
        val btnReset : Button = findViewById(R.id.resetBtn)
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val radioGroup : RadioGroup = findViewById(R.id.chooserRadio)
        val textProgress : TextView = findViewById(R.id.progressToText)

        progressBar.min = 0
        progressBar.max = 100
        btnReset.isEnabled = false

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val rb : RadioButton = findViewById(checkedId)
            gapTime = rb.text.toString().replace(" ms", "").toLong()
        }
        radioGroup.check(R.id.radioBtn100)

        fun useThAndRun() {
            btnThRun.isEnabled = false
            btnRxJava.isEnabled = false
            thread(true) {
                while (running) {
                    try {
                        Thread.sleep(gapTime)
                        runOnUiThread {
                            progressBar.progress++
                            textProgress.text = progressBar.progress.toString()
                        }
                        if (progressBar.progress == progressBar.max) running = true
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                runOnUiThread {
                    btnThRun.isEnabled = true
                    btnRxJava.isEnabled = true
                }
            }
        }

        btnThRun.setOnClickListener {
            running = true
            useThAndRun()
        }

    }

}