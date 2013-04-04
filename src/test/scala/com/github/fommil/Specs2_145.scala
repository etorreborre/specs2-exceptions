package com.github.fommil

import akka.contrib.jul.JavaLogging
import org.specs2.control.StackTraceFilter
import org.specs2.mutable.Specification
import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.util.Timeout
import org.specs2.specification.{Step, Fragments}
import scala.collection.mutable.ListBuffer


object LoggedStackTraceFilter extends StackTraceFilter with JavaLogging {

  def apply(e: Seq[StackTraceElement]) = Nil

  override def apply[T <: Exception](e: T): T = {
    println(s"STACK TRACE FILTER CALLED FOR ${e.getClass}") // for debugging specs2
    log.error(e, "Specs2")
    // this only works because log.error will construct the LogRecord instantly
    // if the Logger only took a reference to mutable 'e' this would never work.
    e.setStackTrace(new Array[StackTraceElement](0))
    e
  }
}

class Specs2_145 extends TestKit(ActorSystem()) with Specification with JavaLogging {
  args.report(traceFilter = LoggedStackTraceFilter)

  sequential

  implicit def self = testActor

  implicit val timeout = Timeout(10000)

  override def map(fs: => Fragments) = super.map(fs) ^ Step(system.shutdown())

  // remember to pass -Djava.util.logging.config.file=logging.properties
  // to the JVM or you'll get plain J2SE loggers.

  "fyi" should {
    "produce a nicely formatted exception" in {
      "foo" === "bar"
      /* The full stderr is:

SEVERE: Specs2 [java.lang.Thread] (com.github.fommil.LoggedStackTraceFilter$) 'foo' is not equal to 'bar' java.lang.Exception
	at com.github.fommil.Specs2_145$$anonfun$2$$anonfun$apply$3.apply(Specs2_145.scala:40)
	at com.github.fommil.Specs2_145$$anonfun$2$$anonfun$apply$3.apply(Specs2_145.scala:40)
SEVERE: Specs2 [java.lang.Thread] (com.github.fommil.LoggedStackTraceFilter$) AssertionError: assertion failed: expected int, found class java.lang.String org.specs2.execute.Error$ThrowableException
CAUSE: java.lang.AssertionError: assertion failed: expected int, found class java.lang.String
       */
    }
  }

