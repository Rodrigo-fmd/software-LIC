import  isel.leic.utils.Time
import kotlin.collections.plusAssign
import kotlin.text.get

fun main() {
    TUI.init()
    RouletteDisplay.init()

    var coins = 0
    var coins1= 0
    while (true) {
        var showFirst = true
        var lastSwitch = System.currentTimeMillis()

        while (true) {

            // Verifica se uma moeda foi inserida
            val coinValue = CoinAcceptor.poll()
            println("Coin value: $coinValue")
            if (coinValue > 0) coins1 += coinValue

            val key = TUI.waitKey(10)

            if (maintenance.on()) {
                coins = 100
                val (newShowFirst, newLastSwitch) =
                    TUI.maintenaceInterface(showFirst, lastSwitch)
                showFirst = newShowFirst
                lastSwitch = newLastSwitch
                RouletteDisplay.maintenanceDisplay()

                val key = TUI.waitKey(100)
                when (key) {
                    'D' -> TUI.shutdown()
                    '*' -> break
                    'C' -> { /* TODO */ }
                    'A' -> { /* TODO */ }
                    0.toChar() -> {}
                    else -> showFirst = !showFirst
                }

            }else {
                coins -= coins + coins1
                TUI.initialSreen(coins)
                RouletteDisplay.startDisplay()
                if (key == '*' && coins != 0) break
            }
        }

        TUI.clear()
        val keys = "0123456789ABCD"
        val counts = MutableList(keys.length) { 0 }
        TUI.writeCentered(1, keys)
        TUI.updateCountsDisplay(counts, keys.length)

        while (true) {
            RouletteDisplay.setValue(coins,false)
            val key = TUI.waitKey(100)
            coins = TUI.handleBetInput(key, keys, counts, coins)
            if (key == '#' && counts.any { it > 0 }) break
        }

        // Fase de animação de 5 segundos, ainda permite alterar apostas
        val animationStart = System.currentTimeMillis()
        while (System.currentTimeMillis() - animationStart < 5000) {
            RouletteDisplay.setValue(coins, true)
            val key = TUI.waitKey(100)
            coins = TUI.handleBetInput(key, keys, counts, coins)
        }

        val winningNumber = RouletteDisplay.animation(coins)
        val totalBets = counts.sum()
        val winner = counts[winningNumber] > 0
        val bet = if (winner) counts[winningNumber] * 2 else totalBets

        if (winner) coins += bet

        RouletteDisplay.won(winningNumber, bet, winner)
    }
}