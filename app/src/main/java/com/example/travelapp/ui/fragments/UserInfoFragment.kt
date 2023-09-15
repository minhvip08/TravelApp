package com.example.travelapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.example.travelapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "showName"

/**
 * A simple [Fragment] subclass.
 * Use the [UserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserInfoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var showName: Boolean? = null
    private lateinit var authAttributeHeaderName: TextView
    private lateinit var authAttributeLayoutName: LinearLayout
    private lateinit var authAttributeName: EditText
    private lateinit var authAttributeHeaderEmail: TextView
    private lateinit var authAttributeEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            showName = it.getBoolean(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authAttributeHeaderName = view.findViewById(R.id.auth_attribute_header_name)
        authAttributeHeaderEmail = view.findViewById(R.id.auth_attribute_header_email)
        authAttributeName = view.findViewById(R.id.auth_attribute_name)
        authAttributeEmail = view.findViewById(R.id.auth_attribute_email)
        authAttributeLayoutName = view.findViewById(R.id.auth_attribute_layout_name)
        if (showName == true) {
            showName()
        }
        else {
            hideName()
        }
        // After text changes
        authAttributeName.doAfterTextChanged {
            if (it.isNullOrEmpty()) {
                authAttributeName.error = "Name is required"
            }
            else if (it.isBlank()) {
                authAttributeName.error = "Name must not be blank"
            }
            else if (it.length < 2) {
                authAttributeName.error = "Name must be at least 2 characters long"
            }
            else if (it.length > 50) {
                authAttributeName.error = "Name must not exceed 50 characters"
            }
        }
        authAttributeEmail.doAfterTextChanged {
            if (it.isNullOrEmpty()) {
                authAttributeEmail.error = "Email address is required"
            }
            else if (it.isBlank()) {
                authAttributeEmail.error = "Email address must not be blank"
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                authAttributeEmail.error = "Invalid email address"
            }
        }
    }

    fun isEmailInvalid() : Boolean {
        return authAttributeEmail.error != null
    }

    fun isNameInvalid(): Boolean {
        return authAttributeName.error != null
    }

    fun isNameValid(): Boolean {
        return authAttributeName.error == null
    }

    fun isEmailValid(): Boolean {
        return authAttributeEmail.error == null
    }

    fun showName() {
        authAttributeHeaderName.visibility = View.VISIBLE
        authAttributeLayoutName.visibility = View.VISIBLE
    }


    fun hideName() {
        authAttributeHeaderName.visibility = View.GONE
        authAttributeLayoutName.visibility = View.GONE
    }

    fun getName(): String {
        return authAttributeName.text.toString()
    }

    fun getEmail(): String {
        return authAttributeEmail.text.toString()
    }

    fun disableEmail() {
        authAttributeEmail.isEnabled = false
        authAttributeEmail.setTextColor(resources.getColor(R.color.edit_text_disabled, null))
    }

    fun setName(name: String) {
        authAttributeName.setText(name)
    }

    fun setEmail(email: String) {
        authAttributeEmail.setText(email)
    }

    fun clearName() {
        authAttributeName.text.clear()
    }

    fun clearEmail() {
        authAttributeEmail.text.clear()
    }

    fun hideEmail() {
        authAttributeHeaderEmail.visibility = View.GONE
        authAttributeEmail.visibility = View.GONE
    }

    fun showEmail() {
        authAttributeHeaderEmail.visibility = View.VISIBLE
        authAttributeEmail.visibility = View.VISIBLE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param showName Parameter 1.
         * @return A new instance of fragment UserInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(showName: Boolean) =
            UserInfoFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, showName)
                }
            }
    }
}