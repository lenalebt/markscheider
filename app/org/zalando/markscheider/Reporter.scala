package org.zalando.markscheider

import java.io.File
import java.util.concurrent.TimeUnit

import com.codahale.metrics.{ ConsoleReporter, CsvReporter, MetricRegistry }
import play.api.{ Configuration, Logging }

object Reporter extends Logging {
  def console(conf: Configuration, registry: MetricRegistry): () => Any = {
    for {
      unit <- conf.getOptional[String]("unit")
      period <- conf.getOptional[Int]("period")
      _ <- conf.getOptional[String]("prefix")
    } yield () => {
      logger.info("Enabling ConsoleReporter")

      ConsoleReporter.forRegistry(registry)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .convertRatesTo(TimeUnit.SECONDS)
        .build().start(period, TimeUnit.valueOf(unit))
    }
  }.getOrElse(() => Unit)

  def csv(conf: Configuration, registry: MetricRegistry): () => Any = {
    for {
      outputDir <- conf.getOptional[String]("output")
      unit <- conf.getOptional[String]("unit")
      period <- conf.getOptional[Int]("period")
      _ <- conf.getOptional[String]("prefix")
    } yield () => {
      logger.info("Enabling CsvReporter")

      CsvReporter.forRegistry(registry)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .convertRatesTo(TimeUnit.SECONDS)
        .build(new File(outputDir)).start(period, TimeUnit.valueOf(unit))
    }
  }.getOrElse(() => Unit)

}
