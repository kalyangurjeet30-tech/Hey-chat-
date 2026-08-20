package com.example.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.UserProfile
import com.example.ui.ThemePreference
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTextPrimaryDark
import com.example.ui.theme.WhatsAppTextPrimaryLight
import com.example.ui.theme.WhatsAppTextSecondaryDark
import com.example.ui.theme.WhatsAppTextSecondaryLight
import com.example.ui.theme.WhatsAppTopBarDark
import com.example.ui.theme.WhatsAppTopBarLight

@Composable
fun SettingsDialog(
  userProfile: UserProfile,
  themePreference: ThemePreference,
  onThemeChange: (ThemePreference) -> Unit,
  onUpdateProfile: (String, String, Long) -> Unit,
  onLogout: () -> Unit,
  onDismiss: () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  var showThemePicker by remember { mutableStateOf(false) }
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showLogoutConfirmDialog by remember { mutableStateOf(false) }

  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = if (isDark) Color(0xFF0B141A) else Color(0xFFFFFFFF)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(if (isDark) WhatsAppTopBarDark else WhatsAppTopBarLight)
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Text(
            text = "Settings",
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 12.dp)
          )
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
          // User Profile Card
          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showEditProfileDialog = true }
                .padding(horizontal = 16.dp, vertical = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(Color(userProfile.avatarColorHex)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = userProfile.displayName.take(2).uppercase(),
                  color = Color.White,
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.width(16.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = userProfile.displayName,
                  color = primaryColor,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (userProfile.phoneNumber.isNotBlank()) userProfile.phoneNumber else "No phone set",
                  color = WhatsAppLightGreen,
                  fontSize = 13.5.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = userProfile.statusAbout,
                  color = secondaryColor,
                  fontSize = 13.sp,
                  maxLines = 1
                )
              }

              IconButton(onClick = { showEditProfileDialog = true }) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = "Edit profile",
                  tint = WhatsAppGreen,
                  modifier = Modifier.size(22.dp)
                )
              }
            }

            HorizontalDivider(
              color = if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF)
            )
          }

          // Theme Settings Item
          item {
            SettingsItemRow(
              icon = Icons.Default.DarkMode,
              title = "Theme",
              subtitle = when (themePreference) {
                ThemePreference.SYSTEM -> "System default"
                ThemePreference.LIGHT -> "Light"
                ThemePreference.DARK -> "Dark"
              },
              onClick = { showThemePicker = true },
              isDark = isDark
            )
          }

          item {
            SettingsItemRow(
              icon = Icons.Default.Key,
              title = "Account",
              subtitle = "Security notifications, change number",
              onClick = { showEditProfileDialog = true },
              isDark = isDark
            )
          }

          item {
            SettingsItemRow(
              icon = Icons.Default.Lock,
              title = "Privacy",
              subtitle = "Block contacts, disappearing messages",
              onClick = { },
              isDark = isDark
            )
          }

          item {
            SettingsItemRow(
              icon = Icons.Default.Chat,
              title = "Chats",
              subtitle = "Theme, wallpapers, chat history",
              onClick = { },
              isDark = isDark
            )
          }

          item {
            SettingsItemRow(
              icon = Icons.Default.Notifications,
              title = "Notifications",
              subtitle = "Message, group & call tones",
              onClick = { },
              isDark = isDark
            )
          }

          item {
            SettingsItemRow(
              icon = Icons.Default.Storage,
              title = "Storage and data",
              subtitle = "Network usage, auto-download",
              onClick = { },
              isDark = isDark
            )
          }

          item {
            SettingsItemRow(
              icon = Icons.Default.Language,
              title = "App language",
              subtitle = "English (device's language)",
              onClick = { },
              isDark = isDark
            )
          }

          item {
            SettingsItemRow(
              icon = Icons.Default.Help,
              title = "Help",
              subtitle = "Help center, contact us, privacy policy",
              onClick = { },
              isDark = isDark
            )
          }

          item {
            HorizontalDivider(
              color = if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF),
              modifier = Modifier.padding(vertical = 8.dp)
            )
          }

          // Log Out / Change Phone Number
          item {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { showLogoutConfirmDialog = true }
                .padding(horizontal = 20.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Log out",
                tint = Color(0xFFE53935),
                modifier = Modifier.size(24.dp)
              )

              Spacer(modifier = Modifier.width(20.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Log out / Change phone number",
                  color = Color(0xFFE53935),
                  fontSize = 16.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Logged in as ${userProfile.phoneNumber}",
                  color = secondaryColor,
                  fontSize = 13.sp
                )
              }
            }
          }

          // Hey Chat Branding Footer with Logo
          item {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_hey_chat_logo),
                contentDescription = "Hey Chat",
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Hey Chat",
                color = primaryColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "v2.24.18 • End-to-end encrypted",
                color = secondaryColor,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    var editName by remember { mutableStateOf(userProfile.displayName) }
    var editAbout by remember { mutableStateOf(userProfile.statusAbout) }
    val avatarColors = listOf(
      0xFF008069,
      0xFF00A884,
      0xFF128C7E,
      0xFF25D366,
      0xFF3949AB,
      0xFF8E24AA,
      0xFFE53935,
      0xFFFB8C00
    )
    var selectedColorIndex by remember {
      val idx = avatarColors.indexOf(userProfile.avatarColorHex)
      mutableIntStateOf(if (idx >= 0) idx else 0)
    }

    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit profile", fontWeight = FontWeight.Bold) },
      text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(Color(avatarColors[selectedColorIndex])),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (editName.isNotBlank()) editName.take(2).uppercase() else "HC",
              color = Color.White,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Colors
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 16.dp)
          ) {
            avatarColors.forEachIndexed { index, col ->
              val isSel = index == selectedColorIndex
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(Color(col))
                  .clickable { selectedColorIndex = index }
                  .border(
                    width = if (isSel) 2.dp else 0.dp,
                    color = if (isSel) primaryColor else Color.Transparent,
                    shape = CircleShape
                  ),
                contentAlignment = Alignment.Center
              ) {
                if (isSel) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                  )
                }
              }
            }
          }

          OutlinedTextField(
            value = editName,
            onValueChange = { if (it.length <= 25) editName = it },
            label = { Text("Your name") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WhatsAppLightGreen),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = editAbout,
            onValueChange = { editAbout = it },
            label = { Text("About") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WhatsAppLightGreen),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Phone: ${userProfile.phoneNumber}",
            color = secondaryColor,
            fontSize = 12.5.sp,
            modifier = Modifier.align(Alignment.Start)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateProfile(
              if (editName.isBlank()) "Hey Chat User" else editName.trim(),
              editAbout.trim(),
              avatarColors[selectedColorIndex]
            )
            showEditProfileDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppLightGreen)
        ) {
          Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel", color = secondaryColor)
        }
      }
    )
  }

  // Logout Confirm Dialog
  if (showLogoutConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirmDialog = false },
      title = { Text("Log out of Hey Chat?") },
      text = {
        Text(
          text = "You will need to verify your phone number (${userProfile.phoneNumber}) again to log back in.",
          color = secondaryColor,
          fontSize = 14.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirmDialog = false
            onLogout()
            onDismiss()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
        ) {
          Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirmDialog = false }) {
          Text("Cancel", color = secondaryColor)
        }
      }
    )
  }

  // Theme Picker Modal
  if (showThemePicker) {
    AlertDialog(
      onDismissRequest = { showThemePicker = false },
      title = { Text("Choose theme") },
      text = {
        Column {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onThemeChange(ThemePreference.SYSTEM)
                showThemePicker = false
              }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = themePreference == ThemePreference.SYSTEM,
              onClick = {
                onThemeChange(ThemePreference.SYSTEM)
                showThemePicker = false
              },
              colors = RadioButtonDefaults.colors(selectedColor = WhatsAppGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("System default")
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onThemeChange(ThemePreference.LIGHT)
                showThemePicker = false
              }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = themePreference == ThemePreference.LIGHT,
              onClick = {
                onThemeChange(ThemePreference.LIGHT)
                showThemePicker = false
              },
              colors = RadioButtonDefaults.colors(selectedColor = WhatsAppGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Light")
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onThemeChange(ThemePreference.DARK)
                showThemePicker = false
              }
              .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = themePreference == ThemePreference.DARK,
              onClick = {
                onThemeChange(ThemePreference.DARK)
                showThemePicker = false
              },
              colors = RadioButtonDefaults.colors(selectedColor = WhatsAppGreen)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dark")
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showThemePicker = false }) {
          Text("Cancel", color = WhatsAppGreen)
        }
      }
    )
  }
}

@Composable
private fun SettingsItemRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  isDark: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 20.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = title,
      tint = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight,
      modifier = Modifier.size(24.dp)
    )

    Spacer(modifier = Modifier.width(20.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        color = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = subtitle,
        color = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight,
        fontSize = 13.5.sp
      )
    }
  }
}

