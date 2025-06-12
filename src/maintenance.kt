object maintenance {
    private const val MAINTENANCE_MASK = 0x80

    fun on(): Boolean{
        return HAL.isBit(MAINTENANCE_MASK)
    }
}