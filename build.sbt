lazy val `domain-common` = ProjectBuilder.common("domain")
lazy val `kafka-common` = ProjectBuilder.common("kafka")

lazy val `candles` =
  ProjectBuilder
    .service("candles")
    .settings(
      libraryDependencies ++= Dependencies.`akka-streams`,
      libraryDependencies ++= Dependencies.postgress
    )
    .dependsOn(
      `domain-common`,
      `kafka-common`,
    )
