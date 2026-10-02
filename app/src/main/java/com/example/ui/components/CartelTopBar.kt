package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest
import java.util.Locale

@Composable
fun CartelTopBar(
  currentScreenName: String,
  nipBalance: Double,
  passiveRate: Double,
  bossLevel: Int,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val formattedBalance = formatNumber(nipBalance)
  val formattedRate = String.format(Locale.US, "+%.1f/s", passiveRate)

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .testTag("cartel_top_bar"),
    color = SurfaceContainerLowest.copy(alpha = 0.95f),
    shadowElevation = 8.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(72.dp)
        .padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Logo and Title
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onProfileClick() }
      ) {
        AsyncImage(
          model = "https://lh3.googleusercontent.com/aida-public/AB6AXuBAyKogrEuUlTFpMAWkqNuOXvrKtgoWb07EHMOHBtH4Lnwax0ZvwiGSR0SGxS4ijM8YnYglYK4CKVHW53lQDvHuJd7z8NXpaMkmMAv70LApxOoYcgxlTvNP-OVC-uLfjt-9nK8o1UBWr0fr07_9h8WXTVs0OUYJ3kQCruNgPLLVw-2xCtRYxO-V59xGgsNMObHfp0MF6W-XeXxtVR7-5borLwAbA-zIPGFgGjFk2aGuQCTx8J4r1HW8KQ",
          contentDescription = "Catnip Cartel Logo",
          contentScale = ContentScale.Fit,
          modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Text(
            text = "CARTEL",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 18.sp
          )
          Text(
            text = currentScreenName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.5.sp
            ),
            color = PrimaryNeon,
            lineHeight = 12.sp
          )
        }
      }

      // Center: NIP balance ticker pill
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainerHigh.copy(alpha = 0.95f),
        shadowElevation = 4.dp
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(PrimaryNeon),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Eco,
              contentDescription = "Catnip",
              tint = OnPrimaryContainer,
              modifier = Modifier.size(16.dp)
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Column(modifier = Modifier.padding(end = 4.dp)) {
            Text(
              text = "$formattedBalance NIP",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.primary,
              lineHeight = 15.sp
            )
            Text(
              text = formattedRate,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = PrimaryNeon,
              lineHeight = 11.sp
            )
          }
        }
      }

      // Right: OG Boss Level and Profile
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onProfileClick() }
      ) {
        Column(
          horizontalAlignment = Alignment.End,
          modifier = Modifier.padding(end = 6.dp)
        ) {
          Text(
            text = "OG BOSS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 0.5.sp
            ),
            color = SecondaryGold,
            lineHeight = 12.sp
          )
          Text(
            text = "LVL $bossLevel",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 12.sp
          )
        }
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

fun formatNumber(num: Double): String {
  return when {
    num >= 1_000_000_000 -> String.format(Locale.US, "%.2fB", num / 1_000_000_000.0)
    num >= 1_000_000 -> String.format(Locale.US, "%,.0f", num) // e.g. 1,285,284
    num >= 10_000 -> String.format(Locale.US, "%,.0f", num)
    else -> String.format(Locale.US, "%,.0f", num)
  }
}

fun formatShortNumber(num: Long): String {
  return when {
    num >= 1_000_000 -> String.format(Locale.US, "%.1fM", num / 1_000_000.0)
    num >= 1_000 -> String.format(Locale.US, "%.1fK", num / 1_000.0)
    else -> num.toString()
  }
}
