import RouletteDisplay.startDisplay
import isel.leic.utils.Time

object RouletteDisplay {

    private const val DISPLAY_ON = 0x07
    private const val DISPLAY_OFF = 0x0F
    private const val DISPLAY_UPDATE = 0x06
    private const val DISPLAY_SIZE = 8
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
            DISPLAY_SIZE
        )
        // Atualiza o display
        SerialEmitter.send(
            SerialEmitter.Destination.ROULETTE,
            DISPLAY_UPDATE,
            DISPLAY_SIZE
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

    fun bettingPhase(
        keys: String,
        counts: MutableList<Int>,
        coinsInit: Int,
        updateCounts: (List<Int>) -> Unit,
        waitKey: (Long) -> Char
    ): Int {
        var coins = coinsInit
        val digits = coins.toString().reversed().map { it.digitToInt() }
        val nDisplays = displays.drop(digits.size)

        digits.forEachIndexed { idx, _ -> setValue(coins) }

        forSeconds(5) {
            loop.forEach { value ->
                nDisplays.forEach { displayIdx ->
                    sendDigitToDisplay(value, displayIdx)
                }
                val key = waitKey(100)
                val idx = keys.indexOf(key)
                if (idx != -1 && counts[idx] < 9 && coins > 0) {
                    counts[idx]++
                    coins--
                    updateCounts(counts)
                }
                Time.sleep(100)
            }
        }
        return coins
    }

    fun animation(coins: Int): Int{
        var sleepTime = 100L

        // Valor inicial aleatório
        var random = (0..13).random()

        // Mostra valores crescentes, voltando ao início quando chega a 14
        repeat(8) {
            displays.forEach { disp ->
                sendDigitToDisplay(random, disp)
            }
            random = (random + 1) % 14
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
        val winnings = count
        val winningsStr = winnings.toString().reversed()
        val winnerStr = if (winningNumber < 10) winningNumber.toString() else "ABCD"[winningNumber - 10].toString()
        val totalDisplays = displays.size
        val spaceBetweenNumbers = totalDisplays - winningsStr.length - winnerStr.length
        val displayStr = winningsStr + "-".repeat(spaceBetweenNumbers) + winnerStr

        displayStr.forEachIndexed { idx, char ->
            val digit = when {
                char == '-' -> if(winner) 0x18 else 0x10
                char in "ABCD" -> 0x0A + "ABCD".indexOf(char) // 0x0A para A, 0x0B para B, etc.
                else -> char.digitToInt()
            }
            sendDigitToDisplay(digit, idx)
        }
        Time.sleep(4000)
        println("Vencedor: $winner, Número: $winningNumber, Contagem: $count, Ganhos: $winnings")
    }

    fun off(value: Boolean) {
        // Liga ou desliga o display
        SerialEmitter.send(
            SerialEmitter.Destination.ROULETTE,
            if (value) DISPLAY_OFF else DISPLAY_ON,
            DISPLAY_SIZE
        )
    }
}