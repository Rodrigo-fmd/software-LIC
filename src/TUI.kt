import isel.leic.utils.Time

fun main() {
    HAL.init()
    KBD.init()
    LCD.init()
    var position = 0
    val maxPosition = 32 // 16 colunas * 2 linhas

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
}