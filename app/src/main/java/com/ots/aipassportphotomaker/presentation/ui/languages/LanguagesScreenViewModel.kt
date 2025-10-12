package com.ots.aipassportphotomaker.presentation.ui.languages

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.managers.AppLocaleManager
import com.ots.aipassportphotomaker.common.managers.PreferencesHelper
import com.ots.aipassportphotomaker.common.utils.AdsConstants
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import com.ots.aipassportphotomaker.common.utils.Logger
import com.ots.aipassportphotomaker.common.utils.SharedPrefUtils
import com.ots.aipassportphotomaker.domain.util.DispatchersProvider
import com.ots.aipassportphotomaker.domain.util.NetworkMonitor
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject

// Created by amanullah on 27/08/2025.
// Copyright (c) 2025 Ozi Publishing. All rights reserved.

@HiltViewModel
class LanguagesScreenViewModel @Inject constructor(
    imageProcessingBundle: LanguagesScreenBundle,
    private val dispatcher: DispatchersProvider,
    private val networkMonitor: NetworkMonitor,
    private val preferencesHelper: PreferencesHelper,
    private val analyticsManager: AnalyticsManager,
    private val adsManager: MyAdsManager,
    private val appLocaleManager: AppLocaleManager,
    @ApplicationContext private val context: Context
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<LanguagesScreenUiState> =
        MutableStateFlow(LanguagesScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<LanguagesScreenNavigationState> =
        singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _settingState = MutableStateFlow(LanguagesScreenUiState())
    val settingState: StateFlow<LanguagesScreenUiState> = _settingState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: MutableStateFlow<Boolean> get() = _isLoading

    val sourceScreen: String = imageProcessingBundle.sourceScreen

    init {
        Logger.i(
            "LanguagesScreenViewModel",
            " initialized with sourceScreen: $sourceScreen"
        )

        loadState(false)

        onInitialState()
    }

    private fun onInitialState() = launch {
        analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "LanguagesScreen")
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }

    fun onDoneClick() {
        launch {
            _navigationState.tryEmit(
                LanguagesScreenNavigationState.OnboardingScreen(
                    sourceScreen = sourceScreen
                )
            )
        }
    }

    fun isPremiumUser(): Boolean {
        return preferencesHelper.getBoolean(AdsConstants.IS_NO_ADS_ENABLED, false)
    }

    fun sendEvent(eventName: String, eventValue: String) {
        analyticsManager.sendAnalytics(eventName, eventValue)
    }

    fun getSavedLanguage(): String? {
        return preferencesHelper.getString(SharedPrefUtils.SELECTED_LANGUAGE, "")
    }

    fun handleLanguageChange(code: String) {
        val savedLang = getSavedLanguage()
        if (code != savedLang) {
            preferencesHelper.setString("app_language", code)
            val localeList = LocaleListCompat.forLanguageTags(code)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }


    private fun loadInitialLanguage() {
        val currentLanguage = appLocaleManager.getLanguageCode(context)
        _settingState.value = _settingState.value.copy(selectedLanguage = currentLanguage)
    }

    fun changeLanguage(languageCode: String) {
        appLocaleManager.changeLanguage(context,languageCode)
        _settingState.value = _settingState.value.copy(selectedLanguage = languageCode)
    }
}