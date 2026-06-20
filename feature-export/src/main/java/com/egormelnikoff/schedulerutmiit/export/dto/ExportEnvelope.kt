package com.egormelnikoff.schedulerutmiit.export.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExportEnvelope(
    override val version: Int
) : VersionedExportData