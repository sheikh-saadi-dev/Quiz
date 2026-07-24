package com.example.admob

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdManager private constructor(private val context: Context) {

    private var lastInterstitialShownTime: Long = 0L
    private var quizzesCompletedCount: Int = 0

    private val _isAdsRemoved = MutableStateFlow(false)
    val isAdsRemoved: StateFlow<Boolean> = _isAdsRemoved.asStateFlow()

    fun setAdsRemoved(removed: Boolean) {
        _isAdsRemoved.value = removed
    }

    /**
     * Hard compliance rule:
     * First 2 quiz completions for a brand new user skip interstitial to preserve retention.
     * Enforces minimum 60 seconds gap between interstitial ads.
     */
    fun canShowInterstitial(): Boolean {
        if (_isAdsRemoved.value) return false
        
        quizzesCompletedCount++
        if (quizzesCompletedCount <= 2) {
            return false
        }

        val currentTime = System.currentTimeMillis()
        val timeSinceLastAd = currentTime - lastInterstitialShownTime
        val sixtySecondsMillis = 60_000L

        return timeSinceLastAd >= sixtySecondsMillis
    }

    fun recordInterstitialShown() {
        lastInterstitialShownTime = System.currentTimeMillis()
    }

    companion object {
        @Volatile
        private var INSTANCE: AdManager? = null

        fun getInstance(context: Context): AdManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AdManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
