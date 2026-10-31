package com.egormelnikoff.schedulerutmiit.ui.view_model

import android.net.Uri

sealed interface MainAction {
    data class ExportData(val uri: Uri) : MainAction
    data class ImportData(val uri: Uri?) : MainAction
}
