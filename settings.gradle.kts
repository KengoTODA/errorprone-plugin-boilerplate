plugins { id("com.gradle.enterprise") version "3.13.2" }

rootProject.name = "errorprone-plugin-boilerplate"

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}
