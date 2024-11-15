package br.com.energym.models

data class Reward(
    val description: String,
    val points: Int,
    val company: String,
    val type: String,
    val isAvailable: Boolean
)