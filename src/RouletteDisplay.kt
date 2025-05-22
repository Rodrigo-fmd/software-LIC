import isel.leic.utils.Time

object RouletteDisplay {

    private const val DISPLAY_ON = 0x07
    private const val DISPLAY_OFF = 0x0F
    private val loop = listOf(0x12, 0x13, 0x14, 0x15, 0x16, 0x11)
    private val displays = (0..5).toList()

    fun init() {
        SerialEmitter.init()
    }

    private fun forSeconds(seconds: Int, block: () -> Unit) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < seconds * 1000) {
            block()
        }
    }

    private fun sendDigitToDisplay(digit: Int, displayIdx: Int) {
        // Envia o dígito para o display correspondente
        SerialEmitter.send(
            SerialEmitter.Destination.ROULETTE,
            (digit shl 3) or displayIdx,
            9
        )
    }

    fun startDisplay() {
        loop.forEach { value ->
            displays.forEach { displayIdx ->
                sendDigitToDisplay(value, displayIdx)
            }
            Time.sleep(100)
        }
    }

    fun animation(coins: Int): Int{
        val digits = coins.toString().reversed().map { it.digitToInt() }
        val nDisplays = displays.drop(digits.size)
        var sleepTime = 100L

        // Mostra as moedas nos displays à direita
        digits.forEachIndexed { idx, digit -> sendDigitToDisplay(digit, idx) }

        // Valor inicial aleatório
        var random = (0..15).random()

        forSeconds(5) {
            loop.forEach { value ->
                nDisplays.forEach { displayIdx ->
                    sendDigitToDisplay(value, displayIdx)
                }
                Time.sleep(100)
            }
        }
        // Mostra valores crescentes, voltando ao início quando chega a 16
        repeat(8) {
            displays.forEach { disp ->
                sendDigitToDisplay(random, disp)
            }
            random = (random + 1) % 16
            Time.sleep(sleepTime)
            sleepTime += 150
        }

        // Mostra o valor final
        forSeconds(5) {
            displays.forEach { displayIdx ->
                sendDigitToDisplay(random, displayIdx)
            }
            Time.sleep(100)
            displays.forEach {
                displayIdx -> sendDigitToDisplay(0x1F, displayIdx)
            }
            Time.sleep(100)
        }

        return random

    }

    fun setValue(value: Int) {
        // Escreve os números nos displays
        val digits = value.toString().reversed().map { it.digitToInt() }
        digits.forEachIndexed { idx, digit -> sendDigitToDisplay(digit, idx) }

        // Preenche os restantes com 0
        for (j in digits.size until displays.size)
            sendDigitToDisplay(0x1F, j)
    }

    fun won(winningNumber: Int, count: Int, winner: Boolean) {
        val winnings = count * 2
        val winningsStr = winnings.toString()
        val winnerStr = winningNumber.toString()
        val totalDisplays = displays.size
        val spaceBetweenNumbers = totalDisplays - winningsStr.length - winnerStr.length
        val displayStr = winningsStr + "-".repeat(spaceBetweenNumbers) + winnerStr
        forSeconds(4){
            displayStr.forEachIndexed { idx, char ->
                val digit = when {
                    char == '-' -> if(winner) 0x18 else 0x10
                    else -> char.digitToInt()
                }
                sendDigitToDisplay(digit, idx)
            }
        }
    }

    fun off(value: Boolean) {
        // Liga ou desliga o display
        SerialEmitter.send(
            SerialEmitter.Destination.ROULETTE,
            if (value) DISPLAY_OFF else DISPLAY_ON,
            9
        )
    }
}