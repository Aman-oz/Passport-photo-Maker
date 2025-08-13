package com.ots.aipassportphotomaker.presentation.ui.usecase.photoid

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import com.ots.aipassportphotomaker.domain.model.DocumentListItem
import javax.inject.Inject
import kotlin.let

/**
 * @author by Ali Asadi on 29/01/2023
 */
class InsertSeparatorIntoPagingData @Inject constructor() {
    fun insert(pagingData: PagingData<DocumentListItem.Document>): PagingData<DocumentListItem> {
        return pagingData.insertSeparators { before: DocumentListItem.Document?, after: DocumentListItem.Document? ->
            when {
                isListEmpty(before, after) -> null
                isHeader(before) -> insertHeaderItem(after)
                isFooter(after) -> insertFooterItem()
                isDifferentCategory(before, after) -> insertSeparatorItem(after)
                else -> null
            }
        }
    }

    private fun isListEmpty(before: DocumentListItem.Document?, after: DocumentListItem.Document?): Boolean = before == null && after == null

    private fun isHeader(before: DocumentListItem.Document?): Boolean = before == null

    private fun isFooter(after: DocumentListItem.Document?): Boolean = after == null

    private fun isDifferentCategory(before: DocumentListItem.Document?, after: DocumentListItem.Document?): Boolean =
        before?.name != after?.name

    /**
     * Insert Header; return null to skip adding a header.
     * **/
    private fun insertHeaderItem(after: DocumentListItem.Document?): DocumentListItem? = createSeparator(after)

    /**
     * Insert Footer; return null to skip adding a footer.
     * **/
    @Suppress("FunctionOnlyReturningConstant")
    private fun insertFooterItem(): DocumentListItem? = null

    /**
     * Insert a separator between two items that start with different date.
     * **/
    private fun insertSeparatorItem(after: DocumentListItem.Document?): DocumentListItem.Separator? = createSeparator(after)

    private fun createSeparator(item: DocumentListItem.Document?) = item?.let {
        DocumentListItem.Separator(it.name)
    }
}
