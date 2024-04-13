package data

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class TypeDeserializer : StdDeserializer<EType>(String::class.java) {

    override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): EType? {
        if (p0 == null || p0.text.isNullOrBlank()) {
            return null
        }
        return EType.valueOf(p0.text.substringAfter("TYPE_"))
    }

}