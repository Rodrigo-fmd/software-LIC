import  isel.leic.utils.Time
import kotlin.text.get

fun main() {
    TUI.init()
    RouletteDisplay.init()

    var coins = 70

    while (true) {
        TUI.clear()
        TUI.writeCentered(0, "Roulette Game")
        TUI.writeCentered(1, "1 * 2 * 3 * $${coins}")

        while (true) {
            RouletteDisplay.startDisplay()
            val key = TUI.waitKey(10)
            if (key == '*') break
        }

        TUI.clear()
        val keys = "0123456789ABCD"
        val counts = MutableList(keys.length) { 0 }
        TUI.writeCentered(1, keys)
        TUI.updateCountsDisplay(counts, keys.length)

        while (true) {
            RouletteDisplay.setValue(coins)
            val key = TUI.waitKey(100)
            val idx = keys.indexOf(key)
            if(idx != -1 && counts[idx] < 9 && coins > 0)
                counts[idx]++

            if(coins > 0 && idx != -1 && counts[idx] < 9) coins--
            TUI.updateCountsDisplay(counts, keys.length)

            if (key == '#' && counts.any { it > 0 }) break
        }

        coins = RouletteDisplay.bettingPhase(
            keys,
            counts,
            coins,
            { TUI.updateCountsDisplay(it, keys.length) },
            { timeout -> TUI.waitKey(timeout) }
        )

        val winningNumber = RouletteDisplay.animation(coins)
        val totalBets = counts.sum()
        val winner = counts[winningNumber] > 0
        val bet = if (winner) counts[winningNumber] * 2 else totalBets

        if (winner) coins += bet

        RouletteDisplay.won(winningNumber, bet, winner)
    }
}