package com.example.genderpickercanvas.genderPickerScreen.component

sealed class Gender {

    data object Male: Gender()
    data object FeMale: Gender()

}