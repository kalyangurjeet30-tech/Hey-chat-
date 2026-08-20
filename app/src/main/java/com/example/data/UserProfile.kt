package com.example.data

data class UserProfile(
  val phoneNumber: String = "",
  val countryCode: String = "+1",
  val countryName: String = "United States",
  val countryFlag: String = "🇺🇸",
  val displayName: String = "Hey Chat User",
  val statusAbout: String = "Hey there! I am using Hey Chat.",
  val avatarColorHex: Long = 0xFF008069,
  val avatarType: AvatarType = AvatarType.DEFAULT,
  val isLoggedIn: Boolean = false
)

data class CountryCode(
  val name: String,
  val code: String,
  val dialCode: String,
  val flag: String
)

val POPULAR_COUNTRIES = listOf(
  CountryCode("United States", "US", "+1", "🇺🇸"),
  CountryCode("India", "IN", "+91", "🇮🇳"),
  CountryCode("United Kingdom", "GB", "+44", "🇬🇧"),
  CountryCode("Canada", "CA", "+1", "🇨🇦"),
  CountryCode("United Arab Emirates", "AE", "+971", "🇦🇪"),
  CountryCode("Germany", "DE", "+49", "🇩🇪"),
  CountryCode("France", "FR", "+33", "🇫🇷"),
  CountryCode("Australia", "AU", "+61", "🇦🇺"),
  CountryCode("Brazil", "BR", "+55", "🇧🇷"),
  CountryCode("Japan", "JP", "+81", "🇯🇵"),
  CountryCode("Singapore", "SG", "+65", "🇸🇬"),
  CountryCode("Saudi Arabia", "SA", "+966", "🇸🇦"),
  CountryCode("Pakistan", "PK", "+92", "🇵🇰"),
  CountryCode("Nigeria", "NG", "+234", "🇳🇬"),
  CountryCode("Mexico", "MX", "+52", "🇲🇽"),
  CountryCode("Spain", "ES", "+34", "🇪🇸"),
  CountryCode("Italy", "IT", "+39", "🇮🇹"),
  CountryCode("Netherlands", "NL", "+31", "🇳🇱"),
  CountryCode("Indonesia", "ID", "+62", "🇮🇩"),
  CountryCode("South Africa", "ZA", "+27", "🇿🇦")
)
