package extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.*
import org.gradle.kotlin.dsl.getByType
import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.registerPrintLineCoverageTask() {
    tasks.register("printLineCoverage") {
        group = "verification" // Put into the same group as the `kover` tasks
        dependsOn("koverXmlReport")
        doLast {
            val report = file("$projectDir/build/reports/kover/report.xml")

            val doc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(report)
            val rootNode = doc.firstChild
            var childNode = rootNode.firstChild

            var coveragePercent = 0.0

            while (childNode != null) {
                if (childNode.nodeName == "counter") {
                    val typeAttr = childNode.attributes.getNamedItem("type")
                    if (typeAttr.textContent == "LINE") {
                        val missedAttr = childNode.attributes.getNamedItem("missed")
                        val coveredAttr = childNode.attributes.getNamedItem("covered")

                        val missed = missedAttr.textContent.toLong()
                        val covered = coveredAttr.textContent.toLong()

                        coveragePercent = (covered * 100.0) / (missed + covered)

                        break
                    }
                }
                childNode = childNode.nextSibling
            }

            println("%.1f".format(coveragePercent))
        }
    }
}