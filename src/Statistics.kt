object Statistics {
    data class NumberStats(val number: Int, val bets: Int, val spent: Int)

    private fun parseLine(line: String): NumberStats? {
        val parts = line.split(";")
        if (parts.size == 3) {
            val number = parts[0].toIntOrNull()
            val bets = parts[1].toIntOrNull()
            val spent = parts[2].toIntOrNull()
            if (number != null && bets != null && spent != null)
                return NumberStats(number, bets, spent)
        }
        return null
    }

    fun getAllStats(): List<NumberStats> =
        FileAccess.readRouletteStats()
            .mapNotNull { parseLine(it) }

    fun updateStats(number: Int, coinsWon: Int) {
        val stats = getAllStats().toMutableList()
        val idx = stats.indexOfFirst { it.number == number }
        if (idx != -1) {
            val old = stats[idx]
            stats[idx] = old.copy(bets = old.bets + 1, spent = old.spent + coinsWon)
        }
        FileAccess.writeRouletteStats(
            stats.map { "${it.number};${it.bets};${it.spent}" }
        )
    }
}