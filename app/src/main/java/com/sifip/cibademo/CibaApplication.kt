package com.sifip.cibademo

import android.app.Application
import com.sifip.cibademo.data.mock.AuthScenario
import com.sifip.cibademo.data.mock.CibaService
import com.sifip.cibademo.data.mock.MockScenario
import com.sifip.cibademo.data.mock.SifipAuthMock
import com.sifip.cibademo.data.repository.BankRepository

class CibaApplication : Application() {

    lateinit var cibaService: CibaService
        private set

    lateinit var sifipAuth: SifipAuthMock
        private set

    lateinit var bankRepository: BankRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val initialCiba = runCatching {
            MockScenario.valueOf(BuildConfig.DEFAULT_CIBA_SCENARIO)
        }.getOrDefault(MockScenario.APPROVE_LOW_FRAUD)

        cibaService = CibaService(initialCiba)
        sifipAuth = SifipAuthMock(AuthScenario.ALL_OK)
        bankRepository = BankRepository()
    }
}
