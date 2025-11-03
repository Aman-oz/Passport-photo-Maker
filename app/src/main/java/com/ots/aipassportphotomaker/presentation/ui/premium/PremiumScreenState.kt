package com.ots.aipassportphotomaker.presentation.ui.premium

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.ots.aipassportphotomaker.common.iab.subscription.SubscriptionItem
import com.ots.aipassportphotomaker.domain.bottom_nav.Page
import javax.inject.Inject

data class PremiumScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val subscriptionItems: List<SubscriptionItem> = emptyList(),
    val sourceScreen: String = "other"

)

class PremiumScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {
    val sourceScreen: String = savedStateHandle.toRoute<Page.Premium>().sourceScreen

}

sealed class PremiumScreenNavigationState {
    data class HomeScreen(val value: Int) : PremiumScreenNavigationState()

}
