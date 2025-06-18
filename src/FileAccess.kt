import java.io.File

object FileAccess {
    private const val ROULETTE_FILE = "Roulette_Stats.txt"
    private const val STATISTICS_FILE = "statistics.txt"

    fun readRouletteStats(): List<String> =
        File(ROULETTE_FILE).readLines()

    fun writeRouletteStats(lines: List<String>) {
        File(ROULETTE_FILE).writeText(lines.joinToString("\n"))
    }

    fun readStatistics(): List<Int> =
        File(STATISTICS_FILE).readLines().mapNotNull { it.toIntOrNull() }

    fun writeStatistics(values: List<Int>) {
        File(STATISTICS_FILE).writeText(values.joinToString("\n"))
    }
}