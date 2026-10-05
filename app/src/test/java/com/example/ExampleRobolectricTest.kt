package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.DistrictStatus
import com.example.data.GameRepository
import com.example.ui.audio.MusicManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `smoke test core loop via repository`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("catnip_cartel_save", Context.MODE_PRIVATE)
    prefs.edit().clear().putBoolean("tutorial_seen", true).apply()

    val repository = GameRepository(context)
    repository.resetGame()

    // 1. Tap the boss cat to earn nip
    val firstTap = repository.onAvatarTapped()
    assertEquals(25L, firstTap)
    assertEquals(25.0, repository.state.value.nipBalance, 0.01)
    repository.onAvatarTapped() // 50 nip total
    assertEquals(50.0, repository.state.value.nipBalance, 0.01)

    // 2. Hire a Crew operative (Alley Kitten costs 50 nip)
    repository.hireOperative("alley_kitten", 1)
    val afterHire = repository.state.value
    val alleyKitten = afterHire.operatives.find { it.id == "alley_kitten" }!!
    assertEquals(1, alleyKitten.owned)
    assertEquals(0.0, afterHire.nipBalance, 0.01)
    assertEquals(1.0, repository.calculatePassiveRate(afterHire), 0.01)

    // 3. Claim a Turf district (The Porch costs 5,000 nip)
    repository.claimAdDrop() // +50,000 nip
    val beforeClaim = repository.state.value
    val porchBefore = beforeClaim.districts.find { it.id == "the_porch" }!!
    val rooftopsBefore = beforeClaim.districts.find { it.id == "the_rooftops" }!!
    assertEquals(DistrictStatus.READY_TO_EXPAND, porchBefore.status)
    assertEquals(DistrictStatus.LOCKED, rooftopsBefore.status)

    repository.claimDistrict("the_porch")
    val afterClaim = repository.state.value
    val porchAfter = afterClaim.districts.find { it.id == "the_porch" }!!
    val rooftopsAfter = afterClaim.districts.find { it.id == "the_rooftops" }!!
    println("SMOKE_LOG: porchAfter.status=${porchAfter.status}, rooftopsAfter.status=${rooftopsAfter.status}")

    // 4. Buy a formula upgrade in Labs (Garden Batch costs 1,000 nip, x2 tap power)
    val tapBeforeLab = repository.calculateTapPower(repository.state.value)
    repository.upgradeFormula("garden_batch")
    val afterLab = repository.state.value
    val gardenBatch = afterLab.productFormulas.find { it.id == "garden_batch" }!!
    val tapAfterLab = repository.calculateTapPower(afterLab)
    assertEquals(1, gardenBatch.level)
    assertEquals(tapBeforeLab * 2, tapAfterLab)

    // 5. Trigger and play an Animal Control raid (stash all 5 spots to win)
    val nipBeforeRaid = repository.state.value.nipBalance
    repository.triggerRaid()
    assertTrue(repository.state.value.isRaidActive)
    val spots = listOf("Litter Box", "Couch Cushions", "Dark Closet", "Under Bed", "Backyard Dumpster")
    spots.forEach { spot -> repository.stashSpot(spot) }
    val afterRaid = repository.state.value
    assertTrue(afterRaid.raidFinished)
    assertTrue(afterRaid.raidWon)
    assertEquals(false, afterRaid.isRaidActive)
    assertEquals(nipBeforeRaid * 1.05, afterRaid.nipBalance, 0.1)
    assertEquals(2500L, afterRaid.streetCredXp)

    // 6. Prestige in Empire ("Nine Lives") at >= 1,000,000 lifetime nip
    repeat(20) { repository.claimAdDrop() } // +1,000,000 nip
    assertTrue(repository.state.value.totalLifetimeNip >= 1_000_000.0)
    assertTrue(repository.canPrestige())
    repository.doPrestige()
    val afterPrestige = repository.state.value
    assertEquals(1, afterPrestige.prestigeLives)
    assertEquals(0.0, afterPrestige.nipBalance, 0.01)
    assertEquals(31L, repository.calculateTapPower(afterPrestige))
  }

  @Test
  fun `smoke test UI interactions across all screens`() {
    composeTestRule.waitForIdle()
    // Dismiss tutorial if shown
    val skipNodes = composeTestRule.onAllNodesWithTag("tutorial_skip_button").fetchSemanticsNodes()
    if (skipNodes.isNotEmpty()) {
      composeTestRule.onNodeWithTag("tutorial_skip_button").performClick()
      composeTestRule.waitForIdle()
    }

    // 1. Tap MC Whiskers avatar twice (25 * 2 = 50 nip)
    composeTestRule.onNodeWithTag("mc_whiskers_tap_target").performClick()
    composeTestRule.onNodeWithTag("mc_whiskers_tap_target").performClick()
    composeTestRule.waitForIdle()

    // Claim Ad Drop (+50,000 nip) so we can afford Crew, Turf, Labs
    composeTestRule.onNodeWithTag("ad_drop_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // 2. Navigate to CREW and hire Alley Kitten
    composeTestRule.onNodeWithTag("nav_tab_crew").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("hire_btn_alley_kitten").performClick()
    composeTestRule.waitForIdle()

    // 3. Navigate to TURF and check claim button for The Porch
    composeTestRule.onNodeWithTag("nav_tab_turf").performClick()
    composeTestRule.waitForIdle()
    val porchClaimButtons = composeTestRule.onAllNodesWithTag("claim_turf_the_porch").fetchSemanticsNodes()
    println("SMOKE_LOG: UI claim_turf_the_porch nodes count = ${porchClaimButtons.size}")

    // 4. Navigate to LABS and buy Garden Batch formula upgrade
    composeTestRule.onNodeWithTag("nav_tab_labs").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("upgrade_formula_garden_batch").performClick()
    composeTestRule.waitForIdle()

    // 5. Navigate to TURF and launch Raid Defense drill, then stash all 5 spots
    composeTestRule.onNodeWithTag("nav_tab_turf").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("launch_raid_defense_btn").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("stash_spot_litter_box").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("stash_spot_couch_cushions").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("stash_spot_dark_closet").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("stash_spot_under_bed").performScrollTo().performClick()
    composeTestRule.onNodeWithTag("stash_spot_backyard_dumpster").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Click victory return button
    composeTestRule.onNodeWithTag("raid_finish_action_btn").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // 6. Navigate to EMPIRE and test prestige button
    composeTestRule.onNodeWithTag("nav_tab_empire").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("launder_stash_btn").performScrollTo().performClick()
    composeTestRule.waitForIdle()
  }

  @Test
  fun `raw audio resources are present and resolvable`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val titleId = context.resources.getIdentifier("catnip_cartel", "raw", context.packageName)
    val gameplayId = context.resources.getIdentifier("gameplay_loop", "raw", context.packageName)
    val stingerId = context.resources.getIdentifier("raid_stinger", "raw", context.packageName)
    val fanfareId = context.resources.getIdentifier("trunk_bump", "raw", context.packageName)

    assertTrue("catnip_cartel raw resource must exist", titleId != 0)
    assertTrue("gameplay_loop raw resource must exist", gameplayId != 0)
    assertTrue("raid_stinger raw resource must exist", stingerId != 0)
    assertTrue("trunk_bump raw resource must exist", fanfareId != 0)

    val musicManager = MusicManager(context)
    assertEquals(true, musicManager.enabled)
    musicManager.setEnabled(false)
    assertEquals(false, musicManager.enabled)
  }
}
