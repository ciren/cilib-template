inThisBuild(
  List(
    scalaVersion := "2.12.15",
    run / fork := true
  )
)

//val cilibSHA = "9ae2af8e64622334f8b5e523ecd045b335419097"
val cilibVersion = "2.0.0+97-d11405f0-SNAPSHOT"
val benchmarksSHA = "b961ceb0ca917a5e9f466e5075c8a11fb716bd73"

//lazy val cilibCore = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(cilibSHA)), "core")
//lazy val cilibPSO = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(cilibSHA)), "pso")
//lazy val cilibExec = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(cilibSHA)), "exec")
//lazy val cilibIO = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(cilibSHA)), "io")
lazy val benchmarksMaster = ProjectRef(uri("git://github.com/ciren/benchmarks.git#%s".format(benchmarksSHA)), "benchmarks")


lazy val root = (project in file("."))
  .dependsOn(benchmarksMaster)
  .settings(
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "net.cilib" %% "core" % cilibVersion,
      "net.cilib" %% "pso"  % cilibVersion,
      "net.cilib" %% "io"   % cilibVersion,
      "net.cilib" %% "exec" % cilibVersion
    ))
