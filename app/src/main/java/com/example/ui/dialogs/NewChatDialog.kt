package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ContactEntity
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTextPrimaryDark
import com.example.ui.theme.WhatsAppTextPrimaryLight
import com.example.ui.theme.WhatsAppTextSecondaryDark
import com.example.ui.theme.WhatsAppTextSecondaryLight
import com.example.ui.theme.WhatsAppTopBarDark
import com.example.ui.theme.WhatsAppTopBarLight

@Composable
fun NewChatDialog(
  contacts: List<ContactEntity>,
  onContactClick: (ContactEntity) -> Unit,
  onNewGroupClick: () -> Unit,
  onDismiss: () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  var searchQuery by remember { mutableStateOf("") }

  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight

  val filteredContacts = remember(contacts, searchQuery) {
    if (searchQuery.isBlank()) contacts else {
      contacts.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
            it.phoneNumber.contains(searchQuery)
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialThemeColor(isDark)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
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

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Select contact",
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${contacts.size} contacts",
              color = Color(0xCCFFFFFF),
              fontSize = 12.sp
            )
          }
        }

        // Search bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search contact name or number...") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = secondaryColor
            )
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WhatsAppLightGreen,
            unfocusedBorderColor = Color(0x33888888)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("search_contacts_field")
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
          // Action 1: New group
          item {
            NewContactActionRow(
              icon = Icons.Default.GroupAdd,
              title = "New group",
              onClick = onNewGroupClick,
              isDark = isDark
            )
          }

          // Action 2: New contact
          item {
            NewContactActionRow(
              icon = Icons.Default.PersonAdd,
              title = "New contact",
              onClick = { },
              isDark = isDark
            )
          }

          // Action 3: New community
          item {
            NewContactActionRow(
              icon = Icons.Default.QrCode,
              title = "New community",
              onClick = { },
              isDark = isDark
            )
          }

          item {
            Text(
              text = "Contacts on Hey Chat",
              color = secondaryColor,
              fontSize = 13.5.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
          }

          items(
            items = filteredContacts,
            key = { it.id }
          ) { contact ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onContactClick(contact) }
                .padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              AvatarView(
                avatarType = contact.avatarType,
                name = contact.name,
                colorHex = contact.colorHex,
                size = 46.dp
              )

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = contact.name,
                  color = primaryColor,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = contact.statusAbout,
                  color = secondaryColor,
                  fontSize = 13.sp,
                  maxLines = 1
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun NewContactActionRow(
  icon: ImageVector,
  title: String,
  onClick: () -> Unit,
  isDark: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .background(WhatsAppLightGreen, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = Color.White,
        modifier = Modifier.size(22.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Text(
      text = title,
      color = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight,
      fontSize = 16.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
private fun MaterialThemeColor(isDark: Boolean): Color {
  return if (isDark) Color(0xFF0B141A) else Color(0xFFFFFFFF)
}
