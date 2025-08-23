package com.egormelnikoff.schedulerutmiit

import android.content.Context
import com.egormelnikoff.schedulerutmiit.data.datasource.local.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.Api
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import com.egormelnikoff.schedulerutmiit.data.repos.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.repos.local.LocalRepos
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteRepos

interface AppContainerInterface {
    val localRepos: LocalRepos
    val remoteRepos: RemoteRepos
    val dataStore: DataStore
}

class AppContainer(private val applicationContext: Context) : AppContainerInterface {
    private val api = Api()
    private val parser = Parser()

    override val localRepos by lazy {
        LocalRepos(
            namedScheduleDao = AppDatabase.getDatabase(applicationContext).namedScheduleDao(),
            parser = parser
        )
    }

    override val remoteRepos by lazy {
        RemoteRepos(
            api = api,
            parser = parser
        )
    }

    override val dataStore = DataStore(applicationContext)
}