package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Catnip Cartel", appName)
  }

  @Test
  fun `fresh game starts at level 1 with zero nip`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = GameRepository(context)
    val state = repository.state.value

    assertEquals(0.0, state.nipBalance, 0.01)
    assertEquals(1, state.bossLevel)
    assertEquals("STREET ROOKIE", state.bossTitle)
    assertEquals(0L, state.streetCredXp)
  }

  @Test
  fun `tapping avatar increases nip from zero`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = GameRepository(context)
    
    val gained = repository.onAvatarTapped()
    assertTrue(gained >= 1L)
    assertTrue(repository.state.value.nipBalance >= 1.0)
  }

  @Test
  fun `reset game restores clean fresh state`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = GameRepository(context)
    
    repository.onAvatarTapped()
    repository.resetGame()
    
    val state = repository.state.value
    assertEquals(0.0, state.nipBalance, 0.01)
    assertEquals(1, state.bossLevel)
    assertEquals("STREET ROOKIE", state.bossTitle)
  }

  @Test
  fun `tutorial seen flag defaults to false and persists when set`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("catnip_cartel_save", Context.MODE_PRIVATE)

    // Clear any test residue
    prefs.edit().remove("tutorial_seen").apply()
    val initialSeen = prefs.getBoolean("tutorial_seen", false)
    assertEquals(false, initialSeen)

    // Mark tutorial seen
    prefs.edit().putBoolean("tutorial_seen", true).apply()
    val afterSeen = prefs.getBoolean("tutorial_seen", false)
    assertEquals(true, afterSeen)
  }

  @Test
  fun `raw audio resources are present and resolvable`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val gameplayId = context.resources.getIdentifier("gameplay_loop", "raw", context.packageName)
    val stingerId = context.resources.getIdentifier("raid_stinger", "raw", context.packageName)

    assertTrue("gameplay_loop raw resource must exist", gameplayId != 0)
    assertTrue("raid_stinger raw resource must exist", stingerId != 0)

    val musicManager = com.example.ui.audio.MusicManager(context)
    assertEquals(false, musicManager.isMuted())
    musicManager.setMuted(true)
    assertEquals(true, musicManager.isMuted())
  }
}

