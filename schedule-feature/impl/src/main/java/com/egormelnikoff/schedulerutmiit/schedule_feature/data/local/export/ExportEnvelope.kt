package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export

import kotlinx.serialization.Serializable

@Serializable
data class ExportEnvelope(
    override val version: Int
) : VersionedExportData