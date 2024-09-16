package com.submission.rifda_kitchen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.submission.rifda_kitchen.repository.Repository

class ViewmodelFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewmodel::class.java) -> {
                return UserViewmodel(repository) as T
            }
            modelClass.isAssignableFrom(ProductViewmodel::class.java) -> {
                return ProductViewmodel(repository) as T
            }
            modelClass.isAssignableFrom(CartViewmodel::class.java) -> {
                return CartViewmodel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}