package org.btelman.controlsdk.hardware.translators

import org.btelman.controlsdk.hardware.interfaces.Translator
import org.btelman.controlsdk.hardware.interfaces.TranslatorComponent
import org.btelman.controlsdk.hardware.utils.SingleByteUtil

/**
 * Uses the SaberTooth motor controller simplified serial protocol
 *
 * Snippet from a SaberTooth Motor Controller Manual
 *
 * Sending a character between 1 and 127 will
 * control motor 1. 1 is full reverse, 64 is stop and 127 is full forward. Sending a character between
 * 128 and 255 will control motor 2. 128 is full reverse, 192 is stop and 255 is full forward.
 * Character 0 (hex 0x00) is a special case. Sending this character will shut down both motors.
 *
 * Currently hardcoded for a certain speed, but can be changed
 */
@TranslatorComponent("Uses the SaberTooth motor controller simplified serial protocol")
class SingleByteTranslator : Translator {
    override fun translateString(command: String): ByteArray {
        return convertToByte(command)
    }

    override fun translateAny(command: Any): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun translateStop(): ByteArray {
        return convertToByte("stop")
    }

    private val motorForwardSpeed = 127
    private val motorBackwardSpeed = -127

    private val motorForwardTurnSpeed = 60
    private val motorBackwardTurnSpeed = -60

    private fun convertToByte(command: String) : ByteArray{
        return when(command.toLowerCase()){
            "f" -> {
                createPacket(motorForwardSpeed)
            }
            "b" -> {
                createPacket(motorBackwardSpeed)
            }
            "l" -> {
                createPacket(motorBackwardTurnSpeed, motorForwardTurnSpeed)
            }
            "r" -> {
                createPacket(motorForwardTurnSpeed, motorBackwardTurnSpeed)
            }
            else -> {
                val bytes = ByteArray(1)
                bytes[0] = 0x00
                /*return*/ bytes
            }
        }
    }

    /**
     * Create a single byte packet of duplicate motors. Passing in one argument will make
     * both motors move at the same speed
     *
     * Values must be in the byte range, or it may not work correctly
     */
    private fun createPacket(motor0Speed : Int, motor1Speed : Int = Int.MAX_VALUE) : ByteArray{
        //allow for passing in a single variable if both are the same
        var motor1 = motor1Speed
        if(motor1 == Int.MAX_VALUE)
            motor1 = motor0Speed
        val data = ByteArray(2)
        data[0] = SingleByteUtil.getDriveSpeed(motor0Speed.toByte(), 0)
        data[1] = SingleByteUtil.getDriveSpeed(motor1.toByte(), 1)
        return data
    }
}