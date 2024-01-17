package com.example.morslownik;

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.os.Vibrator
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException



class MorseCodeController(private val context: Context) {

    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val morseCodeMap: HashMap<Char, String> = HashMap()

    init {
        morseCodeMap['A'] = ".-"
        morseCodeMap['Ą'] = ".-.-"
        morseCodeMap['B'] = "-..."
        morseCodeMap['C'] = "-.-."
        morseCodeMap['Ć'] = "-.-.."
        morseCodeMap['D'] = "-.."
        morseCodeMap['E'] = "."
        morseCodeMap['Ę'] = "..-.."
        morseCodeMap['F'] = "..-."
        morseCodeMap['G'] = "--."
        morseCodeMap['H'] = "...."
        morseCodeMap['I'] = ".."
        morseCodeMap['J'] = ".---"
        morseCodeMap['K'] = "-.-"
        morseCodeMap['L'] = ".-.."
        morseCodeMap['Ł'] = ".-..-"
        morseCodeMap['M'] = "--"
        morseCodeMap['N'] = "-."
        morseCodeMap['Ń'] = "--.--"
        morseCodeMap['O'] = "---"
        morseCodeMap['Ó'] = "---."
        morseCodeMap['P'] = ".--."
        morseCodeMap['Q'] = "--.-"
        morseCodeMap['R'] = ".-."
        morseCodeMap['S'] = "..."
        morseCodeMap['Ś'] = "...-..."
        morseCodeMap['T'] = "-"
        morseCodeMap['U'] = "..-"
        morseCodeMap['V'] = "...-"
        morseCodeMap['W'] = ".--"
        morseCodeMap['X'] = "-..-"
        morseCodeMap['Y'] = "-.--"
        morseCodeMap['Z'] = "--.."
        morseCodeMap['Ź'] = "--..-"
        morseCodeMap['Ż'] = "--..-."

        // Inicjalizacja mapy dla małych liter, cyfr oraz znaków specjalnych
        morseCodeMap['a'] = ".-"
        morseCodeMap['ą'] = ".-.-"
        morseCodeMap['b'] = "-..."
        morseCodeMap['c'] = "-.-."
        morseCodeMap['ć'] = "-.-.."
        morseCodeMap['d'] = "-.."
        morseCodeMap['e'] = "."
        morseCodeMap['ę'] = "..-.."
        morseCodeMap['f'] = "..-."
        morseCodeMap['g'] = "--."
        morseCodeMap['h'] = "...."
        morseCodeMap['i'] = ".."
        morseCodeMap['j'] = ".---"
        morseCodeMap['k'] = "-.-"
        morseCodeMap['l'] = ".-.."
        morseCodeMap['ł'] = ".-..-"
        morseCodeMap['m'] = "--"
        morseCodeMap['n'] = "-."
        morseCodeMap['ń'] = "--.--"
        morseCodeMap['o'] = "---"
        morseCodeMap['ó'] = "---."
        morseCodeMap['p'] = ".--."
        morseCodeMap['q'] = "--.-"
        morseCodeMap['r'] = ".-."
        morseCodeMap['s'] = "..."
        morseCodeMap['ś'] = "...-..."
        morseCodeMap['t'] = "-"
        morseCodeMap['u'] = "..-"
        morseCodeMap['v'] = "...-"
        morseCodeMap['w'] = ".--"
        morseCodeMap['x'] = "-..-"
        morseCodeMap['y'] = "-.--"
        morseCodeMap['z'] = "--.."
        morseCodeMap['ź'] = "--..-"
        morseCodeMap['ż'] = "--..-."

        // Inicjalizacja mapy znaków specjalnych, cyfr i innych
        morseCodeMap['0'] = "-----"
        morseCodeMap['1'] = ".----"
        morseCodeMap['2'] = "..---"
        morseCodeMap['3'] = "...--"
        morseCodeMap['4'] = "....-"
        morseCodeMap['5'] = "....."
        morseCodeMap['6'] = "-...."
        morseCodeMap['7'] = "--..."
        morseCodeMap['8'] = "---.."
        morseCodeMap['9'] = "----."
        morseCodeMap[' '] = "/"
        morseCodeMap[','] = "--..--"
        morseCodeMap['.'] = ".-.-.-"
        morseCodeMap['?'] = "..--.."
        morseCodeMap[';'] = "-.-.-."
        morseCodeMap[':'] = "---..."
        morseCodeMap['('] = "-.--."
        morseCodeMap[')'] = "-.--.-"
        morseCodeMap['['] = "-.--."
        morseCodeMap[']'] = "-.--.-"
        morseCodeMap['{'] = "-.--."
        morseCodeMap['}'] = "-.--.-"
        morseCodeMap['+'] = ".-.-."
        morseCodeMap['-'] = "-....-"
        morseCodeMap['_'] = "..--.-"
        morseCodeMap['"'] = ".-..-."
        morseCodeMap['\''] = ".----."
        morseCodeMap['/'] = "-..-."
        morseCodeMap['\\'] = "-..-."
        morseCodeMap['@'] = ".--.-."
        morseCodeMap['='] = "-...-"
        morseCodeMap['!'] = "-.-.--"
        morseCodeMap['\n'] = "||\n"
    }

    fun translateToMorse(textToTranslate: String): String {
        val translatedText = StringBuilder()
        textToTranslate.toCharArray().forEach { letter ->
            translatedText.append("${morseCodeMap[letter]} ")
        }
        return translatedText.toString()
    }



    @Throws(InterruptedException::class)
    fun playSound(morseMessage: Array<String>, wpm: Int) {
        val dotDuration = (1200 / wpm).toLong()
        val dashDuration = (3 * dotDuration).toInt()
        val slashDuration = (7 * dotDuration).toInt()

        val sampleRate = 44100
        val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)

        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize,
            AudioTrack.MODE_STREAM
        )

        audioTrack.play()

        for (pattern in morseMessage) {
            for (c in pattern.toCharArray()) {
                when (c) {
                    '.' -> {
                        playBeep(audioTrack, dotDuration.toInt())
                        Thread.sleep(dotDuration)
                    }
                    '-' -> {
                        playBeep(audioTrack, dashDuration)
                        Thread.sleep(dotDuration)
                    }
                    '/' -> {
                        Thread.sleep(slashDuration.toLong())
                    }
                    ' ' -> {
                        Thread.sleep(dashDuration.toLong())
                    }
                }
            }
            Thread.sleep(dotDuration)
        }

        audioTrack.stop()
        audioTrack.release()
    }

    private fun playVibration(duration: Long) {
        vibrator.vibrate(duration)
    }

    private fun playBeep(audioTrack: AudioTrack, duration: Int) {
        val numSamples = duration * 44100 / 1000
        val buffer = ShortArray(numSamples)
        val amp = 10000
        val twoPi = 2.0 * Math.PI

        val freq = 440.0
        val fadeLength = 1000 // Długość cross-fadingu (w próbkach)

        val beepThread = Thread {
            for (i in 0 until numSamples) {
                val fadeValue = if (i < fadeLength) (i.toFloat() / fadeLength.toFloat()) else if (i > numSamples - fadeLength) ((numSamples - i).toFloat() / fadeLength.toFloat()) else 1.0f

                val sample =
                    (amp * fadeValue * Math.sin(i.toDouble() * twoPi * freq / 44100.0)).toInt().toShort()
                buffer[i] = sample
            }

            audioTrack.write(buffer, 0, buffer.size)
        }

        val vibrationThread = Thread {
            playVibration(duration.toLong())
        }

        beepThread.start()
        vibrationThread.start()

        try {
            beepThread.join()
            vibrationThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }



}