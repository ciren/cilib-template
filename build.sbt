inThisBuild(
  List(
    scalaVersion := "2.12.14",
    run / fork := true
  )
)

val revisionSHA = "c9119628710b190ea56974bdc284ef0820d9934f"
val benchmarksSHA = "b961ceb0ca917a5e9f466e5075c8a11fb716bd73"

lazy val cilibCore = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(revisionSHA)), "core")
lazy val cilibPSO = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(revisionSHA)), "pso")
lazy val cilibExec = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(revisionSHA)), "exec")
lazy val cilibIO = ProjectRef(uri("git://github.com/ciren/cilib.git#%s".format(revisionSHA)), "io")
lazy val benchmarksMaster = ProjectRef(uri("git://github.com/ciren/benchmarks.git#%s".format(benchmarksSHA)), "benchmarks")


lazy val root = (project in file("."))
  .dependsOn(cilibCore, cilibPSO, cilibExec, cilibIO, benchmarksMaster)
