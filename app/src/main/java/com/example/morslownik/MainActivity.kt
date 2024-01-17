package com.example.morslownik

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )

        setContentView(R.layout.menulayout)

        findViewById<ImageButton>(R.id.translator_btn).setOnClickListener {
            val intentT = Intent(applicationContext, TranslatorActivity::class.java)
            startActivity(intentT)
        }


        findViewById<ImageButton>(R.id.hisotryButton).setOnClickListener {
            val intentT = Intent(applicationContext, HistoryActivity::class.java)
            startActivity(intentT)
        }



        findViewById<ImageButton>(R.id.cameraButton).setOnClickListener {
            val intentT = Intent(applicationContext, CaptureActivity::class.java)
            startActivity(intentT)
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.nokiasong)

        findViewById<ImageButton>(R.id.alarmButton).setOnClickListener{
            playSound()
        }

    }

    // to tylko do zabawnego sos'a
    private fun playSound() {
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
