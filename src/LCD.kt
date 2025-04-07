import isel.leic.utils.Time

// Escreve no LCD usando a interface a 4 bits.

object LCD {

    // Dimensão do display.
    const val LINES = 2
    const val COLS = 16
    private const val RS_MASK = 0x10
    private const val DATA_MASK = 0x0F
    private const val LOW_NIBBLE_MASK = 0x0F
    private const val ENABLE_MASK = 0x20
    private const val CURSOR_POSITION_MASK = 0x80


    // Define se a interface e Serie ou Paralela
    private const val SERIAL_INTERFACE = false

    // Escreve um byte de comando / dados no LCD em paralelo
    private fun writeNibbleParallel(rs : Boolean, data : Int){
        if (rs) HAL.setBits(RS_MASK)
        else HAL.clrBits(RS_MASK)
        HAL.setBits(ENABLE_MASK)
        HAL.writeBits(DATA_MASK, data)
        Time.sleep(1)
        HAL.clrBits(ENABLE_MASK)
    }

    // Escreve um byte de comando / dados no LCD em série
    private fun writeNibbleSerial(rs : Boolean, data : Int){
        TODO()
    }

    // Escreve um nibble de comando / dados no LCD
    private fun writeNibble(rs : Boolean, data : Int){
        if (SERIAL_INTERFACE) writeNibbleSerial(rs, data)
        else writeNibbleParallel(rs, data)
    }

    // Escreve um byte de comando / dados no LCD
    private fun writeByte(rs : Boolean ,data : Int){
        writeNibble(rs, data shr 4)
        writeNibble(rs, data.and(LOW_NIBBLE_MASK))
    }

    // Escreve um comando no LCD
    private fun writeCMD(data : Int){
        writeByte(false, data)
    }

    // Escreve um dado no LCD
    private fun writeDATA (data : Int){
        writeByte(true, data)
    }

    // Envia a sequência de iniciação para comunicação a 4 bits.
    fun init(){
        Time.sleep(16)
        repeat(3){
            writeNibble(false, 0x03) //set 8 bits
            Time.sleep(5)
        }
        writeNibble(false, 0x02) //set 4 bits
        writeCMD( 0x28) //set 2 lines and 5*8 dots
        writeCMD(0x08) //Display off
        writeCMD( 0x01) //Display clear
        writeCMD(0x06) //Entry mode set
        writeCMD(0x0F) //Display on, cursor on, blink on
    }

    // Escreve um character na posição corrente.
    fun write(c : Char){
        writeDATA(c.code)
    }

    // Escreve uma ‘string’ na posição corrente.
    fun write(text : String){
        for (c in text)
            write(c)
    }

    // Envia comando para posicionar cursor (’line’ : 0 .. LINES - 1 , ’column’ : 0 .. COLS - 1)
    fun cursor(line : Int, column : Int){
        if(line == 0) writeCMD(column or CURSOR_POSITION_MASK)
        else writeCMD((0x40 + column) or CURSOR_POSITION_MASK)
    }

    // Envia comando para limpar o ecrã posicionar o cursor em (0,0)
    fun clear(){
        writeCMD(0x1)
    }
}