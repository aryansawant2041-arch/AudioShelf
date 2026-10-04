package com.example.audioshelf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = requireActivity().getSharedPreferences("session", android.content.Context.MODE_PRIVATE)
            .getString("logged_in_user", "User")

        view.findViewById<TextView>(R.id.profileUsername).text = username

        view.findViewById<Button>(R.id.logoutButton).setOnClickListener {
            requireActivity().getSharedPreferences("session", android.content.Context.MODE_PRIVATE)
                .edit().clear().apply()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }
}