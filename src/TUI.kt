import LCD.COLS
import isel.leic.utils.Time
import kotlin.system.exitProcess

object TUI {

    fun init() {
        HAL.init()
        KBD.init()
        SerialEmitter.init()
        LCD.init()
        LCD.clear()
    }

    fun writeCentered(line: Int, text: String) {
        val displayText = if (text.length > COLS) text.substring(0, COLS) else text
        val padding = ((COLS - displayText.length) / 2).coerceAtLeast(0)
        LCD.cursor(line, 0)
        LCD.write(" ".repeat(padding) + displayText + " ".repeat((COLS - padding - displayText.length).coerceAtLeast(0)))
    }

    fun clear() {
        LCD.clear()
    }

    fun waitKey(timeout: Long = 0): Char {
        return KBD.waitKey(timeout)
    }

    fun writeAt(line: Int, col: Int, text: String) {
        LCD.cursor(line, col)
        LCD.write(text)
    }

    fun updateCountsDisplay(counts: List<Int>, keysLength: Int) {
        val counters = counts.joinToString("") { if (it > 0) it.toString() else " " }
        writeCentered(0, counters.padEnd(keysLength))
    }

    fun cursor(line: Int, col: Int) {
        LCD.cursor(line, col)
    }

    private var lastLine0: String? = null
    private var lastLine1: String? = null

    fun maintenaceInterface(showFirst: Boolean, lastSwitch: Long): Pair<Boolean, Long> {
        val line0 = "On Maintenance"
        val line1 = if (showFirst) "*-Play D-shutD" else "C-stats A-Count"

        if (line0 != lastLine0) {
            writeCentered(0, line0)
            lastLine0 = line0
        }
        if (line1 != lastLine1) {
            writeCentered(1, line1)
            lastLine1 = line1
        }

        var newShowFirst = showFirst
        var newLastSwitch = lastSwitch
        if (System.currentTimeMillis() - lastSwitch >= 2000) {
            newShowFirst = !showFirst
            newLastSwitch = System.currentTimeMillis()
        }
        return Pair(newShowFirst, newLastSwitch)
    }

    fun clearMaintenanceCache() {
        lastLine0 = null
        lastLine1 = null
    }

    fun shutdown() {
        clear()
        writeCentered(0, "Shutdown")
        writeCentered(1, "5-Yes Other-No")
        val confirmKey = waitKey(7000)
        if (confirmKey == '5') {
            exitProcess(0)
        }
    }

    fun initialSreen(coins: Int) {
        clear()
        writeCentered(0, "Roulette Game")
        writeAt(1, 0, "1 * 2 * 3 * $" + coins)
    }

    fun updateCoinsInitial(coins: Int) {
        writeAt(1, 13, "$coins")
    }

    fun handleBetInput(key: Char, keys: String, counts: MutableList<Int>, coins: Int): Int {
        val idx = keys.indexOf(key)
        if (idx != -1 && counts[idx] < 9 && coins > 0)
            counts[idx]++
        val newCoins = if (coins > 0 && idx != -1 && counts[idx] < 9) coins - 1 else coins
        updateCountsDisplay(counts, keys.length)
        return newCoins
    }

    private fun numberToChar(num: Int): String =
        if (num in 10..13) ('A' + (num - 10)).toString() else num.toString()

    fun showStatsPaged() {
        var index = 0
        val totalStats = Statistics.getAllStats()
        if (totalStats.isEmpty()) return

        fun showPage() {
            clear()
            val stat0 = totalStats.getOrNull(index)
            val stat1 = totalStats.getOrNull(index + 1)
            if (stat0 != null)
                writeAt(0, 0, "${numberToChar(stat0.number)}: -> ${stat0.bets} \$:${stat0.spent}")
            if (stat1 != null)
                writeAt(1, 0, "${numberToChar(stat1.number)}: -> ${stat1.bets} \$:${stat1.spent}")
        }

        showPage()
        while (true) {
            val key = waitKey(5000)
            when (key) {
                '8' -> if (index < totalStats.size - 2) { index++; showPage() }
                '2' -> if (index > 0) { index--; showPage() }
                else -> break
            }
        }
    }

    fun showGamesAndCoins(games: Int, coins: Int) {
        clear()
        writeAt(0,0, "Games:$games")
        writeAt(1,0, "Coins:$coins")
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < 5000) {
            val key = waitKey(100)
            if (key != 0.toChar()) break
        }
        clearMaintenanceCache()
    }
}

fun main(){
    TUI.init()
    TUI.clear()
    TUI.writeCentered(0, "Welcome")
    TUI.writeCentered(1, "Press any key to start")

    val key = TUI.waitKey(5000)
    println("Key pressed: $key")
    Time.sleep(2000)
    TUI.initialSreen(100) // Exemplo com 100 moedas iniciais
    Time.sleep(2000)
    TUI.clear()
    TUI.writeAt(0, 0,"ola")
    Time.sleep(2000)
    TUI.clear()

    var showFirst = true
    var lastSwitch = System.currentTimeMillis()

    while (true) {
        // Mostra a interface de manutenção alternando as mensagens
        val (newShowFirst, newLastSwitch) = TUI.maintenaceInterface(showFirst, lastSwitch)
        showFirst = newShowFirst
        lastSwitch = newLastSwitch

        val key = TUI.waitKey(100)
        when (key) {
            'D' -> TUI.shutdown()
            '*' -> break
            0.toChar() -> {}
            else -> showFirst = !showFirst
        }
    }
}
