import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper

class FlashController(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var flashSignalRunnable: Runnable

    private var isFlashing = false // Track if the flashlight is currently flashing

    @Throws(InterruptedException::class)
    fun playFlashSignal(morseMessage: Array<String>, wpm: Int) {
        val dotDuration = (1200 / wpm).toLong()
        val dashDuration = (3 * dotDuration).toInt()
        val slashDuration = (7 * dotDuration).toInt()

        flashSignalRunnable = Runnable {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraId = cameraManager.cameraIdList[0] // Use the first camera

            for (pattern in morseMessage) {
                for (c in pattern.toCharArray()) {
                    when (c) {
                        '.' -> {
                            flashTorch(cameraManager, cameraId, dotDuration)
                            Thread.sleep(dotDuration)
                        }
                        '-' -> {
                            flashTorch(cameraManager, cameraId, dashDuration.toLong())
                            Thread.sleep(dashDuration.toLong())
                        }
                        '/' -> {
                            Thread.sleep(slashDuration.toLong())
                        }
                        ' ' -> {
                            // Pause between words
                            Thread.sleep(dotDuration * 3)
                        }
                    }
                    stopFlash(cameraManager, cameraId) // Turn off flashlight after each signal
                    Thread.sleep(dotDuration) // Pause between signals
                }
                Thread.sleep(dotDuration * 3) // Pause between characters
            }

            stopFlashSignal() // Stop flashing at the end of the sequence
        }

        handler.post(flashSignalRunnable)
    }

    private fun flashTorch(cameraManager: CameraManager, cameraId: String, duration: Long) {
        isFlashing = true
        cameraManager.setTorchMode(cameraId, true)
        handler.postDelayed({
            stopFlash(cameraManager, cameraId)
        }, duration)
    }

    private fun stopFlash(cameraManager: CameraManager, cameraId: String) {
        if (isFlashing) {
            isFlashing = false
            cameraManager.setTorchMode(cameraId, false)
        }
    }

    fun startFlashSignal() {
        handler.post(flashSignalRunnable)
    }

    fun stopFlashSignal() {
        handler.removeCallbacks(flashSignalRunnable)
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        stopFlash(cameraManager, cameraId) // Ensure flashlight is turned off when stopping the signal
    }
}
