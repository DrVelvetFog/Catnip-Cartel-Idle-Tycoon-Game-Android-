package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.CartelGameState
import com.example.ui.theme.OnPrimaryContainer
import com.example.ui.theme.OnSecondary
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SecondaryGold
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest

@Composable
fun BossProfileDialog(
  state: CartelGameState,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("boss_profile_dialog"),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
      elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = SecondaryGold
          ) {
            Text(
              text = "SYNDICATE DOSSIER",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = OnSecondary,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Box(
          modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(SurfaceContainerLowest)
        ) {
          AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAUdI4DX0Fgpcf17ipgusF0Dhj2MN75Jf0x4lmZVtslCgHPv2kk_0-0im9H2Ogf3Jn09TXasw_KbPqryuGAtIfNdtAnipqZMQVgFDkiX_Tm3FSyNVGM0WLLCMG48MlDVrcid_VKPXIkg23KLHGJ6mIvDfSnoHlPJbgC4nzBoqNzk_leKZrypxljrzFh6pdB684m8Wned8htxvWzKX8cMRyLbHpdXzVZ_IGlTrCMR8Ekk_PGdfCwnoXDdA",
            contentDescription = "MC Whiskers",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "MC WHISKERS",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "OG Boss • Level ${state.bossLevel}",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = SecondaryGold,
            fontSize = 12.sp
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = SurfaceContainerLowest,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Street Cred XP",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${state.streetCredXp} XP",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = SecondaryGold
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Lifetime Nip",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${formatNumber(state.totalLifetimeNip)} NIP",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = PrimaryNeon
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Operatives in Cartel",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${state.operatives.sumOf { it.owned }} Cats",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = SurfaceContainer,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "\"Keep the beats bumping and the catnip drying. If Animal Control comes knocking, stash the stash!\"",
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryNeon,
            fontSize = 11.sp,
            modifier = Modifier.padding(10.dp)
          )
        }

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryNeon,
            contentColor = OnPrimaryContainer
          )
        ) {
          Text(
            text = "BACK TO HUSTLE",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
          )
        }
      }
    }
  }
}
