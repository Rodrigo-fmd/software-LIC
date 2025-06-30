import isel.leic.utils.Time

object CoinAcceptor {

    private const val MASK_COIN = 0x40
    private const val MASK_ID = 0x20
    private const val ENABLE_ACCEPT = 0x40

    fun checkAndAdd(currentAmount: Int): Int {
        var updatedAmount = currentAmount

        if (HAL.isBit(MASK_COIN)) {
            HAL.setBits(ENABLE_ACCEPT)
            Time.sleep(25)

            val coinValue = if (HAL.isBit(MASK_ID)) 4 else 2
            updatedAmount += coinValue

            do {
                Time.sleep(10)
            }while (HAL.isBit(MASK_COIN))
            // Wait until coin signal is cleared
            HAL.clrBits(ENABLE_ACCEPT)
        }

        return updatedAmount
    }
}