package it.polito.g13.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import it.polito.g13.businesslogic.BusinessClass
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    @Inject
    lateinit var businessClass : BusinessClass

    @Override
    override fun onCreate(){
        super.onCreate()
    }
}