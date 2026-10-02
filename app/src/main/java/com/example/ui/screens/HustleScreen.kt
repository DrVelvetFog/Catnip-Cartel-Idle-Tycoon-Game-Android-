package com.example.ui.screens

import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.CartelGameState
import com.example.data.FloatingParticle
import com.example.ui.components.formatNumber
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OnError
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SecondaryFixedDim
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SecondaryGoldContainer
import com.example.ui.theme.SirenRed
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random

@Composable
fun HustleScreen(
  state: CartelGameState,
  passiveRate: Double,
  tapPower: Long,
  onTapAvatar: () -> Unit,
  onZoomiesClick: () -> Unit,
  onAdDropClick: () -> Unit,
  onOverdriveClick: () -> Unit,
  onGoToRaid: () -> Unit,
  onBoomboxClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  // Tap scale bounce
  var tapScale by remember { mutableFloatStateOf(1f) }

  // Floating particles
  val particles = remember { mutableStateListOf<FloatingParticle>() }

  // Infinite pulsating animation for neon glow
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  // Street gossip rotator
  val gossipList = remember {
    listOf(
      "Alley Kittens harvested a fresh brick! Turf revenues surge +18%.",
      "Siamese Lookout spotted animal control cruiser near 5th Street!",
      "Maine Coon Muscle broke up a stray dog scuffle behind the fish market.",
      "Tabby Runner clocked 35mph in custom gold sneakers on Brooklyn Blvd.",
      "Persian Accountant laundered 50,000 NIP into luxury scratching towers."
    )
  }
  var gossipIndex by remember { mutableIntStateOf(0) }
  LaunchedEffect(Unit) {
    while (true) {
      delay(6000)
      gossipIndex = (gossipIndex + 1) % gossipList.size
    }
  }

  // Cleanup particles safely based on age
  LaunchedEffect(Unit) {
    while (true) {
      delay(200)
      if (particles.isNotEmpty()) {
        val now = SystemClock.uptimeMillis()
        particles.removeAll { now - it.id > 900 }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // EMERGENCY RAID BANNER (If Raid Active or Triggerable)
      if (state.isRaidActive && !state.raidFinished) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onGoToRaid() }
            .testTag("raid_alert_banner"),
          color = SirenRed,
          shape = RoundedCornerShape(8.dp),
          shadowElevation = 6.dp
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Alert",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "CRITICAL BUST IN PROGRESS!",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black
                ),
                color = Color.White
              )
            }
            Text(
              text = "DEFEND >>",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = Color.Yellow
            )
          }
        }
      }

      // TOP STATS HERO BENTO
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("top_stats_hero_bento"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Eco,
                  contentDescription = null,
                  tint = PrimaryNeon,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "STREET INVENTORY",
                  style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.height(2.dp))
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = formatNumber(state.nipBalance),
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 28.sp,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.testTag("catnip_counter_display")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "NIP",
                  fontFamily = FontFamily.SansSerif,
                  fontWeight = FontWeight.Black,
                  fontSize = 18.sp,
                  color = PrimaryNeon
                )
              }

              // Multiplier ticker indicators
              Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = SurfaceContainerLowest.copy(alpha = 0.8f)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Bolt,
                      contentDescription = null,
                      tint = SecondaryGold,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                      text = String.format(Locale.US, "+%,.1f/s", passiveRate),
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = SecondaryGold
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = SurfaceContainerLowest.copy(alpha = 0.8f)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.TouchApp,
                      contentDescription = null,
                      tint = PrimaryNeon,
                      modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                      text = "+$tapPower/tap",
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }
              }
            }

            // Midnight Zoomies Interactive Floating Frenzy Widget
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onZoomiesClick() }
                .testTag("zoomies_frenzy_button"),
              color = SurfaceContainerLowest,
              shape = RoundedCornerShape(12.dp),
              shadowElevation = 4.dp
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHighest),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Zoomies Frenzy",
                    tint = SecondaryGold,
                    modifier = Modifier.size(26.dp)
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "ZOOMIES",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = SecondaryGold,
                  fontSize = 10.sp
                )
                Text(
                  text = if (state.isZoomiesActive) "${state.zoomiesRemainingSeconds}s ACTIVE!" else "READY: ${state.zoomiesCharges}X",
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 9.sp,
                  color = PrimaryNeon
                )
              }
            }
          }

          // Passive Progress Bar Segment
          Spacer(modifier = Modifier.height(10.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(SurfaceContainerLowest)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(0.68f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(PrimaryNeon)
            )
          }
        }
      }

      // ACTIVE BOOMBOX BEAT BOOST INDICATOR BAR
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceContainerHigh,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceContainerLowest),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Speaker,
                contentDescription = "Speaker",
                tint = SecondaryGold,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "BOOMBOX BEAT BOOST",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                  color = SecondaryGold,
                  fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (state.isBoomboxBoostActive) SecondaryGold.copy(alpha = 0.2f) else PrimaryNeon.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = if (state.isBoomboxBoostActive) "2X ACTIVE" else "2X READY",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (state.isBoomboxBoostActive) SecondaryGold else PrimaryNeon,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Text(
                text = if (state.isBoomboxBoostActive) "Tape deck spinning 90s basslines" else "Drop beat for 30m 2x production boost",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            }
          }

          if (state.isBoomboxBoostActive) {
            // Timer pill
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = SurfaceContainerLowest
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Timer,
                  contentDescription = null,
                  tint = SecondaryGold,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                val hours = state.boomboxRemainingSeconds / 3600
                val mins = (state.boomboxRemainingSeconds % 3600) / 60
                val secs = state.boomboxRemainingSeconds % 60
                Text(
                  text = String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs),
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = SecondaryGold
                )
              }
            }
          } else {
            Button(
              onClick = onBoomboxClick,
              shape = RoundedCornerShape(6.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SecondaryGold),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("activate_boombox_button")
            ) {
              Text(
                text = "ACTIVATE",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = OnSecondary
              )
            }
          }
        }
      }

      // CENTER STAGE: MC WHISKERS TAP TARGET HERO
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(270.dp)
        ) {
          // Neon pulsing visualizer rings
          Box(
            modifier = Modifier
              .size(260.dp * pulseScale)
              .clip(CircleShape)
              .background(PrimaryNeon.copy(alpha = 0.08f))
          )
          Box(
            modifier = Modifier
              .size(240.dp)
              .clip(CircleShape)
              .background(SecondaryGold.copy(alpha = 0.06f))
          )

          // Main mascot circle
          Box(
            modifier = Modifier
              .size(228.dp)
              .scale(tapScale)
              .clip(CircleShape)
              .background(SurfaceContainerLowest)
              .border(
                width = 3.dp,
                brush = Brush.radialGradient(
                  colors = listOf(PrimaryNeon, SecondaryGold.copy(alpha = 0.5f), Color.Transparent)
                ),
                shape = CircleShape
              )
              .pointerInput(Unit) {
                detectTapGestures(
                  onPress = { offset ->
                    tapScale = 0.92f
                    onTapAvatar()
                    particles.add(
                      FloatingParticle(
                        id = SystemClock.uptimeMillis(),
                        text = "+$tapPower",
                        x = offset.x - 40,
                        y = offset.y - 120
                      )
                    )
                    tryAwaitRelease()
                    tapScale = 1f
                  }
                )
              }
              .testTag("mc_whiskers_tap_target"),
            contentAlignment = Alignment.Center
          ) {
            // Vinyl Groove Outer Layer
            Box(
              modifier = Modifier
                .size(216.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHigh),
              contentAlignment = Alignment.Center
            ) {
              AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAUdI4DX0Fgpcf17ipgusF0Dhj2MN75Jf0x4lmZVtslCgHPv2kk_0-0im9H2Ogf3Jn09TXasw_KbPqryuGAtIfNdtAnipqZMQVgFDkiX_Tm3FSyNVGM0WLLCMG48MlDVrcid_VKPXIkg23KLHGJ6mIvDfSnoHlPJbgC4nzBoqNzk_leKZrypxljrzFh6pdB684m8Wned8htxvWzKX8cMRyLbHpdXzVZ_IGlTrCMR8Ekk_PGdfCwnoXDdA",
                contentDescription = "MC Whiskers Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(208.dp)
                  .clip(CircleShape)
              )
            }

            // Top-left OG MC badge
            Surface(
              modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 10.dp, y = 10.dp),
              shape = RoundedCornerShape(12.dp),
              color = SecondaryGold,
              shadowElevation = 4.dp
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Headphones,
                  contentDescription = null,
                  tint = OnSecondary,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = "OG MC",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = OnSecondary,
                  fontSize = 9.sp
                )
              }
            }

            // Bottom-right TAP! badge
            Surface(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-8).dp),
              shape = RoundedCornerShape(14.dp),
              color = SurfaceContainerLowest,
              shadowElevation = 6.dp
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.LocalFireDepartment,
                  contentDescription = null,
                  tint = PrimaryNeon,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "TAP!",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                  ),
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 14.sp
                )
              }
            }
          }

          // Floating click particles
          particles.forEach { particle ->
            Text(
              text = particle.text,
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              color = PrimaryNeon,
              modifier = Modifier
                .offset { IntOffset(particle.x.toInt(), particle.y.toInt()) }
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(PrimaryNeon)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "TAP AVATAR TO SHAKE DOWN THE HOOD",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
          )
        }
      }

      // STREET GOSSIP TICKER BANNER
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            gossipIndex = (gossipIndex + 1) % gossipList.size
          }
          .testTag("street_gossip_ticker"),
        color = SurfaceContainerLow,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(PrimaryNeon.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Campaign,
              contentDescription = "Gossip",
              tint = PrimaryNeon,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "STREET GOSSIP: ",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
            color = SecondaryGold,
            fontSize = 11.sp
          )
          Text(
            text = gossipList[gossipIndex],
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            maxLines = 1,
            modifier = Modifier.weight(1f)
          )
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Next",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // QUICK ACTION / TACTILE BLING HUD FOOTER
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Ad Drop Dropbox Button
        Button(
          onClick = onAdDropClick,
          modifier = Modifier
            .weight(1f)
            .height(58.dp)
            .testTag("ad_drop_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.primary
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SecondaryGold),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = OnSecondary,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "AD DROP DROPBOX",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                color = SecondaryGold,
                fontSize = 10.sp
              )
              Text(
                text = "+50,000 NIP CRATE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            }
          }
        }

        // Super Frenzy Overdrive Trigger
        Button(
          onClick = onOverdriveClick,
          modifier = Modifier
            .weight(1f)
            .height(58.dp)
            .testTag("overdrive_frenzy_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SecondaryGold,
            contentColor = OnSecondary
          ),
          elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceContainerLowest),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = null,
                tint = SecondaryGold,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = if (state.isOverdriveActive) "${state.overdriveRemainingSeconds}s BEAT FRENZY" else "OVERDRIVE",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = OnSecondary,
                fontSize = 13.sp,
                lineHeight = 15.sp
              )
              Text(
                text = "30s BEAT FRENZY",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = OnSecondary.copy(alpha = 0.85f),
                fontSize = 9.sp
              )
            }
          }
        }
      }

      // Quick Alert Mini-Game Trigger (Raid Defense)
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onGoToRaid() }
          .testTag("test_raid_defense_btn"),
        color = SurfaceContainerLowest,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceContainerHighest)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocalPolice,
              contentDescription = null,
              tint = ErrorRed,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "TACTICAL DRILL: ANIMAL CONTROL RAID DEFENSE",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 10.sp
            )
          }
          Text(
            text = "PLAY DRILL",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
            color = PrimaryNeon,
            fontSize = 10.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
