package org.firstinspires.ftc.teamcode.subsystem

import com.qualcomm.ftccommon.SoundPlayer
import com.qualcomm.robotcore.hardware.HardwareMap
import java.io.File

class AudioPlayer(val hardwareMap: HardwareMap) {
    fun errorSound() {
        val context = hardwareMap.appContext

        val soundFile = File(context.cacheDir, "factorio-destroyed.ogg")

        context.assets.open("sounds/factorio-destroyed.ogg").use { input ->
            soundFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        SoundPlayer.getInstance().startPlaying(context, soundFile)
    }
}