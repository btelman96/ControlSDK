package org.btelman.controlsdk.hardware.translators

import com.google.common.primitives.Bytes.concat
import org.btelman.controlsdk.hardware.interfaces.Translator
import org.btelman.controlsdk.hardware.interfaces.TranslatorComponent

/**
 * Handles NXT communication using the the joystick driver for Tetrix/Matrix
 *
 * This protocol was used in earlier FIRST Tech Challenge seasons
 */
@TranslatorComponent(
    "Converts to Lego Mindstorms NXT protocol using the the joystick driver for Tetrix/Matrix. " +
            "FBLR commands are sent as d-pad directions")
class NXTJoystickDriverTranslator : Translator {
    override fun translateString(command: String): ByteArray {
        val joy1 = Joystick()
        when(command){
            "F" -> {
                joy1.topHat = 0.toByte() //up
            }
            "B" -> {
                joy1.topHat = 4.toByte() //down
            }
            "R" -> {
                joy1.topHat = 2.toByte() //left
            }
            "L" -> {
                joy1.topHat = 6.toByte() //right
            }
            //add more commands here for your use case
            else -> {
                joy1.topHat = (-1).toByte() //inactive
            }
        }
        return getPacket(joy1)
    }

    override fun translateAny(command: Any): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translateStop(): ByteArray {
        return getPacket()
    }

    class Joystick{
        var X1 : Byte = 0x00
        var X2 : Byte = 0x00
        var Y1 : Byte = 0x00
        var Y2 : Byte = 0x00
        var buttons = ByteArray(2) //a short bitmap of buttons, split into bytearray
        var topHat : Byte = (-1).toByte()
    }

    fun getPacket(joy1 : Joystick = Joystick(), joy2 : Joystick = Joystick()) : ByteArray{
        var bytePackage = byteArrayOf(0x00.toByte(),
                0x09,
                0x00,
                0x12,
                0x00,
                0x01, //usermode - true
                0x00, //stopPgm - false
                //joystick 1
                joy1.X1, joy1.Y1, //stick 1
                joy1.X2, joy1.Y2, //stick 2
                joy1.buttons[0],
                joy1.buttons[1],
                joy1.topHat,
                //joystick 2
                joy2.X1, joy2.Y1, //stick 1
                joy2.X2, joy2.Y2, //stick 2
                joy2.buttons[0],
                joy2.buttons[1],
                joy2.topHat,
                0x00)
        val bluetoothPacket = byteArrayOf(0x16, 0x00)
        bytePackage = concat(bluetoothPacket, bytePackage)
        return bytePackage
    }
}