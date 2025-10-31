package com.egormelnikoff.schedulerutmiit.data.repos.settings

import com.egormelnikoff.schedulerutmiit.app.model.TelegramPage
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import javax.inject.Inject

interface SettingsRepos {
    suspend fun getTgChannelInfo(url: String): Result<TelegramPage>
}

class SettingsReposImpl @Inject constructor(
    private val parser: Parser
) : SettingsRepos {
    override suspend fun getTgChannelInfo(url: String) = parser.parseChannelInfo(url)
}