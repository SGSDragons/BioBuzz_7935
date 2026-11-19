package org.firstinspires.ftc.teamcode.subsystem

import android.content.Context
import com.qualcomm.ftccommon.SoundPlayer
import java.io.File

class AudioPlayer(private val context: Context) {
    private fun loadAsset(name: String): File =
        File(context.cacheDir, name).also { file ->
            if (!file.exists() || file.length() == 0L) {
                context.assets.open("sounds/$name").use { input ->
                    file.outputStream().use { input.copyTo(it) }
                }
            }
        }

    val factorioDestroyed: File = loadAsset("factorio-destroyed.ogg")

    init {
        factorioDestroyed
    }

    fun playSound(sound: File) {
        SoundPlayer.getInstance().startPlaying(context, sound)
    }
}