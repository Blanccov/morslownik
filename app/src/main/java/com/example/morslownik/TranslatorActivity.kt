package com.example.morslownik

import FlashController
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Telephony
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.plcoding.audiorecorder.playback.AndroidAudioPlayer
import com.plcoding.audiorecorder.record.AndroidAudioRecorder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale


class TranslatorActivity : ComponentActivity() {

    companion object {
        private const val PICK_CONTACT_REQUEST = 1
    }
    private lateinit var seekBar: SeekBar
    private lateinit var wpmView: TextView

    private var wpm = 10

    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null
    private var isRecording = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null){
            if (requestCode == 10){
                val stringArrayListExtra = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                findViewById<TextView>(R.id.prevText).setText(stringArrayListExtra!![0])
                isRecording = false
            }
        }
        else {
            Toast.makeText(applicationContext, "Failed to recognize speech!", Toast.LENGTH_LONG).show()
        }
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = data?.data
            val cursor: Cursor? = contactUri?.let {
                contentResolver.query(it, null, null, null, null)
            }

            cursor?.use {
                if (it.moveToFirst()) {
                    val phoneNumberIndex: Int =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val phoneNumber: String = it.getString(phoneNumberIndex)
                    val afterText = findViewById<TextView>(R.id.afterText)

                    // Sprawdź, czy istnieje aplikacja do obsługi wiadomości SMS/MMS
                    if (isSmsMmsIntentHandled()) {
                        val sendIntent = Intent(Intent.ACTION_VIEW)
                        sendIntent.putExtra("address", phoneNumber)
                        sendIntent.putExtra("sms_body", afterText.text.toString().trim().replace(Regex("\\n+"), "\n").replace(Regex(" +"), " "))
                        sendIntent.type = "vnd.android-dir/mms-sms"
                        startActivity(sendIntent)
                    } else {
                        // Jeśli nie ma wbudowanej aplikacji, sprawdź domyślną aplikację
                        val defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this)
                        if (defaultSmsApp != null) {
                            val sendIntent = Intent(Intent.ACTION_SENDTO)
                            sendIntent.data = Uri.parse("smsto:$phoneNumber")
                            sendIntent.putExtra("sms_body", afterText.text.toString().trim().replace(Regex("\\n+"), "\n").replace(Regex(" +"), " "))
                            startActivity(sendIntent)
                        } else {
                            // Obsługa, gdy nie ma ani wbudowanej, ani domyślnej aplikacji do obsługi wiadomości SMS/MMS
                            // Możesz wyświetlić komunikat lub podjąć inne działania
                            // w zależności od wymagań Twojej aplikacji
                            Toast.makeText(this, "Brak aplikacji do obsługi wiadomości SMS/MMS", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun isSmsMmsIntentHandled(): Boolean {
        val sendIntent = Intent(Intent.ACTION_VIEW)
        sendIntent.type = "vnd.android-dir/mms-sms"
        val activities: List<ResolveInfo> = packageManager.queryIntentActivities(sendIntent, 0)
        return activities.isNotEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.translatorlayout)

        val morseCodeController = MorseCodeController(this)
        val flashController = FlashController(this)

        var captureText = CaptureActivity.textToTranslate

        if (captureText != "") {
            findViewById<EditText>(R.id.prevText).setText(captureText)
        }

        val previousText =
            findViewById<EditText>(R.id.prevText)
        val afterText = findViewById<TextView>(R.id.afterText)

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        findViewById<ImageView>(R.id.button_copy).setOnClickListener{
            val textToCopy = previousText.text.toString().trim().replace(Regex("\\n+"), "\n").replace(Regex(" +"), " ")
            if(textToCopy.isNotEmpty()){
            val clip = ClipData.newPlainText("Text", textToCopy)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(
                this,
                "Text copied",
                Toast.LENGTH_LONG
            )
                .show()}
        }

        findViewById<ImageView>(R.id.button_copy2).setOnClickListener{
            val textToCopy = afterText.text.toString().trim().replace(Regex("\\n+"), "\n").replace(Regex(" +"), " ")
            if(textToCopy.isNotEmpty()){
                val clip = ClipData.newPlainText("Text", textToCopy)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(
                    this,
                    "Code copied",
                    Toast.LENGTH_LONG
                )
                    .show()}
        }

        var dumper = ""
        var isTextToMorseMode = true

        findViewById<Button>(R.id.translateButton).setOnClickListener {
            val textToTranslate = previousText.text.toString().trim().replace(Regex("\\n+"), "\n").replace(Regex(" +"), " ")
            if (textToTranslate.isNotEmpty()) {
                if (!textToTranslate.equals(dumper)) {
                    dumper = textToTranslate
                    val translatedText = if (isTextToMorseMode) {
                        morseCodeController.translateToMorse(textToTranslate)
                    } else {
                        morseCodeController.translateToText(textToTranslate)
                    }
                    if (translatedText == "   ") {
                        // Zwrócono błąd, ustaw wartość dla 'afterText' i zakończ funkcję
                        afterText.hint = "Wykryto nieprawidłowe znaki"
                        afterText.text = ""
                        return@setOnClickListener
                    }
                    afterText.text = translatedText

                    val db = DBController(this, null)
                    val plain = textToTranslate
                    val morse = translatedText

                    db.addHistory(plain, morse)

//                    Toast.makeText(
//                        this,
//                        "Translation added to database, probably...",
//                        Toast.LENGTH_LONG
//                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Already translated",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Cannot translate empty text",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val prev = findViewById<EditText>(R.id.prevText)
        val after = findViewById<TextView>(R.id.afterText)
        var temp = ""

        findViewById<ImageButton>(R.id.switchButton).setOnClickListener {
            isTextToMorseMode = !isTextToMorseMode
            val switchButtonText = if (isTextToMorseMode) "Hello" else ".... . .-.. .-.. ---"
                temp = prev.text.toString()
                prev.setText(after.text)
                after.setText(temp)

            findViewById<EditText>(R.id.prevText).hint = switchButtonText
        }


        findViewById<Button>(R.id.clearText).setOnClickListener {
            previousText.text.clear()
            afterText.text = ""
        }

        val playButton: ImageButton = findViewById(R.id.soundButton)
        val torchButton = findViewById<ImageButton>(R.id.torchButton)

        playButton.setOnClickListener {
            val sentences = afterText.text.toString().split("/").toTypedArray()

            // Zablokowanie seekbara
            seekBar.isEnabled = false
            playButton.isEnabled = false
            torchButton.isEnabled = false

            GlobalScope.launch {
                morseCodeController.playSound(sentences, wpm)

                runOnUiThread {
                    // Odblokowanie seekbara
                    seekBar.isEnabled = true
                    playButton.isEnabled = true
                    torchButton.isEnabled = true
                }
            }
        }


        findViewById<Button>(R.id.smsButton).setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_PICK)
            sendIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(sendIntent, PICK_CONTACT_REQUEST)
        }


        torchButton.setOnClickListener {
            val sentences = afterText.text.toString().split("/").toTypedArray()

            // Zablokowanie seekbara
            seekBar.isEnabled = false
            torchButton.isEnabled = false
            playButton.isEnabled = false

            lifecycleScope.launch {
                flashController.playFlashSignal(sentences, wpm)

                // Odblokowanie seekbara
                seekBar.isEnabled = true
                torchButton.isEnabled = true
                playButton.isEnabled = true
            }
        }

        val microphoneButton = findViewById<ImageButton>(R.id.microphoneButton)

        //rozpoznawanie mowy
            microphoneButton.setOnClickListener {
                if (isRecording) {
                    recorder.stop()
                    isRecording = false
                    Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    startActivityForResult(intent, 10)

                    isRecording = true
                    Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
                }
            }

        //back button

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        //seek bar

            seekBar = findViewById(R.id.seekBar)
            wpmView = findViewById(R.id.wpmView)

            seekBar.max = 14
            seekBar.min = 4
            seekBar.progress = 9

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val value = progress + 1
                    wpmView.text = "WPM: $value"
                    wpm = value
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // Puste
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    // Puste
                }
            })
        }
    }
