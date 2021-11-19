inThisBuild(
  List(
    scalaVersion := "2.12.15",
    run / fork := true
  )
)

val cilibVersion = "2.0.0+97-d11405f0-SNAPSHOT"
val benchmarksVersion = "0.2.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "net.cilib" %% "core" % cilibVersion,
      "net.cilib" %% "pso"  % cilibVersion,
      "net.cilib" %% "io"   % cilibVersion,
      "net.cilib" %% "exec" % cilibVersion,
      "net.cilib" %% "benchmarks" % benchmarksVersion
    ))
