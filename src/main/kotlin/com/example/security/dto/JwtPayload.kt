package com.example.security.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.security.core.GrantedAuthority

class JwtPayload @JsonCreator constructor(
    @JsonProperty("name") var name: String,
    @JsonProperty("authorities") var authorities: Set<Authority>)

class Authority @JsonCreator constructor(
    @JsonDeserialize(using = RightDeserializer::class)
    @JsonSerialize(using = RightSerializer::class)
    @JsonProperty("right")
    var right: Right
): GrantedAuthority {
    @JsonIgnore
    override fun getAuthority(): String? {
        return right.name
    }
}

enum class Right {
    CREATE_USER, DELETE_USER, READ_USER, UPDATE_USER
}

class RightDeserializer: JsonDeserializer<Right>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Right? {
        return p?.text?.let { Right.valueOf(it) }
    }
}

class RightSerializer: JsonSerializer<Right>() {
    override fun serialize(value: Right?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(value?.name)
    }
}