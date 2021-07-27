inThisBuild(
  List(
    scalaVersion := "2.12.14"
  )
)

val revisionSHA = "50f927a816b7a3ad6f054ccb5a73e339f92bb01a"
val benchmarksSHA = "master"

lazy val cilibCore = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(revisionSHA)), "core")
lazy val cilibPSO = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(revisionSHA)), "pso")
lazy val cilibExec = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(revisionSHA)), "exec")
lazy val cilibIO = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(revisionSHA)), "io")
lazy val benchmarksMaster = ProjectRef(uri("git://github.com/ciren/benchmarks.git#%s".format(benchmarksSHA)), "benchmarks")


lazy val root = (project in file("."))
  .dependsOn(cilibCore, cilibPSO, cilibExec, cilibIO, benchmarksMaster)
