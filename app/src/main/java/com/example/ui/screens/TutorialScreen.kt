package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SirenRed
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerHighest
import com.example.ui.theme.SurfaceContainerLowest
import kotlinx.coroutines.launch

private const val APP_LOGO_URL =
  "https://lh3.googleusercontent.com/aida-public/AB6AXuBAyKogrEuUlTFpMAWkqNuOXvrKtgoWb07EHMOHBtH4Lnwax0ZvwiGSR0SGxS4ijM8YnYglYK4CKVHW53lQDvHuJd7z8NXpaMkmMAv70LApxOoYcgxlTvNP-OVC-uLfjt-9nK8o1UBWr0fr07_9h8WXTVs0OUYJ3kQCruNgPLLVw-2xCtRYxO-V59xGgsNMObHfp0MF6W-XeXxtVR7-5borLwAbA-zIPGFgGjFk2aGuQCTx8J4r1HW8KQ"

private data class TutorialPageData(
  val stepNumber: Int,
  val badgeText: String,
  val title: String,
  val body: String,
  val icon: ImageVector,
  val accentColor: Color,
  val tipHighlight: String
)

@Composable
fun TutorialScreen(
  onFinishTutorial: () -> Unit,
  modifier: Modifier = Modifier
) {
  val pages = listOf(
    TutorialPageData(
      stepNumber = 1,
      badgeText = "STEP 1 OF 5 • TAP & EARN",
      title = "WELCOME TO THE CARTEL",
      body = "You're the boss now. Tap MC Whiskers to stack catnip. Every tap counts.",
      icon = Icons.Default.TouchApp,
      accentColor = PrimaryNeon,
      tipHighlight = "TAP MC WHISKERS • EARN INSTANT NIP"
    ),
    TutorialPageData(
      stepNumber = 2,
      badgeText = "STEP 2 OF 5 • AUTOMATE REVENUE",
      title = "BUILD YOUR CREW",
      body = "Hire 6 operatives, from Alley Kitten to The Plug. They hustle for you 24/7, even while you sleep.",
      icon = Icons.Default.Groups,
      accentColor = SecondaryGold,
      tipHighlight = "6 SYNDICATE OPERATIVES • 24/7 PASSIVE INCOME"
    ),
    TutorialPageData(
      stepNumber = 3,
      badgeText = "STEP 3 OF 5 • EXPAND TURF",
      title = "CLAIM THE TURF",
      body = "Take over 6 territories, from The Alley to The Whole City. Each one multiplies ALL your earnings, up to x32.",
      icon = Icons.Default.LocationCity,
      accentColor = PrimaryNeon,
      tipHighlight = "6 TERRITORIES • UP TO x32 EARNINGS MULTIPLIER"
    ),
    TutorialPageData(
      stepNumber = 4,
      badgeText = "STEP 4 OF 5 • STASH THE CONTRABAND",
      title = "DODGE ANIMAL CONTROL",
      body = "Raids hit every 8-15 minutes. When the alarm sounds, stash your nip in all 5 hiding spots before time runs out. Win = +5% bonus. Get caught = lose 15%.",
      icon = Icons.Default.LocalPolice,
      accentColor = SirenRed,
      tipHighlight = "5 HIDING SPOTS • WIN +5% BONUS OR LOSE 15%"
    ),
    TutorialPageData(
      stepNumber = 5,
      badgeText = "STEP 5 OF 5 • PRESTIGE & DOMINATE",
      title = "EARN NINE LIVES",
      body = "Hit 1,000,000 lifetime nip to prestige. Each life gives a permanent +25% earnings boost, up to 9 lives. Now get out there and build the empire.",
      icon = Icons.Default.AutoAwesome,
      accentColor = SecondaryGold,
      tipHighlight = "1,000,000 NIP PRESTIGE • PERMANENT +25% PER LIFE (MAX 9)"
    )
  )

  val pagerState = rememberPagerState(pageCount = { pages.size })
  val coroutineScope = rememberCoroutineScope()
  val isLastPage = pagerState.currentPage == pages.size - 1

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            SurfaceContainerLowest,
            MaterialTheme.colorScheme.background,
            SurfaceContainerLowest
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("tutorial_screen")
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Bar: Step Indicator and Skip Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // App Identity Pill
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(start = 4.dp)
        ) {
          AsyncImage(
            model = APP_LOGO_URL,
            contentDescription = "Catnip Cartel Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .size(28.dp)
              .clip(RoundedCornerShape(6.dp))
              .border(1.dp, PrimaryNeon.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "CARTEL ORIENTATION",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 1.2.sp
            ),
            color = PrimaryNeon
          )
        }

        // Skip Button
        TextButton(
          onClick = onFinishTutorial,
          modifier = Modifier.testTag("tutorial_skip_button"),
          colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
          )
        ) {
          Text(
            text = "SKIP",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            )
          )
        }
      }

      // Main Swipeable Pager Content
      HorizontalPager(
        state = pagerState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .testTag("tutorial_pager")
      ) { pageIndex ->
        val page = pages[pageIndex]
        TutorialPageContent(page = page)
      }

      // Bottom Section: Pager Dots + Navigation Buttons
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Pager Dots Indicator
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.testTag("tutorial_pager_dots")
        ) {
          repeat(pages.size) { index ->
            val isSelected = pagerState.currentPage == index
            Box(
              modifier = Modifier
                .height(8.dp)
                .width(if (isSelected) 28.dp else 8.dp)
                .clip(CircleShape)
                .background(
                  if (isSelected) PrimaryNeon else SurfaceContainerHighest
                )
            )
          }
        }

        // Navigation Row: Back / Next or Start Hustlin'
        if (isLastPage) {
          // Last page: Big "START HUSTLIN'" Button
          Button(
            onClick = onFinishTutorial,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("tutorial_start_hustlin_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = PrimaryNeon,
              contentColor = OnPrimaryContainer
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
              tint = OnPrimaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "START HUSTLIN'",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
              )
            )
          }
        } else {
          // Normal pages: Back and Next buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Previous button (hidden on first page)
            if (pagerState.currentPage > 0) {
              OutlinedButton(
                onClick = {
                  coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                  }
                },
                modifier = Modifier
                  .height(46.dp)
                  .testTag("tutorial_back_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                  contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  SurfaceContainerHighest
                )
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Previous page",
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "BACK",
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
              }
            } else {
              Spacer(modifier = Modifier.width(1.dp))
            }

            // Next button
            Button(
              onClick = {
                coroutineScope.launch {
                  pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
              },
              modifier = Modifier
                .height(46.dp)
                .testTag("tutorial_next_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryNeon,
                contentColor = OnPrimaryContainer
              )
            ) {
              Text(
                text = "NEXT",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                )
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Next page",
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TutorialPageContent(page: TutorialPageData) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 24.dp, vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // App Logo Display at the top of each page
    Box(
      modifier = Modifier
        .size(90.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(SurfaceContainerLowest)
        .border(2.dp, page.accentColor.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
      contentAlignment = Alignment.Center
    ) {
      AsyncImage(
        model = APP_LOGO_URL,
        contentDescription = "Catnip Cartel",
        contentScale = ContentScale.Fit,
        modifier = Modifier
          .size(76.dp)
          .clip(RoundedCornerShape(14.dp))
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Step Badge
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = page.accentColor.copy(alpha = 0.15f),
      border = androidx.compose.foundation.BorderStroke(1.dp, page.accentColor.copy(alpha = 0.4f))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = page.icon,
          contentDescription = null,
          tint = page.accentColor,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = page.badgeText,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          ),
          color = page.accentColor,
          fontSize = 11.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Punchy Bold Title
    Text(
      text = page.title,
      style = MaterialTheme.typography.headlineMedium.copy(
        fontWeight = FontWeight.Black,
        letterSpacing = 0.8.sp
      ),
      color = MaterialTheme.colorScheme.primary,
      textAlign = TextAlign.Center,
      fontSize = 24.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Street-Cat Body Copy
    Text(
      text = page.body,
      style = MaterialTheme.typography.bodyLarge.copy(
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium
      ),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      fontSize = 15.sp,
      modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Syndicate Rule / Tip Highlight Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(
        containerColor = SurfaceContainerHigh.copy(alpha = 0.85f)
      ),
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        page.accentColor.copy(alpha = 0.25f)
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Bolt,
          contentDescription = null,
          tint = page.accentColor,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = page.tipHighlight,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          color = page.accentColor,
          fontSize = 11.sp,
          textAlign = TextAlign.Center
        )
      }
    }
  }
}
