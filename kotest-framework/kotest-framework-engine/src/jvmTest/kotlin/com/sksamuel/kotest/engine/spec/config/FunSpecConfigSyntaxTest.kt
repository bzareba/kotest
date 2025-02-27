package com.sksamuel.kotest.engine.spec.config

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.seconds

object Tag1 : Tag()
object Tag2 : Tag()

/**
 * A test that just ensures the syntax for test configs does not break between releases.
 * The actual functionality of things like tags and timeouts is tested elsewhere.
 */
@ExperimentalKotest
class FunSpecConfigSyntaxTest : FunSpec() {
   init {

      val counter = AtomicInteger(0)

      afterSpec {
         counter.get() shouldBe 21
      }

      test("a test disabled by an enabled flag").config(enabled = false) {
         error("boom")
      }

      test("a test disabled by an enabled function").config(enabledIf = { System.currentTimeMillis() == 0L }) {
         error("boom")
      }

      test("a test with multiple invocations").config(invocations = 2) {
         counter.incrementAndGet()
      }

      test("a test with multiple threads").config(threads = 2, invocations = 3) {
         counter.incrementAndGet()
      }

      test("a test with timeout").config(timeout = 1.seconds) {
          counter.incrementAndGet()
      }

      test("a test with tags").config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
      }

      test("a test with multiple tags").config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
      }

      context("an outer context with timeout").config(timeout = 2.seconds) {
          counter.incrementAndGet()
          test("an inner test") {
              counter.incrementAndGet()
          }
      }

      context("an outer context with tags").config(tags = setOf(Tag1)) {
         counter.incrementAndGet()
         test("an inner test") {
            counter.incrementAndGet()
         }
      }

      context("an outer context with multiple tags").config(tags = setOf(Tag1, Tag2)) {
         counter.incrementAndGet()
         test("an inner test") {
            counter.incrementAndGet()
         }
      }

      context("an outer context disabled by an enabled flag").config(enabled = false) {
         error("boom")
         test("an inner test") { error("boom") }
      }

      context("an outer context disabled by an enabled function").config(enabledIf = { System.currentTimeMillis() == 0L }) {
         error("boom")
         test("an inner test") { error("boom") }
      }

      context("an outer context") {
         counter.incrementAndGet()
         context("an inner context with timeout").config(timeout = 2.seconds) {
             counter.incrementAndGet()
             test("an inner test") {
                 counter.incrementAndGet()
             }
         }

         context("an inner context with tags").config(tags = setOf(Tag1)) {
            counter.incrementAndGet()
            test("an inner test") {
               counter.incrementAndGet()
            }
         }

         context("an inner context with multiple tags").config(tags = setOf(Tag1, Tag2)) {
            counter.incrementAndGet()
            test("an inner test") {
               counter.incrementAndGet()
            }
         }

         context("an inner context disabled by an enabled flag").config(enabled = false) {
            error("boom")
            test("an inner test") { error("boom") }
         }

         context("an inner context disabled by an enabled function").config(enabledIf = { System.currentTimeMillis() == 0L }) {
            error("boom")
            test("an inner test") { error("boom") }
         }
      }
   }
}
