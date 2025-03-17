// Escreve no LCD usando a interface a 4 bits.
object LCD {

// Dimensão do display.
    private const val LINES = 2
    private const val COLS = 16

// Define se a interface e Serie ou Paralela
    private const val SERIAL_INTERFACE = false

// Escreve um byte de comando / dados no LCD em paralelo
    private fun writeNibbleParallel(rs : Boolean, data : Int){
        TODO()
    }

// Escreve um byte de comando / dados no LCD em série
    private fun writeNibbleSerial(rs : Boolean, data : Int){
        TODO()
    }

// Escreve um nibble de comando / dados no LCD
    private fun writeNibble(rs : Boolean, data : Int){
        TODO()
    }

// Escreve um byte de comando / dados no LCD
    private fun writeByte(rs : Boolean ,data : Int){
        TODO()
    }

// Escreve um comando no LCD
    private fun writeCMD(data : Int){
        TODO()
    }

// Escreve um dado no LCD
    private fun writeDATA (data : Int){
        TODO ()
    }

    // Envia a sequência de iniciação para comunicação a 4 bits.
    fun init(){
        TODO()
    }

    // Escreve um character na posição corrente.
    fun write(c : Char){
        writeDATA(c.code)
    }

    // Escreve uma ‘string’ na posição corrente.
    fun write(text : String){
        for (c in text) {
            write(c)
        }
    }

    // Envia comando para posicionar cursor (’line’ : 0 .. L I N E S - 1 , ’c o l u m n’ : 0 .. COLS - 1)
    fun cursor(line : Int, column : Int){
        TODO()
    }
    // Envia comando para limpar o ecrã posicionar o cursor em (0,0)
    fun clear(){
        TODO()
    }
}