package com.ots.aipassportphotomaker.presentation.ui.premium

import androidx.lifecycle.SavedStateHandle
import com.ots.aipassportphotomaker.common.iab.subscription.SubscriptionItem
import javax.inject.Inject

data class PremiumScreenUiState(
    val showLoading: Boolean = true,
    val errorMessage: String? = null,
    val subscriptionItems: List<SubscriptionItem> = emptyList()

)

class PremiumScreenBundle @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

}

sealed class PremiumScreenNavigationState {
    data class HomeScreen(val value: Int) : PremiumScreenNavigationState()

}
