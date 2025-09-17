plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6" // Use the latest version available
    application
}

var basename = "offeringManager"
group = "eu.sedimark"
version = "0.11"

repositories {
    mavenCentral()
}

application {
    mainClass.set("eu.sedimark.Main")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Grizzly 2 HTTP Server
    implementation("org.glassfish.jersey.containers:jersey-container-grizzly2-http:3.1.8")

    // Jersey DI, core. jaxb
    implementation("org.glassfish.jersey.core:jersey-server:3.1.8")
    implementation("org.glassfish.jersey.inject:jersey-hk2:3.1.8")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")

    // Jersey Jackson JSON provider
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.1.8")

    // JAX-RS API
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.0.0")

    // PostgreSQL JDBC Driver
    implementation("org.postgresql:postgresql:42.7.7")

    // Hibernate and JPA support
    implementation("org.hibernate.orm:hibernate-core:6.2.8.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // HikariCP for connection pooling
    implementation("com.zaxxer:HikariCP:5.0.1")

    // JSON library
    implementation("org.json:json:+")

    // Retrofit for HTTP Calls
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-scalars:3.0.0") // plain string responses
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")    // JSON conversion

    // JSON-LD processor
    implementation("com.github.jsonld-java:jsonld-java:0.13.6")

    // Jackson for JSON handling
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")

    // Lombok annotation processor
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Apache jena for SHACL validation
    implementation("org.apache.jena:jena-core:5.5.0")
    implementation("org.apache.jena:jena-arq:5.5.0")
    implementation("org.apache.jena:jena-shacl:5.5.0")


//    // Logging (SLF4J)
//    implementation("org.slf4j:slf4j-api:2.0.0")
//    runtimeOnly("org.slf4j:slf4j-simple:2.0.0")
}

tasks {
    shadowJar {
        archiveFileName.set("$basename-$version.jar")
    }
}

//tasks {
//    shadowJar {
//        archiveBaseName.set(basename.toString())  // Set a name for your JAR
//        archiveClassifier.set("")                     // Leave classifier blank so the JAR isn't labeled as "-all"
//        archiveVersion.set(version.toString())        // Optionally, set version if needed
//    }
//}

//tasks.named("startScripts") {
//    dependsOn(tasks.named("shadowJar"))
//}
//
//ktor {
//    fatJar {
//        archiveFileName.set("offeringManager-0.01.jar")
//    }
//}