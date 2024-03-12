package com.theblueground.fixtures

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import java.util.Locale

/**
 * A visitor that extracts all the needed information from a data class that was annotated with
 * the [Fixture] annotation. This information will be used by [FixtureBuilderGenerator] in order to
 * generate a helper function which will create test data.
 */
internal class FixtureVisitor(
    processedFixtureAdapters: Map<TypeName, ProcessedFixtureAdapter>,
    private val processedFixtures: MutableMap<KSFile, List<ProcessedFixture>>,
) : KSVisitorVoid() {

    private val processedParameterMapper = ProcessedParameterMapper(
        processedFixtureAdapters = processedFixtureAdapters,
    )

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        require(classDeclaration.isDataClass || classDeclaration.isObject) {
            "$classDeclaration must be a data class or an object to use ${Fixture::class.simpleName} annotation"
        }

        val containingFile = classDeclaration.containingFile!!

        val processedFixture = ProcessedFixture(
            parentName = extractParentName(classDeclaration.parentDeclaration),
            classType = classDeclaration.toClassName(),
            parameters = extractParameters(classDeclaration = classDeclaration),
        )

        processedFixtures[containingFile] = processedFixtures.getOrDefault(containingFile, emptyList())
            .toMutableList()
            .apply { add(processedFixture) }
    }

    private fun extractParameters(
        classDeclaration: KSClassDeclaration,
    ): List<ProcessedFixtureParameter> = classDeclaration.primaryConstructor
        ?.parameters
        .orEmpty()
        .map { processedParameterMapper.mapParameter(parameterValue = it) }

    private fun extractParentName(parentDeclaration: KSDeclaration?): String {
        parentDeclaration ?: return ""

        return extractParentName(parentDeclaration.parentDeclaration) + parentDeclaration.simpleName.asString()
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }
}
