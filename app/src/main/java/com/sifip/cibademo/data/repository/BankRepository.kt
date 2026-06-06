package com.sifip.cibademo.data.repository

import com.sifip.cibademo.data.model.BankAccount

class BankRepository {
    fun loadAccount(): BankAccount = BankAccount(
        holder = "Hery Razafindrakoto",
        accountNumberMasked = "•••• 4218",
        balanceMga = 4_938_598,
    )
}
