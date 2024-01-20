package com.example.morslownik

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

class DictionaryActivity : ComponentActivity() {

    private lateinit var morseCodeController: MorseCodeController
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dictionary)

        // back button
        findViewById<ImageButton>(R.id.backButton4).setOnClickListener {
            onBackPressed()
        }

        morseCodeController = MorseCodeController(this)
        listView = findViewById(R.id.dictionaryText)

        // Tworzenie listy z dużymi literami i ich tłumaczeniami
        val morseEntries = morseCodeController.morseCodeMap.entries.map { entry ->
            "${entry.key}: ${entry.value}"
        }.toList()

        // Ustawianie adaptera dla listy
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, morseEntries)
        listView.adapter = adapter

        // Obsługa kliknięcia na elemencie listy
        listView.setOnItemClickListener { _, _, position, _ ->
            // Pobierz tekst zaznaczonego elementu
            val selectedText = listView.getItemAtPosition(position).toString()

            // Pobierz znak (część przed dwukropkiem)
            val character = selectedText.substringBefore(":").trim()

            // Pobierz kod Morsa (część po dwukropku)
            val morseCode = selectedText.substringAfter(":").trim()

            // Skopiuj kod Morsa do schowka
            copyToClipboard(morseCode)

            // Wyświetl Toast z informacją o skopiowaniu
            showToast(character)
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Morse Code", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun showToast(character: String) {
        val toastMessage = "Morse code for character '$character' has been copied."
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }
}
