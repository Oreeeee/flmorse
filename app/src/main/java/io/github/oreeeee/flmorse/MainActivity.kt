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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUserText = findViewById(R.id.etUserText)
        btnFlash = findViewById(R.id.btnFlash)

        // Initialize flashlight
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        //cameraManager.setTorchMode(cameraId, true)

        btnFlash.setOnClickListener {
            try {
                flashMorse(convertTextToMorse(etUserText.text.toString()))
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

    private fun flashMorse(stringToMorse: String) {
        stringToMorse.forEach { character ->
            when(character) {
                '.' -> {
                    cameraManager.setTorchMode(cameraId, true)
                    SystemClock.sleep(100)
                    cameraManager.setTorchMode(cameraId, false)
                    SystemClock.sleep(100)
                }
                '-' -> {
                    cameraManager.setTorchMode(cameraId, true)
                    SystemClock.sleep(300)
                    cameraManager.setTorchMode(cameraId, false)
                    SystemClock.sleep(100)
                }
                ' ' -> SystemClock.sleep(300)
                '/' -> SystemClock.sleep(700)
            }
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