import isel.leic.UsbPort

// Ler teclas. Funções retornam '0' ... '9', 'A' ... 'D', ’#’, ’*’ ou NONE.
object KBD {

    private const val NONE = 0
    private const val DVAL_MASK = 0x10
    private const val KEY_CODE_MASK = 0x0F
    private const val ACK_MASK = 0x1
    private const val INIT_VALUE = 0
    private const val KEYS = "147*2580369#ABCD"

    // Inicia a classe
    fun init() = UsbPort.write(INIT_VALUE.and(ACK_MASK))



    // Retorna de imediato a tecla premida ou NONE se nao ha tecla premida.
    fun getKey(): Char {
        if (HAL.isBit(DVAL_MASK)){
            val key = HAL.readBits(KEY_CODE_MASK)
            HAL.setBits(ACK_MASK)
            return KEYS[key]
        }
        else {
            HAL.clrBits(ACK_MASK)
            return NONE.toChar()
        }
    }

    // Retorna a tecla premida, caso ocorra antes do 'timeout' (em milissegundos), ou NONE caso contrario.
    fun waitKey(timeout: Long) : Char{
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeout) {
            val key = getKey()
            if (key != NONE.toChar()){
                return key
            }
        }
        return NONE.toChar()
    }
}

