package com.theblueground.fixtures

import com.squareup.kotlinpoet.TypeName
import kotlin.random.Random

/**
 * Generates the default value for a parameter based on a [ProcessedFixtureParameter]. This value
 * will be used in the helper function declaration that will be generated for a data class which was
 * annotated with the [Fixture] annotation.
 */
internal class ParameterValueGenerator {

    private val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    private class NotAssignException(
        parameter: ProcessedFixtureParameter,
    ) : IllegalArgumentException(
        "Could not assign value for ${parameter.typeName}. This type is not supported yet!",
    )

    fun generateDefaultValue(
        randomize: Boolean,
        parameter: ProcessedFixtureParameter,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
        prefix: String,
    ): String = when {
        parameter.type.isNullable && !randomize -> "null" // TODO this branch should be configurable to keep compatibility?
        parameter.type.isNullable && randomize && Random.nextBoolean() -> "null"
        else -> generateParameterValue(
            randomize = randomize,
            parameter = parameter,
            fixtureAdapters = fixtureAdapters,
            prefix = prefix,
        )
    }

    private fun generateParameterValue(
        randomize: Boolean,
        parameter: ProcessedFixtureParameter,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
        prefix: String,
    ): String = when (parameter) {
        is ProcessedFixtureParameter.PrimitiveParameter ->
            generatePrimitiveValue(randomize = randomize, parameter = parameter)
        is ProcessedFixtureParameter.KnownTypeParameter ->
            generateKnownTypeValue(randomize = randomize, parameter = parameter)
        is ProcessedFixtureParameter.FixtureParameter ->
            generateFixtureValue(parameter = parameter, prefix = prefix)
        is ProcessedFixtureParameter.EnumParameter ->
            generateEnumValue(randomize = randomize, parameter = parameter)
        is ProcessedFixtureParameter.SealedParameter ->
            generateSealedValue(randomize = randomize, parameter = parameter, prefix = prefix)
        is ProcessedFixtureParameter.CollectionParameter ->
            generateCollectionValue(parameter = parameter)
        is ProcessedFixtureParameter.FixtureAdapter ->
            generateFixtureAdapterValue(parameter = parameter, fixtureAdapters = fixtureAdapters)
    }

    private fun generatePrimitiveValue(
        randomize: Boolean,
        parameter: ProcessedFixtureParameter.PrimitiveParameter,
    ): String = when (parameter.typeName) {
        "String" -> generateStringValue(randomize = randomize, parameter = parameter)
        "Char" -> generateCharValue(randomize = randomize)
        "Boolean" -> generateBooleanValue(randomize = randomize)
        "Int" -> generateIntValue(randomize = randomize)
        "Long" -> generateLongValue(randomize = randomize)
        "Float" -> generateFloatValue(randomize = randomize)
        "Double" -> generateDoubleValue(randomize = randomize)
        else -> throw NotAssignException(parameter = parameter)
    }

    private fun generateStringValue(
        randomize: Boolean,
        parameter: ProcessedFixtureParameter.PrimitiveParameter,
    ): String = if (randomize) {
        """"${buildRandomString()}""""
    } else {
        "\"${parameter.name}\""
    }

    private fun buildRandomString(): String = (1..Random.nextInt(10))
        .map { allowedChars.random() }
        .joinToString("")

    private fun generateCharValue(randomize: Boolean): String = if (randomize) {
        """"${allowedChars.random()}""""
    } else {
        """"${allowedChars.first()}""""
    }

    private fun generateBooleanValue(randomize: Boolean): String = if (randomize) {
        "${Random.nextBoolean()}"
    } else {
        "false"
    }

    private fun generateIntValue(randomize: Boolean): String = if (randomize) {
        "${Random.nextInt(20)}"
    } else {
        "0"
    }

    private fun generateLongValue(randomize: Boolean): String = if (randomize) {
        "${Random.nextLong(20)}"
    } else {
        "0L"
    }

    private fun generateFloatValue(randomize: Boolean): String = if (randomize) {
        "${Random.nextFloat()}f"
    } else {
        "0f"
    }

    private fun generateDoubleValue(randomize: Boolean): String = if (randomize) {
        "${Random.nextDouble(20.0)}"
    } else {
        "0.0"
    }

    @Suppress("ComplexMethod")
    private fun generateKnownTypeValue(
        randomize: Boolean,
        parameter: ProcessedFixtureParameter.KnownTypeParameter,
    ): String = when (parameter.typeName) {
        "Date" -> generateDateValue(randomize = randomize)
        "TimeZone" -> generateTimeZoneValue(randomize = randomize)
        "UUID" -> generateUUIDValue(randomize = randomize)
        "LocalDate" -> generateLocalDateValue(randomize = randomize)
        "LocalTime" -> generateLocalTimeValue(randomize = randomize)
        "LocalDateTime" -> generateLocalDateTimeValue(randomize = randomize)
        "ZonedDateTime" -> generateZonedDateTimeValue(randomize = randomize)
        "Instant" -> generateInstantValue(randomize = randomize)
        "OffsetTime" -> generateOffsetTimeValue(randomize = randomize)
        "OffsetDateTime" -> generateOffsetDateTimeValue(randomize = randomize)
        "ZoneId" -> generateZoneIdValue(randomize = randomize)
        "Duration" -> generateDuration(randomize = randomize)
        "BigDecimal" -> generateBigDecimalValue(randomize = randomize)
        "BigInteger" -> generateBigIntegerValue(randomize = randomize)
        else -> throw NotAssignException(parameter = parameter)
    }

