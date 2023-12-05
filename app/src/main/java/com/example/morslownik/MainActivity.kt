package com.example.morslownik

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.menulayout)

        findViewById<ImageButton>(R.id.translator_btn).setOnClickListener {
            val intentT = Intent(applicationContext, TranslatorActivity::class.java)
            startActivity(intentT)
        }

        findViewById<ImageButton>(R.id.cameraButton).setOnClickListener {
            val intentT = Intent(applicationContext, CaptureActivity::class.java)
            startActivity(intentT)
        }
    }
}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MorslownikTheme {
//        Greeting("Android")
//    }
//}