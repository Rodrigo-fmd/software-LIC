import isel.leic.utils.Time

object RouletteDisplay {

    private const val DISPLAY_ON = 0x07
    private const val DISPLAY_OFF = 0x0F
    private const val DISPLAY_UPDATE = 0x06
    private const val DISPLAY_SIZE = 8
    private val loop = listOf(0x12, 0x13, 0x14, 0x15, 0x16, 0x11)
    private val displays = (0..5).toList()
    private var animationIndex = 0

    fun init() {
        SerialEmitter.init()
        animationIndex = 0
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
        val value = loop[animationIndex % loop.size]
        displays.forEach { displayIdx ->
            sendDigitToDisplay(value, displayIdx)
        }
        animationIndex = (animationIndex + 1) % loop.size
    }

    fun setValue(value: Int, animation: Boolean) {
        val digits = value.toString().reversed().map { it.digitToInt() }
        val nDigits = digits.size
        val nDisplays = displays.size

        // Atualiza sempre os dígitos das moedas imediatamente
        digits.forEachIndexed { idx, digit -> sendDigitToDisplay(digit, idx) }

        if (animation) {
            val value = loop[animationIndex % loop.size]
            // Só anima os displays vazios (à esquerda)
            for (i in nDigits until nDisplays) {
                sendDigitToDisplay(value, i)
            }
            animationIndex = (animationIndex + 1) % loop.size
        } else {
            // Limpa os displays vazios (à esquerda)
            for (j in digits.size until displays.size)
                sendDigitToDisplay(0x1F, j)
        }
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

    fun maintenanceDisplay(){
        displays.forEachIndexed { idx, _ ->sendDigitToDisplay(0x17, idx) }
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

// src/RouletteDisplay.kt
fun main() {
    RouletteDisplay.init()
    RouletteDisplay.off(false)
    while (true) {
        RouletteDisplay.startDisplay()
        Time.sleep(50)
    }
}