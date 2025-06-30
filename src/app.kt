import isel.leic.utils.Time

fun main() {
    TUI.init()
    RouletteDisplay.init()

    var coins = 0
    var lastCoins = -1
    var initialScreenDrawn = false

    var showFirst = true
    var lastSwitch = System.currentTimeMillis()

    var inMaintenance = false
    var prevInMaintenance = false
    var savedCoins = 0

    while (true) {
        initialScreenDrawn = false
        while (true) {
            prevInMaintenance = inMaintenance
            inMaintenance = maintenance.on()

            // Ao sair da manutenção, restaurar as moedas
            if (prevInMaintenance && !inMaintenance) {
                coins = savedCoins
                TUI.clearMaintenanceCache()
            }

            if (!inMaintenance && coins < 999) {
                val newCoins = CoinAcceptor.checkAndAdd(coins)
                if (newCoins > coins) {
                    CoinDeposit.incrementCoins()
                }
                coins = minOf(newCoins, 999)
            }

            val key = TUI.waitKey(10)

            if (inMaintenance) {
                initialScreenDrawn = false
                if (!prevInMaintenance) {
                    savedCoins = coins
                }
                coins = 100 // Garante sempre 100 moedas em manutenção
                val (newShowFirst, newLastSwitch) = TUI.maintenaceInterface(showFirst, lastSwitch)
                showFirst = newShowFirst
                lastSwitch = newLastSwitch
                RouletteDisplay.maintenanceDisplay()

                when (key) {
                    'D' -> TUI.shutdown()
                    '*' -> {
                        TUI.clearMaintenanceCache()
                        break
                    }
                    'C' -> {
                        TUI.clearMaintenanceCache()
                        TUI.clear()
                        TUI.showStatsPaged()
                        // Redesenha a interface de manutenção ao voltar
                        val (newShowFirst, newLastSwitch) = TUI.maintenaceInterface(showFirst, lastSwitch)
                        showFirst = newShowFirst
                        lastSwitch = newLastSwitch
                    }
                    'A' -> {
                        val stats = CoinDeposit.getStats()
                        val games = stats[0]
                        val coins = stats[1]
                        TUI.showGamesAndCoins(games, coins)
                    }
                    0.toChar() -> {}
                    else -> showFirst = !showFirst
                }
            } else {
                if (!initialScreenDrawn) {
                    TUI.initialSreen(coins)
                    initialScreenDrawn = true
                    lastCoins = coins
                    TUI.clearMaintenanceCache()
                } else if (coins != lastCoins) {
                    TUI.updateCoinsInitial(coins)
                    lastCoins = coins
                }
                RouletteDisplay.startDisplay()
                if (key == '*' && coins != 0) break
            }
            Time.sleep(75)
        }

        TUI.clear()
        val keys = "0123456789ABCD"
        val counts = MutableList(keys.length) { 0 }
        TUI.writeCentered(1, keys)
        TUI.updateCountsDisplay(counts, keys.length)

        while (true) {
            RouletteDisplay.setValue(coins, false)
            val key = TUI.waitKey(100)
            if (key in keys) {
                coins = TUI.handleBetInput(key, keys, counts, coins)
            }
            if (key == '#' && counts.any { it > 0 }) break
        }

        val animationStart = System.currentTimeMillis()
        while (System.currentTimeMillis() - animationStart < 5000) {
            RouletteDisplay.setValue(coins, true)
            val key = TUI.waitKey(100)
            if (key in keys) {
                coins = TUI.handleBetInput(key, keys, counts, coins)
            }
        }

        val winningNumber = RouletteDisplay.animation(coins)
        val totalBets = counts.sum()
        val winner = counts[winningNumber] > 0
        val bet = if (winner) counts[winningNumber] * 2 else totalBets

        if (winner) coins += bet

        RouletteDisplay.won(winningNumber, bet, winner)
        Statistics.updateStats(winningNumber, if (winner) bet else 0)
        CoinDeposit.incrementGames()
    }
}