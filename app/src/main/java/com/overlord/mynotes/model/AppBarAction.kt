package com.overlord.mynotes.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class AppBarAction(
    @DrawableRes val icon: Int,
    @StringRes val description: Int,
    val onClick: () -> Unit
)