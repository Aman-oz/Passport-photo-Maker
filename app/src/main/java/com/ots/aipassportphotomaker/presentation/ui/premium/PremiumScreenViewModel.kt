package com.ots.aipassportphotomaker.presentation.ui.premium

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import com.las.collage.maker.iab.ProductItem
import com.ots.aipassportphotomaker.adsmanager.admob.MyAdsManager
import com.ots.aipassportphotomaker.common.ext.singleSharedFlow
import com.ots.aipassportphotomaker.common.iab.AppBillingClient
import com.ots.aipassportphotomaker.common.iab.subscription.SubscriptionItem
import com.ots.aipassportphotomaker.presentation.ui.base.BaseViewModel
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.ots.aipassportphotomaker.common.iab.interfaces.ConnectResponse
import com.ots.aipassportphotomaker.common.iab.interfaces.PurchaseResponse
import com.ots.aipassportphotomaker.common.managers.AnalyticsManager
import com.ots.aipassportphotomaker.common.utils.AnalyticsConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update

@HiltViewModel
class PremiumScreenViewModel @Inject constructor(
    premiumScreenBundle: PremiumScreenBundle,
    private val billingClient: AppBillingClient,
    private val adsManager: MyAdsManager,
    private val analyticsManager: AnalyticsManager,
    @ApplicationContext private val context: Context,
) : BaseViewModel() {

    private val _uiState: MutableStateFlow<PremiumScreenUiState> = MutableStateFlow(
        PremiumScreenUiState()
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationState: MutableSharedFlow<PremiumScreenNavigationState> = singleSharedFlow()
    val navigationState = _navigationState.asSharedFlow()

    private val _subscriptionItems: MutableStateFlow<List<SubscriptionItem>> = MutableStateFlow(emptyList())
    val subscriptionItems = _subscriptionItems.asStateFlow()


    init {

        onInitialState()
        loadState(false)
        connectBillingClient()
    }

    private fun onInitialState() = launch {
        analyticsManager.sendAnalytics(AnalyticsConstants.OPENED, "PremiumScreen")
    }

    private fun loadState(isLoading: Boolean) {
        launch {
            _uiState.value = _uiState.value.copy(showLoading = isLoading, errorMessage = null)
        }
    }

    fun onOpenCameraClicked() {
//        _navigationState.tryEmit(PhotoIDScreenNavigationState.TakePhotoScreen(documentId))
    }

    private fun connectBillingClient() {
        billingClient.connect(
            context = context,
            connectResponse = object : ConnectResponse {
                override fun disconnected() {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = "Billing service disconnected") }
                }

                override fun billingUnavailable() {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = "Billing unavailable") }
                }

                override fun developerError() {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = "Developer error") }
                }

                override fun error() {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = "Billing error") }
                }

                override fun featureNotSupported() {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = "Feature not supported") }
                }

                override fun itemUnavailable() {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = "Item unavailable") }
                }

                override fun ok(subscriptionItems: List<SubscriptionItem>) {
                    loadState(false)
                    _subscriptionItems.value = subscriptionItems
                }

                override fun serviceDisconnected() {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = "Service disconnected") }
                }

                override fun serviceUnavailable() {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = "Service unavailable") }
                }
            },
            purchaseResponse = object : PurchaseResponse {
                override fun isAlreadyOwned() {
                    _uiState.update { it.copy(errorMessage = "Subscription already owned") }
                }

                override fun userCancelled() {
                    _uiState.update { it.copy(errorMessage = "Purchase cancelled by user") }
                }

                override fun ok(productItem: ProductItem) {
                    loadState(false)
                    adsManager.setEnabledNoAds(true)
                    _uiState.update { it.copy(errorMessage = "Purchase successful") }
                    // Navigate to home or update UI as needed
                    _navigationState.tryEmit(PremiumScreenNavigationState.HomeScreen(0))
                }

                override fun error(error: String) {
                    loadState(false)
                    _uiState.update { it.copy(errorMessage = error) }
                }
            }
        )
    }

    fun purchaseSubscription(activity: Activity,sku: String) {

        val subscriptionItem = _subscriptionItems.value.find { it.sku == sku }
        if (subscriptionItem != null) {
            billingClient.purchaseSkuItem(
                baseActivity = activity,
                productItem = subscriptionItem
            )
        } else {
            loadState(false)
            _uiState.update { it.copy(errorMessage = "Subscription item not found") }
        }
    }

    fun sendEvent(eventName: String, eventValue: String) {
        analyticsManager.sendAnalytics(eventName, eventValue)
    }
}