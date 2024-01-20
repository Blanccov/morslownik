package com.example.morslownik

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

class DictionaryActivity : ComponentActivity() {

    private lateinit var morseCodeController: MorseCodeController
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dictionary)

        //back button

        findViewById<ImageButton>(R.id.backButton4).setOnClickListener {
            onBackPressed()
        }

        morseCodeController = MorseCodeController(this)
        listView = findViewById(R.id.dictionaryText )

        // Tworzenie listy z dużymi literami i ich tłumaczeniami
        val morseEntries = morseCodeController.morseCodeMap.entries.map { entry ->
            "${entry.key}: ${entry.value}"
        }.toList()
//
        // Ustawianie adaptera dla listy
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, morseEntries)
        listView.adapter = adapter
    }
}
