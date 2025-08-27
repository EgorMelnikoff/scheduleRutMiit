package com.egormelnikoff.schedulerutmiit

import android.content.Context
import com.egormelnikoff.schedulerutmiit.data.datasource.datastore.DataStore
import com.egormelnikoff.schedulerutmiit.data.datasource.local.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiImpl
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.ParserImpl
import com.egormelnikoff.schedulerutmiit.data.datasource.resources.ResourcesManager
import com.egormelnikoff.schedulerutmiit.data.datasource.resources.ResourcesManagerImpl
import com.egormelnikoff.schedulerutmiit.data.repos.Repos
import com.egormelnikoff.schedulerutmiit.data.repos.ReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.local.LocalReposImpl
import com.egormelnikoff.schedulerutmiit.data.repos.remote.RemoteReposImpl

interface AppContainer {
    val repos: Repos
    val dataStore: DataStore
    val resourcesManager: ResourcesManager
}

class AppContainerImpl(private val context: Context) : AppContainer {
    private val api = ApiImpl()
    private val parser = ParserImpl()
    private val namedScheduleDao by lazy {
        AppDatabase.getDatabase(context).namedScheduleDao()
    }
    private val localRepos = LocalReposImpl(
        namedScheduleDao = namedScheduleDao,
        parser = parser
    )
    private val remoteRepos = RemoteReposImpl(
        api = api,
        parser = parser
    )

    override val repos =
        ReposImpl(
            localRepos = localRepos,
            remoteRepos = remoteRepos
        )


    override val dataStore = DataStore(context)

    override val resourcesManager = ResourcesManagerImpl(context)
}