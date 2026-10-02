package com.example.ui.audio

import android.content.Context
import android.media.MediaPlayer

/**
 * Background music for Catnip Cartel Tycoon.
 *
 * Track slots (all optional except the title theme):
 *  - title   -> R.raw.catnip_cartel   (ships with the app: the vocal theme song)
 *  - loop    -> res/raw/gameplay_loop.m4a  (future: seamless instrumental loop)
 *  - raid    -> res/raw/raid_stinger.m4a   (future: 30s Animal Control tension cue)
 *
 * Missing future tracks are resolved to 0 and skipped gracefully, so dropping
 * a new file into res/raw is all it takes to activate that slot.
 */
class MusicManager(private val context: Context) {

  private var player: MediaPlayer? = null
  private var currentResId: Int = 0
  var enabled: Boolean = true
    private set

  private fun resId(name: String): Int =
    context.resources.getIdentifier(name, "raw", context.packageName)

  /** The in-game loop: gameplay_loop when present, otherwise the title theme. */
  private fun loopResId(): Int {
    val loop = resId("gameplay_loop")
    return if (loop != 0) loop else com.example.R.raw.catnip_cartel
  }

  private fun play(resId: Int, loop: Boolean, volume: Float = 0.55f) {
    if (!enabled || resId == 0) return
    if (currentResId == resId && player?.isPlaying == true) return
    stop()
    try {
      player = MediaPlayer.create(context, resId)?.apply {
        isLooping = loop
        setVolume(volume, volume)
        start()
      }
      currentResId = resId
    } catch (_: Exception) {
      player = null
      currentResId = 0
    }
  }

  /** Title/menu music: the vocal CATNIP CARTEL theme. */
  fun playTitle() = play(com.example.R.raw.catnip_cartel, loop = true)

  /** Gameplay music: seamless loop when present, title theme otherwise. */
  fun playGameplay() = play(loopResId(), loop = true, volume = 0.45f)

  /** Raid stinger: one-shot tension cue when present; ignored otherwise. */
  fun playRaidStinger() {
    val stinger = resId("raid_stinger")
    if (stinger != 0) play(stinger, loop = false, volume = 0.7f)
  }

  /** Back to the loop after a raid ends. */
  fun resumeAfterRaid() = playGameplay()

  fun setEnabled(on: Boolean) {
    enabled = on
    if (!on) stop() else playGameplay()
  }

  fun pause() {
    try { player?.pause() } catch (_: Exception) {}
  }

  fun resume() {
    if (!enabled) return
    try {
      if (currentResId != 0 && player?.isPlaying == false) player?.start()
      else if (currentResId == 0) playGameplay()
    } catch (_: Exception) {}
  }

  fun stop() {
    try {
      player?.stop()
      player?.release()
    } catch (_: Exception) {}
    player = null
    currentResId = 0
  }

  fun release() = stop()
}
