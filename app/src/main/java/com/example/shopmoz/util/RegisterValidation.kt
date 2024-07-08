package com.example.shopmoz.util

import android.os.Message

sealed class RegisterValidation() {
    object  Sucess: RegisterValidation()

    data class Failed(val message: String): RegisterValidation()

}
data class RegisterFieldsState(
    val email: RegisterValidation,
    val password: RegisterValidation
)