package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export

import kotlinx.serialization.Serializable

@Serializable
data class ExportEnvelope(
    override val version: Int
) : VersionedExportData