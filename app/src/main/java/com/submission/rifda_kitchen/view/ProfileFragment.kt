package com.submission.rifda_kitchen.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.submission.rifda_kitchen.databinding.FragmentProfileBinding
import com.submission.rifda_kitchen.repository.AuthRepository
import com.submission.rifda_kitchen.repository.Repository
import com.submission.rifda_kitchen.viewModel.AuthViewmodel
import com.submission.rifda_kitchen.viewModel.AuthViewmodelFactory
import com.submission.rifda_kitchen.viewModel.UserViewmodel
import com.submission.rifda_kitchen.viewModel.ViewmodelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val authRepository = AuthRepository()
    private val repository = Repository()
    private val authViewmodel: AuthViewmodel by viewModels { AuthViewmodelFactory(authRepository) }
    private val userViewmodel: UserViewmodel by viewModels { ViewmodelFactory(repository) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewmodel.getCurrentUser()

        showCurrentUser()

        binding.btnSignOut.setOnClickListener {
            signOut()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signOut() {
        authViewmodel.signOUt(requireContext())
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }

    private fun showCurrentUser() {
        userViewmodel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.apply {
                    currentUserName.text = user.name
                    currentUserEmail.text = user.email
                }
                Glide.with(this)
                    .load(user.photo_url)
                    .into(binding.currentUserImage)
            }
        }
    }


}