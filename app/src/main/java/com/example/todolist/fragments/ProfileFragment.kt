package com.example.todolist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todolist.auth.LoginActivity
import com.example.todolist.databinding.FragmentProfileBinding
import com.example.todolist.model.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels() // ViewModel for user data
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // ✅ Manually Set Toolbar Title for Profile Fragment
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = "Profile"

        // Observe ViewModel for user name
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.txtUserName.text = "Hello, $name"
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // ✅ Ensure Title is "Profile" Even When Switching Fragments
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = "Profile"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
