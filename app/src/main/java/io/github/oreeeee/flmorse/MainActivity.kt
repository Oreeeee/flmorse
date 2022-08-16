package io.github.oreeeee.flmorse

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.delay
import java.lang.Exception
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String

    private lateinit var etUserText: EditText
    private lateinit var btnFlash: Button
    private lateinit var sbRepeatCount: SeekBar
    private lateinit var tvRepeatCount: TextView
    private lateinit var tvRepeatedTimes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUserText = findViewById(R.id.etUserText)
        btnFlash = findViewById(R.id.btnFlash)
        sbRepeatCount = findViewById(R.id.sbRepeatCount)
        tvRepeatCount = findViewById(R.id.tvRepeatCount)
        tvRepeatedTimes = findViewById(R.id.tvRepeatedTimes)

        // Initialize flashlight
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        tvRepeatCount.text = "1"

        var repeatCountValue: Int
        sbRepeatCount.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                repeatCountValue = seekBar.progress + 1
                tvRepeatCount.text = "$repeatCountValue"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        btnFlash.setOnClickListener {
            try {
                flashMorse(convertTextToMorse(etUserText.text.toString()), sbRepeatCount.progress + 1)
            } catch(e: IllegalArgumentException) {
                noFlashlightDialog()
            }
        }
    }

    private fun convertTextToMorse(textToConvert: String): String {
        var stringInMorse: String = ""

        textToConvert.uppercase().forEach { character ->
            // Long when statement incoming!
            when(character) {
                // Letters
                'A' -> stringInMorse += ".- "
                'B' -> stringInMorse += "-... "
                'C' -> stringInMorse += "-.-. "
                'D' -> stringInMorse += "-.. "
                'E' -> stringInMorse += ". "
                'F' -> stringInMorse += "..-. "
                'G' -> stringInMorse += "--. "
                'H' -> stringInMorse += ".... "
                'I' -> stringInMorse += ".. "
                'J' -> stringInMorse += ".--- "
                'K' -> stringInMorse += "-.- "
                'L' -> stringInMorse += ".-.. "
                'M' -> stringInMorse += "-- "
                'N' -> stringInMorse += "-. "
                'O' -> stringInMorse += "--- "
                'P' -> stringInMorse += ".--. "
                'Q' -> stringInMorse += "--.- "
                'R' -> stringInMorse += ".-. "
                'S' -> stringInMorse += "... "
                'T' -> stringInMorse += "- "
                'U' -> stringInMorse += "..- "
                'V' -> stringInMorse += "...- "
                'W' -> stringInMorse += ".-- "
                'X' -> stringInMorse += "-..- "
                'Y' -> stringInMorse += "-.-- "
                'Z' -> stringInMorse += "--.. "

                // Numbers
                '1' -> stringInMorse += ".---- "
                '2' -> stringInMorse += "..--- "
                '3' -> stringInMorse += "...-- "
                '4' -> stringInMorse += "....- "
                '5' -> stringInMorse += "..... "
                '6' -> stringInMorse += "-.... "
                '7' -> stringInMorse += "--... "
                '8' -> stringInMorse += "---.. "
                '9' -> stringInMorse += "----. "
                '0' -> stringInMorse += "----- "

                ' ' -> stringInMorse += "/"

                else -> Log.i("MainActivity", "Unsupported character given, skipping... TODO: Replace this with some kind of popup.")
            }
        }

        Log.i("MainActivity", "Morse: $stringInMorse")

        return stringInMorse
    }

    private fun flashMorse(stringToMorse: String, timesToRepeat: Int) {
        for(i in 1..timesToRepeat) {
            stringToMorse.forEach { character ->
                when(character) {
                    '.' -> {
                        cameraManager.setTorchMode(cameraId, true)
                        SystemClock.sleep(200)
                        cameraManager.setTorchMode(cameraId, false)
                        SystemClock.sleep(200)
                    }
                    '-' -> {
                        cameraManager.setTorchMode(cameraId, true)
                        SystemClock.sleep(600)
                        cameraManager.setTorchMode(cameraId, false)
                        SystemClock.sleep(200)
                    }
                    ' ' -> SystemClock.sleep(600)
                    '/' -> SystemClock.sleep(1200)
                }
            }
            tvRepeatedTimes.text = "Repeated $i times"
        }
    }

    private fun noFlashlightDialog() {
        val dialogToShow = AlertDialog.Builder(this)

        dialogToShow.setTitle("Error")
        dialogToShow.setMessage("Flashlight not detected! If this is a mistake, submit an issue on GitHub.")

        dialogToShow.setPositiveButton("Ok") { dialog, which ->
            exitProcess(status=0)
        }

        dialogToShow.show()
    }
}