    private fun generateDateValue(randomize: Boolean): String = if (randomize) {
        "Date()"
    } else {
        "Date(0)"
    }

    private fun generateTimeZoneValue(randomize: Boolean): String = if (randomize) {
        "TimeZone.getTimeZone(TimeZone.getAvailableIDs().random())"
    } else {
        "TimeZone.getTimeZone(\"UTC\")"
    }

    private fun generateUUIDValue(randomize: Boolean): String = if (randomize) {
        "UUID.randomUUID()"
    } else {
        "UUID.fromString(\"00000000-0000-0000-0000-000000000000\")"
    }

    private fun generateDuration(randomize: Boolean): String = if (randomize) {
        "Duration.ofMillis(Random.nextLong(20))"
    } else {
        "Duration.ZERO"
    }

    private fun generateLocalDateValue(randomize: Boolean): String = if (randomize) {
        "LocalDate.now()"
    } else {
        "LocalDate.of(1989, 1, 23)"
    }

    private fun generateLocalTimeValue(randomize: Boolean): String = if (randomize) {
        "LocalTime.now()"
    } else {
        "LocalTime.of(0, 0)"
    }

    private fun generateLocalDateTimeValue(randomize: Boolean): String = if (randomize) {
        "LocalDateTime.now()"
    } else {
        "LocalDateTime.of(1989, 1, 23, 0, 0)"
    }

    private fun generateZonedDateTimeValue(randomize: Boolean): String = if (randomize) {
        "ZonedDateTime.now()"
    } else {
        "ZonedDateTime.of(1989,1,23,0,0,0,0, ZoneId.of(\"UTC\"))"
    }

    private fun generateInstantValue(randomize: Boolean): String = if (randomize) {
        "Instant.now()"
    } else {
        "Instant.EPOCH"
    }

    private fun generateOffsetTimeValue(randomize: Boolean): String = if (randomize) {
        "OffsetTime.now()"
    } else {
        "OffsetTime.MIN"
    }

    private fun generateOffsetDateTimeValue(randomize: Boolean): String = if (randomize) {
        "OffsetDateTime.now()"
    } else {
        "OffsetDateTime.MIN"
    }

    private fun generateZoneIdValue(randomize: Boolean): String = if (randomize) {
        "ZoneId.of(ZoneId.getAvailableZoneIds().random())"
    } else {
        "ZoneId.of(\"UTC\")"
    }

    private fun generateBigDecimalValue(randomize: Boolean): String = if (randomize) {
        "BigDecimal.valueOf(${Random.nextDouble(20.0)})"
    } else {
        "BigDecimal.ZERO"
    }

    private fun generateBigIntegerValue(randomize: Boolean): String = if (randomize) {
        "BigInteger.valueOf(${Random.nextInt(20)})"
    } else {
        "BigInteger.ZERO"
    }

    private fun generateFixtureValue(
        parameter: ProcessedFixtureParameter.FixtureParameter,
        prefix: String,
    ): String = "${parameter.packageName}." + "${prefix}${parameter.typeName}()".replaceFirstChar { it.lowercaseChar() }

    private fun generateEnumValue(
        randomize: Boolean,
        parameter: ProcessedFixtureParameter.EnumParameter,
    ): String {
        val enumEntry = if (randomize) {
            parameter.entries.random()
        } else {
            parameter.entries.first()
        }

        return "${parameter.typeName}.$enumEntry"
    }

    private fun generateSealedValue(
        randomize: Boolean,
        parameter: ProcessedFixtureParameter.SealedParameter,
        prefix: String,
    ): String {
        val entries = parameter.entries.filter { it.isObject || it.isFixture }
        require(entries.isNotEmpty()) {
            "At least one sealed data class annotated with @Fixture is required in: ${parameter.type.toString().removeSuffix("?")}"
        }
        val sealedEntry = if (randomize) {
            entries.random()
        } else {
            entries.first()
        }

        return when {
            sealedEntry.isObject -> "${parameter.typeName}.${sealedEntry.name}"
            sealedEntry.isFixture -> "$prefix${parameter.typeName}${sealedEntry.name}()".replaceFirstChar { it.lowercaseChar() }
            else -> error("${sealedEntry.name} in ${parameter.typeName} was expected to be annotated with @Fixture")
        }
    }

    private fun generateCollectionValue(
        parameter: ProcessedFixtureParameter.CollectionParameter,
    ): String = "empty${parameter.typeName}()"

    private fun generateFixtureAdapterValue(
        parameter: ProcessedFixtureParameter.FixtureAdapter,
        fixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    ): String {
        val adapter = fixtureAdapters[parameter.type]!!
        return "${adapter.packageName}.${adapter.functionName}()"
    }
}
