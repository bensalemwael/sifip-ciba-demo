package com.sifip.cibademo

import android.app.Application
import com.sifip.cibademo.data.mock.CibaService
import com.sifip.cibademo.data.mock.MockScenario
import com.sifip.cibademo.data.repository.BankRepository

class CibaApplication : Application() {

    lateinit var cibaService: CibaService
        private set

    lateinit var bankRepository: BankRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val initial = runCatching {
            MockScenario.valueOf(BuildConfig.DEFAULT_CIBA_SCENARIO)
        }.getOrDefault(MockScenario.APPROVE_LOW_FRAUD)

        cibaService = CibaService(initial)
        bankRepository = BankRepository()
    }
}
