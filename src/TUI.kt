import LCD.COLS
import isel.leic.utils.Time

/*fun main() {
    HAL.init()
    KBD.init()
    SerialEmitter.init()
    LCD.init()
    var position = 0
    val maxPosition = LCD.COLS * LCD.LINES // 16 colunas * 2 linhas

    while (true) {
        val key = KBD.waitKey(10000)
        if (key != KBD.NONE.toChar()) {
            if (position >= maxPosition) {
                Time.sleep(3000)
                LCD.clear()
                position = 0
            }
            if (position % LCD.COLS == 0 && position != 0) {
                LCD.cursor(position / LCD.COLS, 0)
            }
            LCD.write(key)
            position++
        }
    }
}*/

object TUI {

    fun init() {
        HAL.init()
        KBD.init()
        SerialEmitter.init()
        LCD.init()
        LCD.clear()
    }

    fun writeCentered(line: Int, text: String) {
        val padding = (COLS - text.length) / 2
        LCD.cursor(line, 0)
        LCD.write(" ".repeat(padding) + text + " ".repeat(COLS - padding - text.length - 1))
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
}
