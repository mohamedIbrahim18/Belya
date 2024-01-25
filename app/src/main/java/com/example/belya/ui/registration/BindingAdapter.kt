package com.example.belya.ui.registration

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("app:errorBindingAdapter")
fun bindErrorOnTextInputLayOut(textInputLayout: TextInputLayout
                               ,errorMessage:String?){
textInputLayout.error = errorMessage
}