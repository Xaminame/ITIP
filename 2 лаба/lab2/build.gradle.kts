import java.time.LocalDateTime

plugins {
    id("java")
    application
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass = "org.example.Main"
}

repositories {
    mavenCentral()
}

dependencies {
    // Apache Commons Lang3
    implementation("org.apache.commons:commons-lang3:3.14.0")

    // Logging stack
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.slf4j:slf4j-api:2.0.9")

    // Test dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// Включение интерактивного ввода для задачи run
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.shadowJar {
    manifest {
        attributes(Pair("Main-Class", "org.example.Main"))
    }
}

// Пользовательская задача для вывода информации о проекте
abstract class PrintInfoTask : DefaultTask() {
    @TaskAction
    fun print() {
        println("=============================")
        println("Это моя первая пользовательская задача!")
        println("Проект: ${project.name}")
        println("Версия Gradle: ${project.gradle.gradleVersion}")
        println("=============================")
    }
}

tasks.register<PrintInfoTask>("printInfo") {
    group = "Custom"
    description = "Выводит информацию о проекте"
}

// Задача для генерации паспорта сборки
abstract class GenerateBuildPassportTask : DefaultTask() {
    @get:OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    init {
        outputFile.set(project.file("src/main/resources/build-passport.properties"))
    }

    @TaskAction
    fun generate() {
        val userName = System.getenv("USER") ?: System.getenv("USERNAME") ?: "unknown"
        val osName = System.getProperty("os.name")
        val javaVersion = System.getProperty("java.version")
        val buildTime = LocalDateTime.now().toString()

        val content = """
            |# Build Passport
            |build.user=$userName
            |build.os=$osName
            |build.java.version=$javaVersion
            |build.timestamp=$buildTime
            |build.greeting=Добро пожаловать в приложение lab2!
        """.trimMargin()

        outputFile.get().asFile.parentFile.mkdirs()
        outputFile.get().asFile.writeText(content)
        println("Build passport generated: ${outputFile.get().asFile.absolutePath}")
    }
}

tasks.register<GenerateBuildPassportTask>("generateBuildPassport") {
    group = "Custom"
    description = "Генерирует файл с информацией о сборке"
}

tasks.named("processResources") {
    dependsOn(tasks.named("generateBuildPassport"))
}
