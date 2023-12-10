package com.example.morslownik;

//import javax.sound.sampled.AudioFormat
//import javax.sound.sampled.AudioSystem
//import javax.sound.sampled.DataLine
//import javax.sound.sampled.SourceDataLine
//import javax.sound.sampled.LineUnavailableException

class MorseCodeController {
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
        morseCodeMap['O'] = "---"
        morseCodeMap['P'] = ".--."
        morseCodeMap['Q'] = "--.-"
        morseCodeMap['R'] = ".-."
        morseCodeMap['S'] = "..."
        morseCodeMap['T'] = "-"
        morseCodeMap['U'] = "..-"
        morseCodeMap['V'] = "...-"
        morseCodeMap['W'] = ".--"
        morseCodeMap['X'] = "-..-"
        morseCodeMap['Y'] = "-.--"
        morseCodeMap['Z'] = "--.."

        // Inicjalizacja mapy dla małych liter, cyfr oraz znaków specjalnych
        morseCodeMap['a'] = ".-"
        morseCodeMap['b'] = "-..."
        morseCodeMap['c'] = "-.-."
        morseCodeMap['d'] = "-.."
        morseCodeMap['e'] = "."
        morseCodeMap['f'] = "..-."
        morseCodeMap['g'] = "--."
        morseCodeMap['h'] = "...."
        morseCodeMap['i'] = ".."
        morseCodeMap['j'] = ".---"
        morseCodeMap['k'] = "-.-"
        morseCodeMap['l'] = ".-.."
        morseCodeMap['m'] = "--"
        morseCodeMap['n'] = "-."
        morseCodeMap['o'] = "---"
        morseCodeMap['p'] = ".--."
        morseCodeMap['q'] = "--.-"
        morseCodeMap['r'] = ".-."
        morseCodeMap['s'] = "..."
        morseCodeMap['t'] = "-"
        morseCodeMap['u'] = "..-"
        morseCodeMap['v'] = "...-"
        morseCodeMap['w'] = ".--"
        morseCodeMap['x'] = "-..-"
        morseCodeMap['y'] = "-.--"
        morseCodeMap['z'] = "--.."

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
    }

    fun translateToMorse(textToTranslate: String): String {
        val translatedText = StringBuilder()
        textToTranslate.toCharArray().forEach { letter ->
            translatedText.append("${morseCodeMap[letter]} ")
        }
        return translatedText.toString()
    }

//    @Throws(LineUnavailableException::class, InterruptedException::class)
//    fun playSound(morseMessage: Array<String>) {
//        val audioFormat = AudioFormat(44100f, 16, 1, true, false)
//        val dataLineInfo = DataLine.Info(SourceDataLine::class.java, audioFormat)
//        val sourceDataLine = AudioSystem.getLine(dataLineInfo) as SourceDataLine
//        sourceDataLine.open(audioFormat)
//        sourceDataLine.start()
//
//        val dotDuration = 200
//        val dashDuration = (1.5 * dotDuration).toInt()
//        val slashDuration = 2 * dashDuration
//
//        for (pattern in morseMessage) {
//            println(pattern)
//
//            for (c in pattern.toCharArray()) {
//                when (c) {
//                    '.' -> {
//                        playBeep(sourceDataLine, dotDuration)
//                        Thread.sleep(dotDuration.toLong())
//                    }
//                    '-' -> {
//                        playBeep(sourceDataLine, dashDuration)
//                        Thread.sleep(dotDuration.toLong())
//                    }
//                    '/' -> Thread.sleep(slashDuration.toLong())
//                }
//            }
//            Thread.sleep(dotDuration.toLong())
//        }
//
//        sourceDataLine.drain()
//        sourceDataLine.stop()
//        sourceDataLine.close()
//    }
//
//    private fun playBeep(line: SourceDataLine, duration: Int) {
//        val data = ByteArray(duration * 44100 / 1000)
//
//        for (i in data.indices) {
//            val angle = i / (44100.0 / 440) * 2.0 * Math.PI
//            data[i] = (Math.sin(angle) * 127.0).toByte()
//        }
//
//        line.write(data, 0, data.size)
//    }
}