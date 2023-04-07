import net.ltgt.gradle.errorprone.errorprone

plugins {
    `java-library`
    `maven-publish`
    `jacoco`
    id("com.diffplug.spotless") version "6.18.0"
    id("net.ltgt.errorprone") version "3.0.1"
}

repositories { mavenCentral() }

val junitVersion = "5.9.2"
val errorproneVersion = "2.18.0"
val autoServiceVersion = "1.0.1"

dependencies {
    compileOnly("com.google.errorprone:error_prone_check_api:$errorproneVersion")
    compileOnly("com.google.auto.service:auto-service:$autoServiceVersion")
    annotationProcessor("com.google.auto.service:auto-service:$autoServiceVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("com.google.errorprone:error_prone_test_helpers:$errorproneVersion")
    errorprone("com.google.errorprone:error_prone_core:$errorproneVersion")
}

val exportsArgs = listOf(
    "--add-exports",
    "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-exports",
    "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-exports",
    "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
    "--add-exports",
    "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-exports",
    "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
    "--add-exports",
    "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
    "--add-exports",
    "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-exports",
    "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
)

val addExportsFile = file("$buildDir/tmp/javadoc/add-exports.txt")
val createJavadocOptionFile by tasks.registering {
    outputs.file(addExportsFile)
    doLast {
        addExportsFile.printWriter().use { writer ->
            exportsArgs.chunked(2).forEach {
                writer.println("${it[0]}=${it[1]}")
            }
        }
    }
}

tasks {
    withType<JavaCompile>() {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.compilerArgs.addAll(exportsArgs)
        // disable warnings in generated code by immutables
        // https://github.com/google/error-prone/issues/329
        options.errorprone.disableWarningsInGeneratedCode.set(true)
    }

    withType<Javadoc> {
        dependsOn(createJavadocOptionFile)
        options.optionFiles(addExportsFile)
    }

    test {
        useJUnitPlatform()
        // Starting in JDK 16 the default disallows access to internal javac APIs
        // https://github.com/google/error-prone/pull/2015
        jvmArgs = exportsArgs
    }

    check {
        dependsOn("jacocoTestReport")
    }
}

spotless {
    java {
        removeUnusedImports()
        googleJavaFormat()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
        indentWithSpaces()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

defaultTasks("spotlessApply", "build")
