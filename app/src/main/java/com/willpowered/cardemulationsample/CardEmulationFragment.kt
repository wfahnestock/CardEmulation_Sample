package com.willpowered.cardemulationsample

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class CardEmulationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_card_emulation, container, false)
        val account = view.findViewById<EditText>(R.id.card_account_field)
        account.setText(CardStorage().getAccount(requireContext()))
        account.addTextChangedListener(AccountUpdater())
        return view
    }

    private inner class AccountUpdater : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // Not implemented.
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // Not implemented.
        }

        override fun afterTextChanged(s: Editable) {
            val account = s.toString()
            CardStorage().setAccount(activity!!, account)
        }
    }

    companion object {
        const val TAG = "CardEmulationFragment"
    }
}