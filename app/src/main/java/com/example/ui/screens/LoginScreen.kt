package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CountryCode
import com.example.data.POPULAR_COUNTRIES
import com.example.data.UserProfile
import com.example.ui.theme.WhatsAppEmerald
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppLightGreen
import com.example.ui.theme.WhatsAppTextPrimaryDark
import com.example.ui.theme.WhatsAppTextPrimaryLight
import com.example.ui.theme.WhatsAppTextSecondaryDark
import com.example.ui.theme.WhatsAppTextSecondaryLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class LoginStep {
  WELCOME,
  PHONE_INPUT,
  OTP_VERIFY,
  PROFILE_SETUP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  onLoginCompleted: (UserProfile) -> Unit,
  modifier: Modifier = Modifier
) {
  val isDark = isSystemInDarkTheme()
  val primaryColor = if (isDark) WhatsAppTextPrimaryDark else WhatsAppTextPrimaryLight
  val secondaryColor = if (isDark) WhatsAppTextSecondaryDark else WhatsAppTextSecondaryLight
  val backgroundColor = if (isDark) Color(0xFF0B141A) else Color(0xFFFFFFFF)

  var currentStep by remember { mutableStateOf(LoginStep.WELCOME) }

  // Phone number step state
  var selectedCountry by remember { mutableStateOf(POPULAR_COUNTRIES[0]) } // US default
  var showCountryPicker by remember { mutableStateOf(false) }
  var rawPhoneNumber by remember { mutableStateOf("") }
  var showConfirmPhoneDialog by remember { mutableStateOf(false) }

  // OTP step state
  var otpCode by remember { mutableStateOf("") }
  var generatedOtp by remember { mutableStateOf("482910") }
  var otpCountdown by remember { mutableIntStateOf(45) }
  var isOtpError by remember { mutableStateOf(false) }
  var showSimulatedSmsBanner by remember { mutableStateOf(false) }

  // Profile setup step state
  var displayName by remember { mutableStateOf("") }
  var statusAbout by remember { mutableStateOf("Hey there! I am using Hey Chat.") }
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
  var selectedColorIndex by remember { mutableIntStateOf(0) }
  var isFinishingSetup by remember { mutableStateOf(false) }

  // OTP countdown timer
  LaunchedEffect(currentStep, otpCountdown) {
    if (currentStep == LoginStep.OTP_VERIFY && otpCountdown > 0) {
      delay(1000)
      otpCountdown--
    }
  }

  // Show simulated incoming SMS banner when arriving at OTP screen
  LaunchedEffect(currentStep) {
    if (currentStep == LoginStep.OTP_VERIFY) {
      delay(800)
      showSimulatedSmsBanner = true
    } else {
      showSimulatedSmsBanner = false
    }
  }

  Surface(
    modifier = modifier.fillMaxSize(),
    color = backgroundColor
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      AnimatedContent(
        targetState = currentStep,
        transitionSpec = {
          slideInHorizontally { width -> width } + fadeIn() togetherWith
              slideOutHorizontally { width -> -width } + fadeOut()
        },
        label = "login_step_transition"
      ) { step ->
        when (step) {
          LoginStep.WELCOME -> {
            WelcomeStepView(
              onAgreeAndContinue = { currentStep = LoginStep.PHONE_INPUT },
              primaryColor = primaryColor,
              secondaryColor = secondaryColor,
              isDark = isDark
            )
          }

          LoginStep.PHONE_INPUT -> {
            PhoneInputStepView(
              selectedCountry = selectedCountry,
              phoneNumber = rawPhoneNumber,
              onPhoneNumberChange = { rawPhoneNumber = it.filter { ch -> ch.isDigit() }.take(12) },
              onOpenCountryPicker = { showCountryPicker = true },
              onNext = {
                if (rawPhoneNumber.length >= 6) {
                  showConfirmPhoneDialog = true
                }
              },
              primaryColor = primaryColor,
              secondaryColor = secondaryColor,
              isDark = isDark
            )
          }

          LoginStep.OTP_VERIFY -> {
            OtpVerificationStepView(
              fullPhoneNumber = "${selectedCountry.dialCode} $rawPhoneNumber",
              otpCode = otpCode,
              generatedOtp = generatedOtp,
              countdown = otpCountdown,
              isError = isOtpError,
              onOtpChange = { newOtp ->
                val filtered = newOtp.filter { it.isDigit() }.take(6)
                otpCode = filtered
                isOtpError = false
                if (filtered.length == 6) {
                  if (filtered == generatedOtp || filtered == "123456") {
                    currentStep = LoginStep.PROFILE_SETUP
                  } else {
                    isOtpError = true
                  }
                }
              },
              onResendOtp = {
                otpCountdown = 45
                generatedOtp = (100000..999999).random().toString()
                showSimulatedSmsBanner = true
              },
              onEditPhone = { currentStep = LoginStep.PHONE_INPUT },
              primaryColor = primaryColor,
              secondaryColor = secondaryColor,
              isDark = isDark
            )
          }

          LoginStep.PROFILE_SETUP -> {
            ProfileSetupStepView(
              displayName = displayName,
              onDisplayNameChange = { if (it.length <= 25) displayName = it },
              statusAbout = statusAbout,
              onStatusAboutChange = { statusAbout = it },
              avatarColors = avatarColors,
              selectedColorIndex = selectedColorIndex,
              onColorSelect = { selectedColorIndex = it },
              isLoading = isFinishingSetup,
              onFinish = {
                val finalName = if (displayName.isBlank()) "Hey Chat User" else displayName.trim()
                val profile = UserProfile(
                  phoneNumber = "${selectedCountry.dialCode} $rawPhoneNumber",
                  countryCode = selectedCountry.dialCode,
                  countryName = selectedCountry.name,
                  countryFlag = selectedCountry.flag,
                  displayName = finalName,
                  statusAbout = statusAbout.trim(),
                  avatarColorHex = avatarColors[selectedColorIndex],
                  isLoggedIn = true
                )
                onLoginCompleted(profile)
              },
              primaryColor = primaryColor,
              secondaryColor = secondaryColor,
              isDark = isDark
            )
          }
        }
      }

      // Simulated Incoming SMS Toast Banner
      AnimatedVisibility(
        visible = showSimulatedSmsBanner && currentStep == LoginStep.OTP_VERIFY,
        enter = fadeIn() + slideInHorizontally(),
        exit = fadeOut(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .statusBarsPadding()
          .padding(16.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = if (isDark) Color(0xFF202C33) else Color(0xFFF0F2F5),
          shadowElevation = 6.dp,
          border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppLightGreen),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .clickable {
                otpCode = generatedOtp
                showSimulatedSmsBanner = false
                currentStep = LoginStep.PROFILE_SETUP
              }
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(WhatsAppLightGreen, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Sms,
                contentDescription = "SMS",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "📩 Verification SMS Received",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = WhatsAppLightGreen
              )
              Text(
                text = "Hey Chat code: $generatedOtp • Tap to auto-fill",
                fontSize = 12.5.sp,
                color = primaryColor
              )
            }

            IconButton(
              onClick = { showSimulatedSmsBanner = false },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = secondaryColor,
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }

  // Country Picker Modal Sheet
  if (showCountryPicker) {
    CountryPickerBottomSheet(
      selectedCountry = selectedCountry,
      onSelect = {
        selectedCountry = it
        showCountryPicker = false
      },
      onDismiss = { showCountryPicker = false },
      isDark = isDark
    )
  }

  // Phone Confirmation Dialog
  if (showConfirmPhoneDialog) {
    AlertDialog(
      onDismissRequest = { showConfirmPhoneDialog = false },
      title = {
        Text(
          text = "Verify phone number?",
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column {
          Text(
            text = "We will be verifying the phone number:",
            color = secondaryColor,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "${selectedCountry.dialCode} $rawPhoneNumber",
            color = primaryColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Is this OK, or would you like to edit the number before getting an SMS code?",
            color = secondaryColor,
            fontSize = 13.5.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showConfirmPhoneDialog = false
            generatedOtp = (100000..999999).random().toString()
            otpCountdown = 45
            otpCode = ""
            currentStep = LoginStep.OTP_VERIFY
          },
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppLightGreen),
          modifier = Modifier.testTag("confirm_phone_ok_btn")
        ) {
          Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showConfirmPhoneDialog = false },
          modifier = Modifier.testTag("confirm_phone_edit_btn")
        ) {
          Text("Edit", color = WhatsAppLightGreen)
        }
      }
    )
  }
}

// ----------------------------------------------------
// 1. WELCOME STEP
// ----------------------------------------------------
@Composable
private fun WelcomeStepView(
  onAgreeAndContinue: () -> Unit,
  primaryColor: Color,
  secondaryColor: Color,
  isDark: Boolean
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 28.dp, vertical = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(top = 40.dp)
    ) {
      Text(
        text = "Welcome to Hey Chat",
        color = primaryColor,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(48.dp))

      // Hero Badge
      Box(
        modifier = Modifier
          .size(150.dp)
          .clip(CircleShape)
          .border(
            width = 3.dp,
            brush = Brush.linearGradient(
              listOf(WhatsAppLightGreen, WhatsAppEmerald, WhatsAppGreen)
            ),
            shape = CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_hey_chat_logo),
          contentDescription = "Hey Chat Logo",
          modifier = Modifier
            .size(144.dp)
            .clip(CircleShape)
        )
      }

      Spacer(modifier = Modifier.height(36.dp))

      Text(
        text = "Simple. Secure.\nReliable messaging and calling.",
        color = secondaryColor,
        fontSize = 15.5.sp,
        textAlign = TextAlign.Center,
        lineHeight = 22.sp
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 20.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Encrypted",
          tint = secondaryColor,
          modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "End-to-end encrypted chats & calls",
          color = secondaryColor,
          fontSize = 12.5.sp
        )
      }

      Text(
        text = "Read our Privacy Policy. Tap \"Agree and continue\" to accept the Terms of Service.",
        color = secondaryColor,
        fontSize = 12.5.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 24.dp)
      )

      Button(
        onClick = onAgreeAndContinue,
        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppLightGreen),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("agree_and_continue_btn")
      ) {
        Text(
          text = "AGREE AND CONTINUE",
          color = Color.White,
          fontSize = 14.5.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

// ----------------------------------------------------
// 2. PHONE NUMBER INPUT STEP
// ----------------------------------------------------
@Composable
private fun PhoneInputStepView(
  selectedCountry: CountryCode,
  phoneNumber: String,
  onPhoneNumberChange: (String) -> Unit,
  onOpenCountryPicker: () -> Unit,
  onNext: () -> Unit,
  primaryColor: Color,
  secondaryColor: Color,
  isDark: Boolean
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 24.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Spacer(modifier = Modifier.size(24.dp))
      Text(
        text = "Enter your phone number",
        color = primaryColor,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
      Icon(
        imageVector = Icons.Default.MoreVert,
        contentDescription = "More",
        tint = secondaryColor
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "Hey Chat will need to verify your phone number. Carrier SMS charges may apply.",
      color = secondaryColor,
      fontSize = 14.sp,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Country Selector Row
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = if (isDark) Color(0xFF1F2C34) else Color(0xFFF6F8FA),
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpenCountryPicker() }
        .padding(vertical = 4.dp)
        .testTag("country_picker_btn")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = selectedCountry.flag, fontSize = 20.sp)
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = selectedCountry.name,
            color = primaryColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
          )
        }
        Text(
          text = "▼",
          color = WhatsAppLightGreen,
          fontSize = 12.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Phone Number Input Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Country dial code
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) Color(0xFF1F2C34) else Color(0xFFF6F8FA),
        modifier = Modifier
          .width(80.dp)
          .height(56.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          Text(
            text = selectedCountry.dialCode,
            color = primaryColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Number text field
      OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberChange,
        placeholder = { Text("phone number", color = secondaryColor) },
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Phone,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onNext() }),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = WhatsAppLightGreen,
          unfocusedBorderColor = if (isDark) Color(0xFF2A3942) else Color(0xFFE0E0E0),
          focusedTextColor = primaryColor,
          unfocusedTextColor = primaryColor
        ),
        singleLine = true,
        modifier = Modifier
          .weight(1f)
          .testTag("phone_number_input")
      )
    }

    Spacer(modifier = Modifier.weight(1f))

    Button(
      onClick = onNext,
      enabled = phoneNumber.length >= 6,
      colors = ButtonDefaults.buttonColors(
        containerColor = WhatsAppLightGreen,
        disabledContainerColor = WhatsAppLightGreen.copy(alpha = 0.4f)
      ),
      shape = RoundedCornerShape(24.dp),
      modifier = Modifier
        .width(160.dp)
        .height(44.dp)
        .testTag("login_next_btn")
    ) {
      Text(
        text = "NEXT",
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      )
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

// ----------------------------------------------------
// 3. OTP VERIFICATION STEP
// ----------------------------------------------------
@Composable
private fun OtpVerificationStepView(
  fullPhoneNumber: String,
  otpCode: String,
  generatedOtp: String,
  countdown: Int,
  isError: Boolean,
  onOtpChange: (String) -> Unit,
  onResendOtp: () -> Unit,
  onEditPhone: () -> Unit,
  primaryColor: Color,
  secondaryColor: Color,
  isDark: Boolean
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 24.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onEditPhone) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Back",
          tint = primaryColor
        )
      }

      Text(
        text = "Verifying your number",
        color = primaryColor,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )

      Icon(
        imageVector = Icons.Default.MoreVert,
        contentDescription = "More",
        tint = secondaryColor
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "Waiting to automatically detect an SMS sent to",
      color = secondaryColor,
      fontSize = 14.sp,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(4.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Text(
        text = fullPhoneNumber,
        color = primaryColor,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "Wrong number?",
        color = WhatsAppLightGreen,
        fontSize = 13.5.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.clickable { onEditPhone() }
      )
    }

    Spacer(modifier = Modifier.height(36.dp))

    // 6-digit PIN Boxes
    BasicTextField(
      value = otpCode,
      onValueChange = onOtpChange,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
      decorationBox = {
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          repeat(6) { index ->
            val char = if (index < otpCode.length) otpCode[index].toString() else ""
            val isFocused = otpCode.length == index

            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDark) Color(0xFF1F2C34) else Color(0xFFF0F2F5))
                .border(
                  width = if (isFocused) 2.dp else 1.dp,
                  color = when {
                    isError -> Color.Red
                    isFocused -> WhatsAppLightGreen
                    else -> if (isDark) Color(0xFF2A3942) else Color(0xFFD0D7DE)
                  },
                  shape = RoundedCornerShape(8.dp)
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = char,
                color = primaryColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      },
      modifier = Modifier.testTag("otp_code_input")
    )

    if (isError) {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "Incorrect code. Please try again.",
        color = Color(0xFFE53935),
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
      )
    }

    Spacer(modifier = Modifier.height(28.dp))

    Text(
      text = "Enter 6-digit code",
      color = secondaryColor,
      fontSize = 13.sp
    )

    Spacer(modifier = Modifier.height(32.dp))

    HorizontalDivider(color = if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF))

    // Resend SMS Options
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(enabled = countdown == 0) { onResendOtp() }
        .padding(vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Sms,
        contentDescription = "Resend SMS",
        tint = if (countdown == 0) WhatsAppLightGreen else secondaryColor,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        text = "Resend SMS",
        color = if (countdown == 0) primaryColor else secondaryColor,
        fontSize = 15.sp,
        modifier = Modifier.weight(1f)
      )
      if (countdown > 0) {
        Text(
          text = "0:${String.format("%02d", countdown)}",
          color = secondaryColor,
          fontSize = 14.sp
        )
      }
    }

    HorizontalDivider(color = if (isDark) Color(0xFF202C33) else Color(0xFFE9EDEF))

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Phone,
        contentDescription = "Call me",
        tint = secondaryColor,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        text = "Call me",
        color = secondaryColor,
        fontSize = 15.sp,
        modifier = Modifier.weight(1f)
      )
      Text(
        text = "1:59",
        color = secondaryColor,
        fontSize = 14.sp
      )
    }
  }
}

