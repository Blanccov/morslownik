package com.example.morslownik

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class CaptureActivity : ComponentActivity() {
    private var imageUri: Uri? = null
    private lateinit var textRecognizer: TextRecognizer
    private lateinit var clear: ImageButton
    private lateinit var getImage: ImageButton
    private lateinit var next: ImageButton
    private lateinit var recgText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.capturelayout)

        clear = findViewById(R.id.clearButton)
        getImage = findViewById(R.id.getImageButton)
        next = findViewById(R.id.nextButton)
        recgText = findViewById(R.id.recgText)

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        getImage.setOnClickListener{
            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }

        next.setOnClickListener {
            val intentT = Intent(applicationContext, TranslatorActivity::class.java)
            startActivity(intentT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.data // Przypisanie wartości imageUri
                Toast.makeText(this, "image selected", Toast.LENGTH_SHORT).show()
                recognizeText()
            }
        } else {
            Toast.makeText(this, "image not selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun recognizeText() {
        if (imageUri != null) {
            try {
                val inputImage = InputImage.fromFilePath(this, imageUri!!)

                textRecognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val resultText = visionText.text
                        recgText.text = resultText
                        textToTranslate = resultText
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var textToTranslate: String = ""
    }
}