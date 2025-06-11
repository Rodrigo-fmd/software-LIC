object SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.
    const val SELECT_LCD = 0x01
    const val SELECT_ROULETTE= 0x02
    const val CLOCK_MASK = 0x10
    const val SDX_MASK = 0x08

    enum class Destination {LCD, ROULETTE}
    // Inicia a classe
    fun init(){
        HAL.init()
        HAL.setBits(SELECT_LCD)
        HAL.setBits(SELECT_ROULETTE)
    }
    // Envia a trama para o módulo destino
    fun send(addr: Destination, data: Int, size : Int){
        var activeBits = 0

        when(addr) {
            Destination.LCD -> {
                HAL.clrBits(SELECT_LCD)
                HAL.setBits(SELECT_ROULETTE)
            }
            Destination.ROULETTE -> {
                HAL.clrBits(SELECT_ROULETTE)
                HAL.setBits(SELECT_LCD)
            }
        }

        HAL.clrBits(CLOCK_MASK)

        for (i in 0 until size) {
            val bit = (data shr i) and 0x01
            if (bit == 1) activeBits++

            HAL.writeBits(SDX_MASK, bit shl 3)

            HAL.setBits(CLOCK_MASK)
            HAL.clrBits(CLOCK_MASK)
        }

        if (activeBits % 2 != 0) HAL.clrBits(SDX_MASK)
        else HAL.setBits(SDX_MASK)

        HAL.setBits(CLOCK_MASK)
        HAL.clrBits(CLOCK_MASK)

        HAL.setBits(SELECT_LCD)
        HAL.setBits(SELECT_ROULETTE)
        }
}