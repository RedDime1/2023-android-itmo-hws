package ru.ok.itmo.example

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlin.concurrent.thread
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var gapTime = 1000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btnThRun : Button = findViewById(R.id.startThRunBtn)
        val btnRxJava : Button = findViewById(R.id.startRxJavaBtn)
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
                while (progressBar.progress != progressBar.max) {
                    try {
                        Thread.sleep(gapTime)
                        runOnUiThread {
                            progressBar.progress++
                            textProgress.text = progressBar.progress.toString()
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                runOnUiThread {
                    btnReset.isEnabled = true
                }
            }
        }

        fun useRxJava() {
            btnThRun.isEnabled = false
            btnRxJava.isEnabled = false
            Observable.interval(gapTime, TimeUnit.MILLISECONDS).takeWhile{progressBar.progress != progressBar.max}
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    progressBar.progress++
                    textProgress.text = progressBar.progress.toString()
                    if (progressBar.progress == progressBar.max) {
                        btnReset.isEnabled = true
                    }
                }
        }

        btnThRun.setOnClickListener {
            useThAndRun()
        }

        btnRxJava.setOnClickListener {
            useRxJava()
        }

        btnReset.setOnClickListener {
            progressBar.progress = 0
            textProgress.text = progressBar.progress.toString()
            btnReset.isEnabled = false
            btnThRun.isEnabled = true
            btnRxJava.isEnabled = true
        }

    }

}