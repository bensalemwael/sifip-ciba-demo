package com.sifip.cibademo.data.model

data class FraudResult(
    val score: Int, // 0..100
    val decision: FraudDecision,
    val reasons: List<String>,
)

enum class FraudDecision { APPROVE, REVIEW, CHALLENGE, REJECT }
