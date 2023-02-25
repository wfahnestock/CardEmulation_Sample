package com.willpowered.cardemulationsample

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import java.util.*

/**
 * This is a sample APDU Service which demonstrates how to interface with the card emulation support
 * added in Android 4.4, KitKat.
 *
 * <p>This sample replies to any requests sent with the string "Hello World". In real-world
 * situations, you would need to modify this code to implement your desired communication
 * protocol.
 *
 * <p>This sample will be invoked for any terminals selecting AIDs of 0xF11111111, 0xF22222222, or
 * 0xF33333333. See src/main/res/xml/aid_list.xml for more details.
 *
 * <p class="note">Note: This is a low-level interface. Unlike the NdefMessage many developers
 * are familiar with for implementing Android Beam in apps, card emulation only provides a
 * byte-array based communication channel. It is left to developers to implement higher level
 * protocol support as needed.
 */
class myHostApduService : HostApduService() {

    // AID for our loyalty card service.
    private val SAMPLE_CARD_AID = "F222222222"

    // ISO-DEP command HEADER for selecting an AID.
    // Format: [Class | Instruction | Parameter 1 | Parameter 2]
    private val SELECT_APDU_HEADER = "00A40400"

    // "OK" status word sent in response to SELECT AID command (0x9000)
    private val SELECT_OK_SW: ByteArray = hexStringToByteArray("9000")

    // "UNKNOWN" status word sent in response to invalid APDU command (0x0000)
    private val UNKNOWN_CMD_SW: ByteArray = hexStringToByteArray("0000")
    private val SELECT_APDU: ByteArray = buildSelectApdu(SAMPLE_CARD_AID)

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        Log.i(TAG, "Received APDU: " + byteArrayToHexString(commandApdu))

        return if (SELECT_APDU.contentEquals(commandApdu)) {
            val cardNumber = CardStorage().getAccount(this)
            val cardNumberBytes: ByteArray = cardNumber.toByteArray()
            Log.i(TAG, "Sending card number: $cardNumber");
            // return the card number byte array along with "SELECT_OK" at the end
            concatArrays(cardNumberBytes, SELECT_OK_SW);
        } else {
            // Return unknown command
            UNKNOWN_CMD_SW
        }
    }

    /**
     * Called if the connection to the NFC card is lost, in order to let the application know the
     * cause for the disconnection (either a lost link, or another AID being selected by the
     * reader).
     *
     * @param reason Either DEACTIVATION_LINK_LOSS or DEACTIVATION_DESELECTED
     */
    override fun onDeactivated(reason: Int) { Log.i(TAG, "HCE Service Deactivated | reason: $reason") }

    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    private fun buildSelectApdu(aid: String): ByteArray {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return hexStringToByteArray(
            SELECT_APDU_HEADER + String.format(
                "%02X",
                aid.length / 2
            ) + aid
        )
    }

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    private fun byteArrayToHexString(bytes: ByteArray): String {
        val hexArray = charArrayOf(
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
        )
        val hexChars = CharArray(bytes.size * 2) // Each byte has two hex characters (nibbles)
        var v: Int
        for (j in bytes.indices) {
            v = bytes[j].toInt() and 0xFF // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v ushr 4] // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v and 0x0F] // Select hex character from lower nibble
        }
        return String(hexChars)
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     *
     *
     * Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        require(len % 2 != 1) { "Hex string must have even number of characters" }
        val data = ByteArray(len / 2) // Allocate 1 byte per 2 hex characters
        var i = 0
        while (i < len) {

            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = ((((s[i].digitToIntOrNull(16) ?: (-1 shl 4)) + s[i + 1].digitToIntOrNull(
                16
            )!!) ?: -1)).toByte()
            i += 2
        }
        return data
    }

    /**
     * Utility method to concatenate two byte arrays.
     * @param first First array
     * @param rest Any remaining arrays
     * @return Concatenated copy of input arrays
     */
    fun concatArrays(first: ByteArray, vararg rest: ByteArray): ByteArray {
        var totalLength = first.size
        for (array in rest) {
            totalLength += array.size
        }
        val result: ByteArray = first.copyOf(totalLength)
        var offset = first.size
        for (array in rest) {
            System.arraycopy(array, 0, result, offset, array.size)
            offset += array.size
        }
        return result
    }

    companion object {
        private const val TAG = "myHostApduService"
    }
}