import cilib._
import cilib.pso._
import cilib.pso.Defaults._
import cilib.exec._
import cilib.io._

import benchmarks._

import eu.timepit.refined.auto._

import spire.implicits._
import spire.math.Interval

import zio.prelude.{ Comparison => _, _ }
import zio.stream._


object Main extends zio.App {

 val bounds = Interval(-5.12, 5.12) ^ 30
  val env =
    Environment(
      cmp = Comparison.dominance(Min),
      eval = Eval.unconstrained((x: NonEmptyList[Double]) => Feasible(Benchmarks.spherical(x)))
    )

  // Define a normal GBest PSO and run it for a single iteration
  val cognitive = Guide.pbest[Mem[Double], Double]
  val social = Guide.gbest[Mem[Double]]
  val gbestPSO = gbest(0.729844, 1.496180, 1.496180, cognitive, social)

  // RVar
  val swarm =
    Position.createCollection(PSO.createParticle(x => Entity(Mem(x, x.zeroed), x)))(bounds, 20)

  val iter = Kleisli(Iteration.sync(gbestPSO))

  val problemStream = Runner.staticProblem("spherical", env.eval)


  type Swarm = NonEmptyList[Particle[Mem[Double], Double]]

  // A data structure to hold the resulting values.
  // Each class member is mapped to a column within the output file
  final case class Results(min: Double, average: Double)

  def extractSolution(collection: Swarm) = {
    val fitnessValues = collection.map(x =>
      x.pos.objective
        .flatMap(_.fitness match {
          case Left(f) =>
            f match {
              case Feasible(v) => Some(v)
              case _           => None
            }
          case _ => None
        })
        .getOrElse(Double.PositiveInfinity)
    )

    Results(fitnessValues.min, fitnessValues.reduceLeft(_ + _) / fitnessValues.size)
  }

  val combinations =
    RNG.initN(50, 123456789L)
      .map(r =>
        Runner.foldStep(env,
          r,
          swarm,
          Runner.staticAlgorithm("GBestPSO", iter),
          problemStream,
          (x: Swarm, _) => RVar.pure(x)
        )
          .map(Runner.measure(extractSolution _))
          .take(1000) // 1000 iterations
      )

  val threads = 4
  val outputFile = new java.io.File("gbest-pso.parquet")

  def run(args: List[String]) = {
    println("preparing to run")

    ZStream.mergeAll(threads)(combinations: _*)
      .run(parquetSink(outputFile))
      .exitCode
  }

}
