package com.example

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.GameRepository
import com.example.ui.components.BossProfileDialog
import com.example.ui.components.CartelBottomNav
import com.example.ui.components.CartelTab
import com.example.ui.components.CartelTopBar
import com.example.ui.screens.CrewScreen
import com.example.ui.screens.EmpireScreen
import com.example.ui.screens.HustleScreen
import com.example.ui.screens.LabsScreen
import com.example.ui.screens.RaidDefenseScreen
import com.example.ui.screens.TurfScreen
import com.example.ui.screens.TutorialScreen
import com.example.ui.theme.CatnipCartelTheme
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SurfaceContainerLowest
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
  private lateinit var repository: GameRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    repository = GameRepository(applicationContext)

    setContent {
      CatnipCartelTheme {
        CatnipCartelApp(repository = repository)
      }
    }
  }
}

@Composable
fun CatnipCartelApp(repository: GameRepository) {
  val context = LocalContext.current
  val state by repository.state.collectAsStateWithLifecycle()

  val prefs = remember { context.getSharedPreferences("catnip_cartel_save", Context.MODE_PRIVATE) }
  var showTutorial by remember { mutableStateOf(!prefs.getBoolean("tutorial_seen", false)) }

  var currentTab by remember { mutableStateOf(CartelTab.HUSTLE) }
  var isRaidScreenOpen by remember { mutableStateOf(false) }
  var isProfileDialogOpen by remember { mutableStateOf(false) }

  val passiveRate = remember(state) { repository.calculatePassiveRate(state) }
  val tapPower = remember(state) { repository.calculateTapPower(state) }

  // Haptic feedback helper
  fun triggerHaptic() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        @Suppress("DEPRECATION")
        vibrator?.vibrate(25)
      }
    } catch (_: Exception) {}
  }

  // Auto-dismiss toast
  LaunchedEffect(state.toastMessage) {
    if (state.toastMessage != null) {
      delay(2000)
      repository.clearToast()
    }
  }

  // Handle back press if inside subscreen or tutorial
  BackHandler(enabled = showTutorial || isRaidScreenOpen || isProfileDialogOpen) {
    if (showTutorial) {
      prefs.edit().putBoolean("tutorial_seen", true).apply()
      showTutorial = false
    } else if (isProfileDialogOpen) {
      isProfileDialogOpen = false
    } else if (isRaidScreenOpen) {
      isRaidScreenOpen = false
      repository.dismissRaid()
    }
  }

  if (showTutorial) {
    TutorialScreen(
      onFinishTutorial = {
        triggerHaptic()
        prefs.edit().putBoolean("tutorial_seen", true).apply()
        showTutorial = false
      }
    )
  } else {
    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        if (!isRaidScreenOpen) {
          CartelTopBar(
            currentScreenName = currentTab.label,
            nipBalance = state.nipBalance,
            passiveRate = passiveRate,
            bossLevel = state.bossLevel,
            onProfileClick = { isProfileDialogOpen = true }
          )
        }
      },
      bottomBar = {
        if (!isRaidScreenOpen) {
          CartelBottomNav(
            currentTab = currentTab,
            onTabSelected = { tab ->
              triggerHaptic()
              currentTab = tab
            }
          )
        }
      },
      containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        if (isRaidScreenOpen || state.isRaidActive) {
          RaidDefenseScreen(
            state = state,
            onStashSpot = { spot ->
              triggerHaptic()
              repository.stashSpot(spot)
            },
            onBack = {
              isRaidScreenOpen = false
              repository.dismissRaid()
            },
            onRetry = {
              repository.triggerRaid()
            }
          )
        } else {
          when (currentTab) {
            CartelTab.HUSTLE -> {
              HustleScreen(
                state = state,
                passiveRate = passiveRate,
                tapPower = tapPower,
                onTapAvatar = {
                  triggerHaptic()
                  repository.onAvatarTapped()
                },
                onZoomiesClick = {
                  triggerHaptic()
                  repository.triggerZoomies()
                },
                onAdDropClick = {
                  triggerHaptic()
                  repository.claimAdDrop()
                },
                onOverdriveClick = {
                  triggerHaptic()
                  repository.triggerOverdrive()
                },
                onGoToRaid = {
                  repository.triggerRaid()
                  isRaidScreenOpen = true
                },
                onBoomboxClick = {
                  triggerHaptic()
                  repository.activateBoomboxBoost()
                }
              )
            }
            CartelTab.CREW -> {
              CrewScreen(
                state = state,
                passiveRate = passiveRate,
                onHire = { opId, amount ->
                  triggerHaptic()
                  repository.hireOperative(opId, amount)
                },
                onUnlockContract = { opId ->
                  triggerHaptic()
                  repository.unlockContract(opId)
                },
                onSelectMultiplier = { mult ->
                  triggerHaptic()
                  repository.setBuyMultiplier(mult)
                }
              )
            }
            CartelTab.TURF -> {
              TurfScreen(
                state = state,
                onClaimDistrict = { districtId ->
                  triggerHaptic()
                  repository.claimDistrict(districtId)
                },
                onLaunchRaidDefense = {
                  repository.triggerRaid()
                  isRaidScreenOpen = true
                }
              )
            }
            CartelTab.LABS -> {
              LabsScreen(
                state = state,
                tapPower = tapPower,
                onUpgradeFormula = { formulaId ->
                  triggerHaptic()
                  repository.upgradeFormula(formulaId)
                },
                onInstallTech = { techId ->
                  triggerHaptic()
                  repository.installTech(techId)
                }
              )
            }
            CartelTab.EMPIRE -> {
              EmpireScreen(
                state = state,
                passiveRate = passiveRate,
                tapPower = tapPower,
                onPrestigeLaunder = {
                  triggerHaptic()
                  repository.claimAdDrop()
                },
                onResetGame = {
                  triggerHaptic()
                  repository.resetGame()
                  showTutorial = true
                }
              )
            }
          }
        }
      }
    }

    // Top floating toast notification
    AnimatedVisibility(
      visible = state.toastMessage != null,
      enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
      exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 80.dp)
    ) {
      val toast = state.toastMessage
      if (toast != null) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = SurfaceContainerLowest,
          shadowElevation = 8.dp,
          modifier = Modifier.padding(horizontal = 16.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = null,
              tint = PrimaryNeon,
              modifier = Modifier.padding(end = 6.dp)
            )
            Text(
              text = toast,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
              color = MaterialTheme.colorScheme.primary,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    // Boss Profile Modal
    if (isProfileDialogOpen) {
      BossProfileDialog(
        state = state,
        onDismiss = { isProfileDialogOpen = false },
        onHowToPlay = {
          isProfileDialogOpen = false
          showTutorial = true
        }
      )
    }
  }
}
}
