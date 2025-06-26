// src/CoinDeposit.kt
object CoinDeposit {
    // Lê as estatísticas: linha 0 = jogos, linha 1 = moedas
    private fun readStats(): MutableList<Int> {
        val stats = FileAccess.readStatistics().toMutableList()
        // Garante que existem pelo menos 2 linhas
        while (stats.size < 2) stats.add(0)
        return stats
    }

    // Escreve as estatísticas
    private fun writeStats(stats: List<Int>) {
        FileAccess.writeStatistics(stats)
    }

    // Incrementa o número de jogos (linha 0)
    fun incrementGames() {
        val stats = readStats()
        stats[0] = stats[0] + 1
        writeStats(stats)
    }

    // Incrementa o número de moedas (linha 1)
    fun incrementCoins() {
        val stats = readStats()
        stats[1] = stats[1] + 1
        writeStats(stats)
    }

    fun getStats(): List<Int> {
        val stats = FileAccess.readStatistics()
        return if (stats.size < 2) listOf(0, 0) else stats
    }
}