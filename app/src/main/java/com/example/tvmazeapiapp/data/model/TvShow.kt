package com.example.tvmazeapiapp.data.model

data class TvShow(
    val id: Int,
    val name: String,
    val network: Network?,
    val genres: List<String>?,
    val rating: Rating?
)

data class Rating(
    val average: Double?
)

data class Network(
    val country: Country?
)

data class Country(
    val name: String
)
