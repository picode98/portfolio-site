package net.saamankhali.portfoliosite

// Ported from https://stackoverflow.com/a/9855338
fun ByteArray.toHexString(): String
{
    val HEX_CHARS: CharArray = "0123456789ABCDEF".toCharArray()
    val resultArray = CharArray(this.size * 2)

    for (i in this.indices)
    {
        val thisByte: Int = this[i].toInt() and 0xFF
        resultArray[i * 2] = HEX_CHARS[thisByte ushr 4]
        resultArray[i * 2 + 1] = HEX_CHARS[thisByte and 0x0F]
    }

    return String(resultArray)
}