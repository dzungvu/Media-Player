package com.dzungvu

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Use for
 * Created by DzungVu on 1/31/2018.
 */
class MyApplication : Application() {
    private val REALM_NAME = "media.realm"
    private val SCHEMA_VERSION: Long = 1

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val realmConfiguration: RealmConfiguration = RealmConfiguration.Builder()
                .name(REALM_NAME)
                .schemaVersion(SCHEMA_VERSION)
//                .migration(RealmMigrations())
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        Realm.getInstance(realmConfiguration)
    }

    override fun onTerminate() {
        super.onTerminate()
        Realm.getDefaultInstance().close()
    }
}