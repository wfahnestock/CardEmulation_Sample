package com.willpowered.cardemulationsample

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log


class CardStorage {

    /* Globals */
    private val lock: Any = Any()
    private var gCardNum: String? = null

    fun setAccount(ctx: Context, s: String) {
        synchronized(lock) {
            Log.i(TAG, "Setting card number: $s")
            val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
            // Persist the card number to disk with ShardPreferences.
            prefs.edit().putString("cardNumber", s).apply()
            gCardNum = s
        }
    }

    fun getAccount(ctx: Context): String {
        synchronized (lock) {
            if (gCardNum == null) {
                val prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
                // Retrieve a saved card number from SharedPreferences, or empty string if it doesn't exist.
                val account = prefs.getString("cardNumber", "");
                gCardNum = account;
            }
            return gCardNum!!
        }
    }

    companion object {
        private const val TAG = "CardStorage"
    }
}