package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PrimaryNeon
import com.example.ui.theme.SurfaceContainerLowest

enum class CartelTab(val label: String, val icon: ImageVector) {
  HUSTLE("Hustle", Icons.Default.Pets),
  CREW("Crew", Icons.Default.Groups),
  TURF("Turf", Icons.Default.FormatPaint),
  LABS("Labs", Icons.Default.Science),
  EMPIRE("Empire", Icons.Default.EmojiEvents)
}

@Composable
fun CartelBottomNav(
  currentTab: CartelTab,
  onTabSelected: (CartelTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .testTag("cartel_bottom_nav"),
    color = SurfaceContainerLowest.copy(alpha = 0.98f),
    shadowElevation = 16.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(68.dp)
        .padding(horizontal = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      CartelTab.entries.forEach { tab ->
        val isSelected = currentTab == tab
        val interactionSource = remember { MutableInteractionSource() }

        Column(
          modifier = Modifier
            .weight(1f)
            .height(58.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
              interactionSource = interactionSource,
              indication = null
            ) { onTabSelected(tab) }
            .testTag("nav_tab_${tab.name.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = tab.icon,
            contentDescription = tab.label,
            tint = if (isSelected) PrimaryNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
          )
          Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
              letterSpacing = 0.8.sp
            ),
            color = if (isSelected) PrimaryNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 3.dp)
          )
        }
      }
    }
  }
}
