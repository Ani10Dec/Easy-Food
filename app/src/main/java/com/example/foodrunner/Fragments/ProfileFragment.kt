package com.example.foodrunner.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.foodrunner.R

class ProfileFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var tvName: TextView
    lateinit var tvNumber: TextView
    lateinit var tvEmail: TextView
    lateinit var tvAddress: TextView
    lateinit var stName: String
    lateinit var stNumber: String
    lateinit var stEmail: String
    lateinit var stAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = activity?.getSharedPreferences("credentials", Context.MODE_PRIVATE) ?: sharedPreferences
        stName = sharedPreferences.getString("userName", "Unknown").toString()
        stNumber = sharedPreferences.getString("userNumber", "Unknown").toString()
        stEmail = sharedPreferences.getString("userEmail", "Unknown").toString()
        stAddress = sharedPreferences.getString("userAddress", "Unknown").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        // HOOKS
        tvName = view.findViewById(R.id.tv_name)
        tvNumber = view.findViewById(R.id.tv_number)
        tvEmail = view.findViewById(R.id.tv_email)
        tvAddress = view.findViewById(R.id.tv_address)
        // Assign Variables
        tvName.text = stName
        tvNumber.text = stNumber
        tvEmail.text = stEmail
        tvAddress.text = stAddress
        return view
    }

}