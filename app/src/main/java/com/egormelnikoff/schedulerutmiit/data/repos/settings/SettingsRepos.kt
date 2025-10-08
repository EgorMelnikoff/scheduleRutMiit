package com.egormelnikoff.schedulerutmiit.data.repos.settings

import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.model.TelegramPage
import javax.inject.Inject

interface SettingsRepos {
    suspend fun getTgChannelInfo(url: String): Result<TelegramPage>
}

class SettingsReposImpl @Inject constructor(
    private val parser: Parser
) : SettingsRepos {
    override suspend fun getTgChannelInfo(url: String): Result<TelegramPage> {
        return parser.parseChannelInfo(url)
    }
}
