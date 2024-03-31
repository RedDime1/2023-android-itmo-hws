package ru.ok.itmo.example

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class LoginFragment : Fragment(R.layout.login_fragment) {

    private lateinit var etLog: TextInputEditText;
    private lateinit var etPassword: TextInputEditText
    private lateinit var subButt: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etLog = view.findViewById(R.id.login)
        etPassword = view.findViewById(R.id.password)
        subButt = view.findViewById(R.id.login_button)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etLog.text.isNullOrBlank() || etPassword.text.isNullOrBlank()) {
                    subButt.isEnabled = false
                    subButt.background =
                        ContextCompat.getDrawable(context!!, R.drawable.button_inactive)
                } else {
                    subButt.isEnabled = true
                    subButt.background =
                        ContextCompat.getDrawable(context!!, R.drawable.button_active)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }

        etLog.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)

        subButt.isEnabled = false
        subButt.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_inactive)
        subButt.setOnClickListener {
            val allOk = checkFields()
            if (allOk) {
                parentFragmentManager.beginTransaction()
                    .remove(this)
                    .replace(R.id.container, SuccesFragment(), "suc_enter")
                    .addToBackStack("suc_enter")
                    .commit()
            }
        }

    }

    private var users = mapOf("dima" to "password", "ogo" to "123456")

    private fun checkFields(): Boolean {
        if (etLog.length() == 0) {
            etLog.error = getString(R.string.field_is_required)
            return false
        }
        if (etPassword.length() == 0) {
            etPassword.error = getString(R.string.field_is_required)
            return false
        }
        if (etLog.length() < 3) {
            etLog.error = getString(R.string.min_login)
            return false
        }
        if (etPassword.length() < 6) {
            etPassword.error = getString(R.string.min_pass)
            return false
        }
        if (etPassword.text.toString() != users.getOrDefault(etLog.text.toString(), "")){
            Toast.makeText(requireView().context, getString(R.string.wrong), Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }


}