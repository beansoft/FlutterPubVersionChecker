package pl.pszklarska.pubversionchecker

import com.google.gson.Gson
import com.intellij.util.containers.getIfSingle
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

const val PUB_API_URL = "https://pub.dartlang.org/api/packages/"

class DependencyChecker {

    private val dependencyList = mutableListOf<Dependency>()

    fun getLatestVersion(dependency: String): String {
        val packageName = dependency.getPackageName()
        val url = URL(PUB_API_URL + packageName)
        printMessage("Checking the latest version for: $packageName")

        val cachedDependency = dependencyList.find { it.packageName == packageName }
        if (cachedDependency != null) {
            return cachedDependency.version
        } else {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val reader = connection.inputStream.bufferedReader()
                val response = parsePackageResponse(reader.lines().getIfSingle()!!)
                val latest = response.latest
                val latestVersion = latest.version
                val latestVersionTrim = latestVersion.trim()
                printMessage("Latest version: $latestVersionTrim")
                dependencyList.add(Dependency(packageName, latestVersionTrim))
                return latestVersionTrim
            } catch (e: IOException) {
                println(e)
                throw UnableToGetLatestVersionException(dependency)
            }
        }
    }

    private fun parsePackageResponse(responseString: String): Response {
        return Gson().fromJson(responseString, Response::class.java)
    }

}

class UnableToGetLatestVersionException(dependency: String) :
    Exception("Cannot get the latest version number for dependency: $dependency")

data class Dependency(
    val packageName: String,
    val version: String
)