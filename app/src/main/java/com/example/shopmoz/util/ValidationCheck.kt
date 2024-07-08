package com.example.shopmoz.util

import android.media.MediaCodec.CryptoInfo.Pattern
import android.util.Patterns

fun validateEmail(email: String): RegisterValidation{
    if (email.isEmpty())
        return RegisterValidation.Failed("Email não pode estar vazio")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return  RegisterValidation.Failed("Formato do Email está incorrecto")

    return  RegisterValidation.Sucess
}

fun validatePassword( password: String): RegisterValidation{
    if (password.isEmpty())
        return RegisterValidation.Failed("Senha não pode estar vazia")
    if (password.length<6)
        return RegisterValidation.Failed("Senha deve conter 6 caracteres no minimo")

    return RegisterValidation.Sucess

}