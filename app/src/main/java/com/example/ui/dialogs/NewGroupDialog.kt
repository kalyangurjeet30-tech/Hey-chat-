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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ContactEntity
import com.example.ui.components.AvatarView
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTextPrimaryDark
import com.example.ui.theme.WhatsAppTextPrimaryLight
import com.example.ui.theme.WhatsAppTextSecondaryDark
import com.example.ui.theme.WhatsAppTextSecondaryLight
import com.example.ui.theme.WhatsAppTopBarDark
import com.example.ui.theme.WhatsAppTopBarLight

@Composable
fun NewGroupDialog(
  contacts: List<ContactEntity>,
  onCreateGroup: (String, List<Long>) -> Unit,
  onDismiss: () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  var groupName by remember { mutableStateOf("") }
  val selectedContactIds = remember { mutableStateListOf<Long>() }

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
      Box(modifier = Modifier.fillMaxSize()) {
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
                text = "New group",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${selectedContactIds.size} of ${contacts.size} selected",
                color = Color(0xCCFFFFFF),
                fontSize = 12.sp
              )
            }
          }

          // Group Name Input
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(WhatsAppEmerald),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Group,
                contentDescription = "Group icon",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
              value = groupName,
              onValueChange = { groupName = it },
              placeholder = { Text("Type group subject here...") },
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WhatsAppLightGreen,
                unfocusedBorderColor = WhatsAppLightGreen.copy(alpha = 0.5f)
              ),
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("group_name_input")
            )
          }

          // Horizontal Selected Contacts Chip List
          if (selectedContactIds.isNotEmpty()) {
            LazyRow(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(selectedContactIds.toList()) { id ->
                val contact = contacts.find { it.id == id }
                contact?.let { c ->
                  Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF)
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      AvatarView(
                        avatarType = c.avatarType,
                        name = c.name,
                        colorHex = c.colorHex,
                        size = 24.dp
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = c.name.split(" ").firstOrNull() ?: c.name,
                        fontSize = 13.sp
                      )
                      Spacer(modifier = Modifier.width(4.dp))
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier
                          .size(16.dp)
                          .clickable { selectedContactIds.remove(id) }
                      )
                    }
                  }
                }
              }
            }
          }

          // Contacts Checkbox List
          LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
              items = contacts,
              key = { it.id }
            ) { contact ->
              val isSelected = selectedContactIds.contains(contact.id)

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    if (isSelected) {
                      selectedContactIds.remove(contact.id)
                    } else {
                      selectedContactIds.add(contact.id)
                    }
                  }
                  .padding(horizontal = 16.dp, vertical = 8.dp),
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

                Checkbox(
                  checked = isSelected,
                  onCheckedChange = { checked ->
                    if (checked) {
                      selectedContactIds.add(contact.id)
                    } else {
                      selectedContactIds.remove(contact.id)
                    }
                  },
                  colors = CheckboxDefaults.colors(
                    checkedColor = WhatsAppLightGreen,
                    checkmarkColor = Color.White
                  )
                )
              }
            }

            item {
              Spacer(modifier = Modifier.height(80.dp))
            }
          }
        }

        // FAB to Confirm Group Creation
        if (groupName.isNotBlank() && selectedContactIds.isNotEmpty()) {
          FloatingActionButton(
            onClick = {
              onCreateGroup(groupName.trim(), selectedContactIds.toList())
            },
            containerColor = WhatsAppLightGreen,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(16.dp)
              .testTag("create_group_confirm_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Create Group",
              modifier = Modifier.size(26.dp)
            )
          }
        }
      }
    }
  }
}
