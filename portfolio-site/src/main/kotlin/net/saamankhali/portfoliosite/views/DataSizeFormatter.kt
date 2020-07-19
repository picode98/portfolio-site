package net.saamankhali.portfoliosite.views

import org.springframework.format.Formatter
import java.util.*

class DataSizeFormatter : Formatter<Long> {
    val DATA_SIZE_SUFFIXES: Array<String> = arrayOf("B", "KB", "MB", "GB", "TB")
    val SIZE_MULTIPLIER: Long = 1024

    override fun print(size: Long, locale: Locale): String {
        var multiplier: Long = 1
        var index: Int = 0

        while(size >= (SIZE_MULTIPLIER * multiplier) && index < (DATA_SIZE_SUFFIXES.size - 1))
        {
            multiplier *= SIZE_MULTIPLIER
            index++
        }

        val sizeNumStr: String = "%.3f".format(size.toDouble() / multiplier)

        return "$sizeNumStr ${DATA_SIZE_SUFFIXES[index]}"
    }

    override fun parse(sizeStr: String, locale: Locale): Long {
        TODO("Not yet implemented")
    }
}