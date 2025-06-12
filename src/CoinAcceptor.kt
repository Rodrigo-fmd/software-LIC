import isel.leic.utils.Time

object CoinAcceptor {
    private const val COIN_MASK = 0x40     // I6
    private const val COIN_ID_MASK = 0x20  // I5
    private const val ACCEPT_MASK = 0x40   // O6

    private var lastCoin = true
    private var pendingValue = 0
    private var accepting = false
    private var debounceTimer = 0L

    fun init() {
        lastCoin = HAL.isBit(COIN_MASK)
    }

    fun poll(): Int {
        val coinNow = HAL.isBit(COIN_MASK)
        val now = System.currentTimeMillis()

        // Detecção da borda de descida (1 → 0) com debounce
        if (lastCoin && !coinNow && !accepting && now - debounceTimer > 50) {
            debounceTimer = now
            accepting = true
            val coinId = HAL.isBit(COIN_ID_MASK)
            pendingValue = if (coinId) 4 else 2
        }

        // Quando COIN volta para 1 e temos moeda para aceitar
        if (accepting && coinNow) {
            HAL.setBits(ACCEPT_MASK)
            Time.sleep(50)
            HAL.clrBits(ACCEPT_MASK)

            accepting = false
            lastCoin = coinNow
            val value = pendingValue
            pendingValue = 0
            return value
        }

        lastCoin = coinNow
        return 0
    }
}
