import  isel.leic.utils.Time

fun main() {
    HAL.init()
    SerialEmitter.init()
    LCD.init()
    TUI.init()
    RouletteDisplay.init()

    while (true) {
        TUI.clear()
        TUI.writeCentered(0, "Roulete Game")
        var coins = 70
        TUI.writeCentered(1, "1 * 2 * 3 * $${coins}")

        while (true) {
            RouletteDisplay.startDisplay()
            val key = TUI.waitKey(100)
            if (key == '*') break
        }

        TUI.clear()
        val keys = "0123456789ABCD"
        val counts = MutableList(keys.length) { 0 }
        TUI.writeCentered(1, keys)

        fun updateCountsDisplay() {
            val counters = counts.joinToString("") { if (it > 0) it.toString() else " " }
            TUI.writeCentered(0, counters.padEnd(keys.length))
        }

        updateCountsDisplay()

        while (coins > 0) {
            RouletteDisplay.setValue(coins)
            val key = TUI.waitKey(100)
            val idx = keys.indexOf(key)
            if (idx != -1) {
                counts[idx]++
                coins--
                updateCountsDisplay()
            }
            if (key == '#') break
        }

        val winningNumber = RouletteDisplay.animation(coins)
        val bet = counts[winningNumber]
        val winner = counts[winningNumber] > 0

        RouletteDisplay.won(winningNumber, bet, winner)

        Time.sleep(1000)
    }
}