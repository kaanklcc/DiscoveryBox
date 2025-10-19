package com.kaankilic.discoverybox.entitiy

data class UserData(
    val adsWatchedToday: Int,
    val maxAdsPerDay: Int,
    val remainingFreeUses: Int,
    val lastFreeUseReset: String,
    val premium: Boolean


)
