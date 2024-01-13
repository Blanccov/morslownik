import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import kotlinx.coroutines.*

class FlashController(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var flashSignalJob: Job
    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private var isFlashing = false // Track if the flashlight is currently flashing

    @Throws(InterruptedException::class)
    suspend fun playFlashSignal(morseMessage: Array<String>, wpm: Int) {
        val dotDuration = (1200 / wpm).toLong()
        val dashDuration = (3 * dotDuration).toInt()
        val slashDuration = (7 * dotDuration).toInt()

        flashSignalJob = coroutineScope {
            launch(Dispatchers.Default) {
                val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val cameraId = cameraManager.cameraIdList[0] // Use the first camera

                for (pattern in morseMessage) {
                    for (c in pattern.toCharArray()) {
                        when (c) {
                            '.' -> {
                                flashTorch(cameraManager, cameraId, dotDuration)
                                delay(dotDuration)
                            }
                            '-' -> {
                                flashTorch(cameraManager, cameraId, dashDuration.toLong())
                                delay(dashDuration.toLong())
                            }
                            '/' -> {
                                delay(slashDuration.toLong())
                            }
                            ' ' -> {
                                // Pause between words
                                delay(dotDuration * 3)
                            }
                        }
                        stopFlash(cameraManager, cameraId) // Turn off flashlight after each signal
                        delay(dotDuration) // Pause between signals
                    }
                    delay(dotDuration * 3) // Pause between characters
                }

                stopFlashSignal() // Stop flashing at the end of the sequence
            }
        }
    }

    private suspend fun flashTorch(cameraManager: CameraManager, cameraId: String, duration: Long) {
        withContext(Dispatchers.Main) {
            isFlashing = true
            cameraManager.setTorchMode(cameraId, true)
            vibrator.vibrate(duration)
            delay(duration)
            stopFlash(cameraManager, cameraId)
        }
    }

    private suspend fun stopFlash(cameraManager: CameraManager, cameraId: String) {
        withContext(Dispatchers.Main) {
            if (isFlashing) {
                isFlashing = false
                cameraManager.setTorchMode(cameraId, false)
            }
        }
    }

    suspend fun stopFlashSignal() {
        if (::flashSignalJob.isInitialized) {
            flashSignalJob.cancel()
        }
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        stopFlash(cameraManager, cameraId) // Ensure flashlight is turned off when stopping the signal
    }
}
