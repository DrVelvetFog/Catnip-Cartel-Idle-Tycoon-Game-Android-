package com.example.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

class MusicManager(private val context: Context) {
  private val tag = "MusicManager"

  private var gameplayPlayer: MediaPlayer? = null
  private var stingerPlayer: MediaPlayer? = null
  private var isMuted: Boolean = false
  private var isRaidMode: Boolean = false

  init {
    val prefs = context.getSharedPreferences("catnip_cartel_save", Context.MODE_PRIVATE)
    isMuted = !prefs.getBoolean("music_enabled", true)
  }

  fun setMuted(muted: Boolean) {
    isMuted = muted
    val prefs = context.getSharedPreferences("catnip_cartel_save", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("music_enabled", !muted).apply()

    if (muted) {
      gameplayPlayer?.pause()
      stingerPlayer?.pause()
    } else {
      if (isRaidMode) {
        if (stingerPlayer != null && !stingerPlayer!!.isPlaying) {
          try { stingerPlayer?.start() } catch (e: Exception) { Log.e(tag, "Error resuming stinger", e) }
        } else {
          playRaidStinger()
        }
      } else {
        if (gameplayPlayer != null && !gameplayPlayer!!.isPlaying) {
          try { gameplayPlayer?.start() } catch (e: Exception) { Log.e(tag, "Error resuming gameplay", e) }
        } else {
          playGameplay()
        }
      }
    }
  }

  fun isMuted(): Boolean = isMuted

  /**
   * Automatically uses gameplay_loop from res/raw when present (resolved dynamically via getIdentifier)
   */
  fun playGameplay() {
    if (isMuted) return
    isRaidMode = false

    // Stop and release stinger if active
    try {
      stingerPlayer?.stop()
      stingerPlayer?.release()
    } catch (_: Exception) {}
    stingerPlayer = null

    // If gameplay player is already running, continue
    if (gameplayPlayer != null && gameplayPlayer!!.isPlaying) return

    val resId = context.resources.getIdentifier("gameplay_loop", "raw", context.packageName)
    if (resId != 0) {
      try {
        gameplayPlayer?.release()
        gameplayPlayer = MediaPlayer().apply {
          setAudioAttributes(
            AudioAttributes.Builder()
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .setUsage(AudioAttributes.USAGE_GAME)
              .build()
          )
          val afd = context.resources.openRawResourceFd(resId)
          if (afd != null) {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            isLooping = true
            setVolume(0.7f, 0.7f)
            prepare()
            start()
          }
        }
        Log.d(tag, "Gameplay loop started successfully from raw/gameplay_loop")
      } catch (e: Exception) {
        Log.e(tag, "Failed to start gameplay_loop media player", e)
      }
    } else {
      Log.d(tag, "No gameplay_loop resource found in res/raw (slot empty)")
    }
  }

  /**
   * Plays raid_stinger one-shot when a raid starts. When the stinger completes or
   * when resumeAfterRaid() is called, returns to the gameplay loop.
   */
  fun playRaidStinger() {
    isRaidMode = true

    // Pause gameplay loop while raid is active
    try {
      if (gameplayPlayer != null && gameplayPlayer!!.isPlaying) {
        gameplayPlayer?.pause()
      }
    } catch (_: Exception) {}

    if (isMuted) return

    val resId = context.resources.getIdentifier("raid_stinger", "raw", context.packageName)
    if (resId != 0) {
      try {
        stingerPlayer?.release()
        stingerPlayer = MediaPlayer().apply {
          setAudioAttributes(
            AudioAttributes.Builder()
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .setUsage(AudioAttributes.USAGE_GAME)
              .build()
          )
          val afd = context.resources.openRawResourceFd(resId)
          if (afd != null) {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            isLooping = false
            setVolume(0.9f, 0.9f)
            setOnCompletionListener {
              // When one-shot stinger finishes, return to gameplay loop if raid mode ended
              if (!isRaidMode) {
                resumeAfterRaid()
              }
            }
            prepare()
            start()
          }
        }
        Log.d(tag, "Raid stinger started successfully from raw/raid_stinger")
      } catch (e: Exception) {
        Log.e(tag, "Failed to start raid_stinger media player", e)
      }
    } else {
      Log.d(tag, "No raid_stinger resource found in res/raw")
    }
  }

  /**
   * Resumes gameplay loop after raid ends (won, lost, or dismissed)
   */
  fun resumeAfterRaid() {
    isRaidMode = false

    try {
      stingerPlayer?.stop()
      stingerPlayer?.release()
    } catch (_: Exception) {}
    stingerPlayer = null

    if (isMuted) return

    if (gameplayPlayer != null) {
      try {
        gameplayPlayer?.start()
      } catch (e: Exception) {
        playGameplay()
      }
    } else {
      playGameplay()
    }
  }

  fun pause() {
    try {
      if (gameplayPlayer?.isPlaying == true) gameplayPlayer?.pause()
      if (stingerPlayer?.isPlaying == true) stingerPlayer?.pause()
    } catch (_: Exception) {}
  }

  fun resume() {
    if (isMuted) return
    try {
      if (isRaidMode) {
        stingerPlayer?.start()
      } else {
        if (gameplayPlayer != null && !gameplayPlayer!!.isPlaying) {
          gameplayPlayer?.start()
        } else if (gameplayPlayer == null) {
          playGameplay()
        }
      }
    } catch (_: Exception) {}
  }

  fun release() {
    try {
      gameplayPlayer?.stop()
      gameplayPlayer?.release()
    } catch (_: Exception) {}
    gameplayPlayer = null

    try {
      stingerPlayer?.stop()
      stingerPlayer?.release()
    } catch (_: Exception) {}
    stingerPlayer = null
  }
}
