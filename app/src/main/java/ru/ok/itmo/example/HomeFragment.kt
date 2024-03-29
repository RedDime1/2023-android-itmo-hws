package ru.ok.itmo.example

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

const val NAVIGATION = "nav_tag"

class HomeFragment : Fragment(R.layout.fragment_with_btn) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.start).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, NavigationFragment(), NAVIGATION)
                .addToBackStack(NAVIGATION)
                .commit()
        }
    }
}