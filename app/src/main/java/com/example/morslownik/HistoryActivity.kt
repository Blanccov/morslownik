package com.example.morslownik

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.text.set

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)

        findViewById<Button>(R.id.clearButton).setOnClickListener {
            val db = DBController(this, null)
            db.clearHistory()
            onBackPressed()
        }

        //back button

        findViewById<ImageButton>(R.id.backButton2).setOnClickListener {
            onBackPressed()
        }

        val db = DBController(this, null)

            // below is the variable for cursor
            // we have called method to get
            // all names from our database
            // and add to name text view
            val cursor = db.getHistory()
            val historyT = findViewById<TextView>(R.id.historyText)

                // moving the cursor to first position and
                // appending value in the text view
                if(cursor!!.moveToFirst()){
                historyT.append(cursor.getString(cursor.getColumnIndexOrThrow(DBController.PLAIN_TEXT)) + "\n")
                historyT.append(cursor.getString(cursor.getColumnIndexOrThrow(DBController.MORSE_CODE)) + "\n\n")

                // moving our cursor to next
                // position and appending values
                while (cursor.moveToNext()) {
                    historyT.append(cursor.getString(cursor.getColumnIndexOrThrow(DBController.PLAIN_TEXT)) + "\n")
                    historyT.append(cursor.getString(cursor.getColumnIndexOrThrow(DBController.MORSE_CODE)) + "\n\n")
                }

                // at last we close our cursor
                cursor.close()}
        else{
            historyT.setText("First time? Nothing to see here :D")
                }
            }

        }



