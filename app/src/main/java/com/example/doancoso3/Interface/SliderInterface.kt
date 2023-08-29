package com.example.doancoso3.Interface

interface SliderInterface {
    fun watchMovies(position: Int)
    fun addFavoriteMovies(position: Int)
    fun removeFavoriteMovies(id: String)
    fun notifyAddOrRemove()
}