// ----------------------------------------------------
// 4. PROFILE SETUP STEP
// ----------------------------------------------------
@Composable
private fun ProfileSetupStepView(
  displayName: String,
  onDisplayNameChange: (String) -> Unit,
  statusAbout: String,
  onStatusAboutChange: (String) -> Unit,
  avatarColors: List<Long>,
  selectedColorIndex: Int,
  onColorSelect: (Int) -> Unit,
  isLoading: Boolean,
  onFinish: () -> Unit,
  primaryColor: Color,
  secondaryColor: Color,
  isDark: Boolean
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(horizontal = 24.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Profile info",
      color = primaryColor,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Please provide your name and choose a profile color",
      color = secondaryColor,
      fontSize = 14.sp,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(36.dp))

    // Profile Avatar with dynamic color
    Box(
      modifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
        .background(Color(avatarColors[selectedColorIndex])),
      contentAlignment = Alignment.Center
    ) {
      if (displayName.isNotBlank()) {
        Text(
          text = displayName.take(2).uppercase(),
          color = Color.White,
          fontSize = 32.sp,
          fontWeight = FontWeight.Bold
        )
      } else {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = "Profile camera",
          tint = Color.White,
          modifier = Modifier.size(36.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Avatar Color Palette Selector
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      avatarColors.forEachIndexed { index, colorHex ->
        val isSelected = index == selectedColorIndex
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color(colorHex))
            .clickable { onColorSelect(index) }
            .border(
              width = if (isSelected) 2.5.dp else 0.dp,
              color = if (isSelected) Color.White else Color.Transparent,
              shape = CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          if (isSelected) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Selected",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Display Name Field
    OutlinedTextField(
      value = displayName,
      onValueChange = onDisplayNameChange,
      label = { Text("Type your name here") },
      supportingText = {
        Text(
          text = "${displayName.length}/25",
          textAlign = TextAlign.End,
          modifier = Modifier.fillMaxWidth()
        )
      },
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = WhatsAppLightGreen,
        unfocusedBorderColor = if (isDark) Color(0xFF2A3942) else Color(0xFFE0E0E0),
        focusedTextColor = primaryColor,
        unfocusedTextColor = primaryColor
      ),
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("profile_name_input")
    )

    Spacer(modifier = Modifier.height(12.dp))

    // About / Bio Field
    OutlinedTextField(
      value = statusAbout,
      onValueChange = onStatusAboutChange,
      label = { Text("About") },
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = WhatsAppLightGreen,
        unfocusedBorderColor = if (isDark) Color(0xFF2A3942) else Color(0xFFE0E0E0),
        focusedTextColor = primaryColor,
        unfocusedTextColor = primaryColor
      ),
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("profile_about_input")
    )

    Spacer(modifier = Modifier.weight(1f))

    Button(
      onClick = onFinish,
      enabled = !isLoading,
      colors = ButtonDefaults.buttonColors(containerColor = WhatsAppLightGreen),
      shape = RoundedCornerShape(24.dp),
      modifier = Modifier
        .width(160.dp)
        .height(48.dp)
        .testTag("profile_finish_btn")
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          color = Color.White,
          modifier = Modifier.size(22.dp)
        )
      } else {
        Text(
          text = "FINISH",
          color = Color.White,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
  }
}

// ----------------------------------------------------
// COUNTRY PICKER BOTTOM SHEET
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountryPickerBottomSheet(
  selectedCountry: CountryCode,
  onSelect: (CountryCode) -> Unit,
  onDismiss: () -> Unit,
  isDark: Boolean
) {
  val sheetState = rememberModalBottomSheetState()
  var searchQuery by remember { mutableStateOf("") }

  val filteredCountries = remember(searchQuery) {
    if (searchQuery.isBlank()) POPULAR_COUNTRIES else {
      POPULAR_COUNTRIES.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
            it.dialCode.contains(searchQuery) ||
            it.code.contains(searchQuery, ignoreCase = true)
      }
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = if (isDark) Color(0xFF1F2C34) else Color(0xFFFFFFFF)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Text(
        text = "Choose a country",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
      )

      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search country or dial code...") },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = WhatsAppLightGreen,
          unfocusedBorderColor = if (isDark) Color(0xFF2A3942) else Color(0xFFE0E0E0)
        ),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
      )

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .height(350.dp)
      ) {
        items(filteredCountries, key = { it.code + it.dialCode }) { country ->
          val isSelected = country.code == selectedCountry.code && country.dialCode == selectedCountry.dialCode

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSelect(country) }
              .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = country.flag, fontSize = 22.sp)
              Spacer(modifier = Modifier.width(16.dp))
              Text(
                text = country.name,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }

            Text(
              text = country.dialCode,
              fontSize = 15.sp,
              color = if (isSelected) WhatsAppLightGreen else Color(0xFF8696A0),
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }
  }
}
