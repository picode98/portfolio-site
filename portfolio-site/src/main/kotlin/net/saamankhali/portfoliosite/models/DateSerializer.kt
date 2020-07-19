package net.saamankhali.portfoliosite.models

import kotlinx.serialization.*
import java.text.SimpleDateFormat
import java.util.*

class DateSerializer : KSerializer<Date> {
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    override val descriptor: SerialDescriptor
        get() = SerialDescriptor("Date", StructureKind.CLASS)

    override fun deserialize(decoder: Decoder): Date {
        return dateFormat.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Date) {
        TODO("Not yet implemented")
    }
}