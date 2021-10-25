import cilib._
import cilib.pso._
import cilib.pso.Defaults._
import cilib.exec._
import cilib.io._

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.auto._

import spire.implicits._
import spire.math.Interval

import zio.prelude.{ Comparison => _, _ }
import zio.stream._


object Main extends zio.App {
  val swarmSize: Int Refined Positive = 20
  val problemDimensions = 10
  val bounds = Interval(-5.12, 5.12) ^ problemDimensions
  val cmp = Comparison.dominance(Min)


  /* Convert the NonEmtpyVector into a AtLeast2List structure which
   * guaraantees that there are 2 or more elements
   */
  def mkAtLeast2List(x: NonEmptyVector[Double]) =
    benchmarks.AtLeast2List.make(x) match {
      case ZValidation.Failure(_, e) => sys.error("Input vector requires at least 2 elements")
      case ZValidation.Success(_, result) => result
    }

  // Define a normal GBest PSO and run it for a single iteration
  val cognitive = Guide.pbest[Mem[Double], Double]
  val social = Guide.gbest[Mem[Double]]
  val gbestPSO = gbest(0.729844, 1.496180, 1.496180, cognitive, social)

  // RVar
  val swarm =
    Position.createCollection(PSO.createParticle(x => Entity(Mem(x, x.zeroed), x)))(bounds, swarmSize)

  // Define the synchronous iteration of the gbestPSO algorithm
  val iter = Kleisli(Iteration.sync(gbestPSO))

  // Create a stream of problems labelled 'f3'
  val problemStream =
    Runner.staticProblem("f3", Eval.unconstrained((x: NonEmptyVector[Double]) => {
      val nev2 = mkAtLeast2List(x)
      Feasible(benchmarks.cec.cec2005.Benchmarks.f3(nev2))
    }))
  //  val problemStream = Runner.staticProblem("f20", eval)
  //  val problemStream = Runner.staticProblem("iris3D", eval)
  //  val problemStream = Runner.staticProblem("ackley", eval)

  type Swarm = NonEmptyVector[Particle[Mem[Double], Double]]

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

    Results(
      min = fitnessValues.toChunk.min,
      average = fitnessValues.toChunk.reduceLeft(_ + _) / fitnessValues.size
    )
  }

  val combinations =
    for {
      r <- RNG.initN(50, 123456789L)
    } yield {
        Runner.foldStep(
          cmp,
          r,
          swarm,
          Runner.staticAlgorithm("GBestPSO", iter),
          problemStream,
          (x: Swarm, _) => RVar.pure(x)
        )
          .map(Runner.measure(extractSolution _))
          .take(1000) // 1000 iterations
    }

  val threads = 1
  val outputFile = new java.io.File("results/gbest-pso_F20_50rruns_cpv01_20part_10dim_repeat.parquet_NEW")
  //val outputFile = new java.io.File("results/gbest-pso_F10_50rruns_"+swarmsize.toString+"70part_50dim_1thread.parquet")

  def run(args: List[String]) = {
    println("Preparing to run")

    ZStream.mergeAll(threads)(combinations: _*)
      .run(parquetSink(outputFile))
      .exitCode
  }
}