  "demonstration of #145" should {
    "produce an ugly stacktrace when using TestKit" in {

      // part of the reason why I have used TestKit, and not just thrown an
      // AssertionError, is to show how much boilerplate is needed to write
      // an Akka spec. It would be awesome to address this at some point, and
      // perhaps that's what fommil/scala-java-logging's test module will do.

      self ! "ping"
      expectMsgType[Int] === 13 // doing a === feels cleaner than a "success" at the end

      /* In comparison to "fyi" above, the full stderr is:

assertion failed: expected int, found class java.lang.String
org.specs2.execute.Error$ThrowableException: AssertionError: assertion failed: expected int, found class java.lang.String
Caused by: java.lang.AssertionError: assertion failed: expected int, found class java.lang.String
	at scala.Predef$.assert(Predef.scala:179)
	at akka.testkit.TestKitBase$class.expectMsgClass_internal(TestKit.scala:365)
	at akka.testkit.TestKitBase$class.expectMsgType(TestKit.scala:337)
	at akka.testkit.TestKit.expectMsgType(TestKit.scala:637)
	at com.github.fommil.Specs2_145$$anonfun$3$$anonfun$apply$6$$anonfun$apply$1.apply$mcI$sp(Specs2_145.scala:53)
	at com.github.fommil.Specs2_145$$anonfun$3$$anonfun$apply$6$$anonfun$apply$1.apply(Specs2_145.scala:53)
	at com.github.fommil.Specs2_145$$anonfun$3$$anonfun$apply$6$$anonfun$apply$1.apply(Specs2_145.scala:53)
	at org.specs2.matcher.Expectable.value$lzycompute(Expectable.scala:21)
	at org.specs2.matcher.Expectable.value(Expectable.scala:21)
	at org.specs2.matcher.BeTypedEqualTo.apply(AnyMatchers.scala:201)
	at org.specs2.matcher.Expectable.applyMatcher(Expectable.scala:48)
	at org.specs2.matcher.ThrownExpectations$$anon$1.applyMatcher(ThrownExpectations.scala:31)
	at org.specs2.matcher.CanBeEqual$CanBeEqualExpectation.$eq$eq$eq(CanBeEqual.scala:16)
	at com.github.fommil.Specs2_145$$anonfun$3$$anonfun$apply$6.apply(Specs2_145.scala:53)
	at com.github.fommil.Specs2_145$$anonfun$3$$anonfun$apply$6.apply(Specs2_145.scala:45)
	at org.specs2.mutable.SpecificationFeatures$$anon$1$$anonfun$asResult$1.apply(Specification.scala:34)
	at org.specs2.mutable.SpecificationFeatures$$anon$1$$anonfun$asResult$1.apply(Specification.scala:34)
	at org.specs2.execute.AsResult$$anon$10.asResult(Result.scala:230)
	at org.specs2.execute.AsResult$.apply(Result.scala:238)
	at org.specs2.specification.Contexts$$anon$4.apply(Contexts.scala:44)
	at org.specs2.mutable.SpecificationFeatures$$anon$1.asResult(Specification.scala:34)
	at org.specs2.execute.AsResult$.apply(Result.scala:238)
	at org.specs2.specification.Example$$anonfun$apply$1.apply(Fragment.scala:141)
	at org.specs2.specification.Example$$anonfun$apply$1.apply(Fragment.scala:141)
	at org.specs2.specification.Example.execute(Fragment.scala:104)
	at org.specs2.specification.FragmentExecution$$anonfun$1.apply(FragmentExecution.scala:52)
	at org.specs2.specification.FragmentExecution$$anonfun$1.apply(FragmentExecution.scala:52)
	at org.specs2.execute.ResultExecution$class.execute(ResultExecution.scala:22)
	at org.specs2.execute.ResultExecution$.execute(ResultExecution.scala:76)
	at org.specs2.specification.FragmentExecution$class.executeBody(FragmentExecution.scala:28)
	at org.specs2.runner.NotifierRunner$$anon$2$$anon$1.executeBody(NotifierRunner.scala:14)
	at org.specs2.specification.FragmentExecution$class.execute(FragmentExecution.scala:52)
	at org.specs2.runner.NotifierRunner$$anon$2$$anon$1.execute(NotifierRunner.scala:14)
	at org.specs2.specification.FragmentExecution$$anonfun$executeFragment$1$$anonfun$apply$1.apply(FragmentExecution.scala:35)
	at org.specs2.specification.FragmentExecution$$anonfun$executeFragment$1$$anonfun$apply$1.apply(FragmentExecution.scala:35)
	at org.specs2.control.Exceptions$class.catchAllOr(Exceptions.scala:54)
	at org.specs2.control.Exceptions$.catchAllOr(Exceptions.scala:109)
	at org.specs2.specification.FragmentExecution$$anonfun$executeFragment$1.apply(FragmentExecution.scala:35)
	at org.specs2.specification.FragmentExecution$$anonfun$executeFragment$1.apply(FragmentExecution.scala:35)
	at org.specs2.reporter.DefaultExecutionStrategy$$anonfun$org$specs2$reporter$DefaultExecutionStrategy$$executeSequence$1.apply(ExecutionStrategy.scala:99)
	at org.specs2.reporter.DefaultExecutionStrategy$$anonfun$org$specs2$reporter$DefaultExecutionStrategy$$executeSequence$1.apply(ExecutionStrategy.scala:99)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)
	at scala.collection.TraversableLike$$anonfun$map$1.apply(TraversableLike.scala:244)
	at scala.collection.immutable.List.foreach(List.scala:318)
	at scala.collection.TraversableLike$class.map(TraversableLike.scala:244)
	at scala.collection.AbstractTraversable.map(Traversable.scala:105)
	at org.specs2.reporter.DefaultExecutionStrategy$class.org$specs2$reporter$DefaultExecutionStrategy$$executeSequence(ExecutionStrategy.scala:99)
	at org.specs2.reporter.DefaultExecutionStrategy$$anonfun$execute$1$$anonfun$2.apply(ExecutionStrategy.scala:43)
	at org.specs2.reporter.DefaultExecutionStrategy$$anonfun$execute$1$$anonfun$2.apply(ExecutionStrategy.scala:41)
	at scala.collection.TraversableOnce$$anonfun$foldLeft$1.apply(TraversableOnce.scala:144)
	at scala.collection.TraversableOnce$$anonfun$foldLeft$1.apply(TraversableOnce.scala:144)
	at scala.collection.GenTraversableViewLike$Mapped$$anonfun$foreach$2.apply(GenTraversableViewLike.scala:81)
	at scala.collection.Iterator$class.foreach(Iterator.scala:727)
	at scala.collection.AbstractIterator.foreach(Iterator.scala:1157)
	at scala.collection.IterableViewLike$Transformed$class.foreach(IterableViewLike.scala:42)
	at scala.collection.SeqViewLike$AbstractTransformed.foreach(SeqViewLike.scala:43)
	at scala.collection.GenTraversableViewLike$Appended$class.foreach(GenTraversableViewLike.scala:99)
	at scala.collection.SeqViewLike$$anon$2.foreach(SeqViewLike.scala:77)
	at scala.collection.GenTraversableViewLike$Mapped$class.foreach(GenTraversableViewLike.scala:80)
	at scala.collection.SeqViewLike$$anon$3.foreach(SeqViewLike.scala:78)
	at scala.collection.TraversableOnce$class.foldLeft(TraversableOnce.scala:144)
	at scala.collection.SeqViewLike$AbstractTransformed.foldLeft(SeqViewLike.scala:43)
	at org.specs2.reporter.DefaultExecutionStrategy$$anonfun$execute$1.apply(ExecutionStrategy.scala:41)
	at org.specs2.reporter.DefaultExecutionStrategy$$anonfun$execute$1.apply(ExecutionStrategy.scala:38)
	at org.specs2.internal.scalaz.syntax.IdOps$class.$bar$greater(IdOps.scala:15)
	at org.specs2.internal.scalaz.syntax.ToIdOps$$anon$1.$bar$greater(IdOps.scala:68)
	at org.specs2.reporter.Reporter$class.report(Reporter.scala:44)
	at org.specs2.runner.NotifierRunner$$anon$2$$anon$1.report(NotifierRunner.scala:14)
	at org.specs2.runner.ClassRunner$$anonfun$apply$1$$anonfun$apply$2.apply(ClassRunner.scala:58)
	at org.specs2.runner.ClassRunner$$anonfun$apply$1$$anonfun$apply$2.apply(ClassRunner.scala:58)
	at org.specs2.control.Exceptions$class.tryo(Exceptions.scala:32)
	at org.specs2.control.Exceptions$.tryo(Exceptions.scala:109)
	at org.specs2.runner.ClassRunner$$anonfun$apply$1.apply(ClassRunner.scala:58)
	at org.specs2.runner.ClassRunner$$anonfun$apply$1.apply(ClassRunner.scala:57)
	at scala.collection.TraversableLike$$anonfun$flatMap$1.apply(TraversableLike.scala:251)
	at scala.collection.TraversableLike$$anonfun$flatMap$1.apply(TraversableLike.scala:251)
	at scala.collection.IndexedSeqOptimized$class.foreach(IndexedSeqOptimized.scala:33)
	at scala.collection.mutable.WrappedArray.foreach(WrappedArray.scala:34)
	at scala.collection.TraversableLike$class.flatMap(TraversableLike.scala:251)
	at scala.collection.AbstractTraversable.flatMap(Traversable.scala:105)
	at org.specs2.runner.ClassRunner.apply(ClassRunner.scala:57)
	at org.specs2.runner.ClassRunner.start(ClassRunner.scala:35)
	at org.specs2.runner.NotifierRunner.start(NotifierRunner.scala:25)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:487)
	at org.jetbrains.plugins.scala.testingSupport.specs2.JavaSpecs2Runner.runSingleTest(JavaSpecs2Runner.java:99)
	at org.jetbrains.plugins.scala.testingSupport.specs2.JavaSpecs2Runner.main(JavaSpecs2Runner.java:76)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:487)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:120)
       */
    }
  }


}
