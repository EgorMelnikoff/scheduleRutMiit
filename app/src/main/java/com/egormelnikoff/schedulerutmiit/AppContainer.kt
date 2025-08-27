package com.egormelnikoff.schedulerutmiit

import android.content.Context
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.datasource.local.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiImpl
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserImpl
import com.egormelnikoff.schedulerutmiit.data.repos.Repos
import com.egormelnikoff.schedulerutmiit.data.repos.ReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.local.LocalReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteReposImpl

interface AppContainer {
    val repos: Repos
    val dataStore: DataStore
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    private val api = ApiImpl()
    private val parser = ParserImpl()

    override val repos by lazy {
        ReposImpl(
            localRepos = LocalReposImpl(
                namedScheduleDao = AppDatabase.getDatabase(applicationContext).namedScheduleDao(),
                parser = parser
            ),
            remoteRepos = RemoteReposImpl(
                api = api,
                parser = parser
            )
        )
    }

    override val dataStore = DataStore(applicationContext)
}