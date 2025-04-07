import isel.leic.UsbPort

object HAL {

    private var output = 0b00000000

    // Inicia o objeto
    fun init() = UsbPort.write(output)

    // Retorna 'true' se o bit definido pela mask esta como valor lógico '1' no UsbPort
    fun isBit(mask : Int) : Boolean = mask.and(UsbPort.read()) == mask

    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask : Int) : Int = mask.and(UsbPort.read())

    // Escreve nos bits representados por mask os valores dos bits correspondentes em value
    fun writeBits(mask : Int, value: Int){
       output = output.and(mask.inv()).or(value.and(mask))
       UsbPort.write(output)
    }

    // Coloca os bits representados por mask no valor lógico '1'
    fun setBits(mask : Int){
        output = mask.or(output)
        UsbPort.write(output)
    }

    // Coloca os bits representados por mask no valor lógico '0'
    fun clrBits(mask : Int){
        output = (mask.inv()).and(output)
        UsbPort.write(output)
    }
}
