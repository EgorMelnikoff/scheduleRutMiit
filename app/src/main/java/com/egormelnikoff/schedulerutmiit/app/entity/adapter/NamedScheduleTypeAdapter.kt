package com.egormelnikoff.schedulerutmiit.app.entity.adapter

import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class NamedScheduleTypeAdapter : TypeAdapter<NamedScheduleType>() {
    override fun write(out: JsonWriter, value: NamedScheduleType?) {
        if (value == null) {
            out.nullValue()
            return
        }

        out.beginObject()
        out.name("id").value(value.id)
        out.name("name").value(value.typeName)
        out.endObject()
    }

    override fun read(reader: JsonReader): NamedScheduleType {
        return when (reader.peek()) {

            JsonToken.NUMBER -> {
                when (reader.nextInt()) {
                    0 -> NamedScheduleType.GROUP
                    1 -> NamedScheduleType.PERSON
                    2 -> NamedScheduleType.ROOM
                    3 -> NamedScheduleType.MY
                    else -> throw JsonParseException("Unknown NamedScheduleType id")
                }
            }

            JsonToken.BEGIN_OBJECT -> {
                var id: Int? = null

                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "id" -> id = reader.nextInt()
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()

                when (id) {
                    0 -> NamedScheduleType.GROUP
                    1 -> NamedScheduleType.PERSON
                    2 -> NamedScheduleType.ROOM
                    3 -> NamedScheduleType.MY
                    else -> throw JsonParseException("Unknown NamedScheduleType id")
                }
            }

            else -> throw JsonParseException("Unexpected token for NamedScheduleType")
        }
    }
}