import isel.leic.utils.Time

object CoinAcceptor {

    private const val MASK_COIN = 0x40
    private const val MASK_ID = 0x20
    private const val ENABLE_ACCEPT = 0x40

    var previousCoinState = false

    fun checkAndAdd(currentAmount: Int): Int {
        var updatedAmount = currentAmount

        val coinDetected = HAL.isBit(MASK_COIN)

        // Detecta borda de subida (de false para true)
        if (coinDetected && !previousCoinState) {
            HAL.setBits(ENABLE_ACCEPT)
            Time.sleep(25)

            updatedAmount += when (HAL.isBit(MASK_ID)) {
                true -> 4
                false -> 2
            }

            HAL.clrBits(ENABLE_ACCEPT)
        }

        // Atualiza estado anterior
        previousCoinState = coinDetected

        return updatedAmount
    }
}