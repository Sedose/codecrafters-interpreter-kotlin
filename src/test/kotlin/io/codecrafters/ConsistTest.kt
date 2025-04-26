package io.codecrafters

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class ConsistTest {
  @Test
  fun `restrict inheritance`() {
    Konsist
      .scopeFromProject()
      .classes()
      .filter {
        it
          .parentInterfaces()
          .any { parent ->
            parent.name == "TokenProcessor"
          }
      }.assertTrue { classDef ->
        classDef
          .parents()
          .map { it.sourceDeclaration }
          .all { it is KoInterfaceDeclaration }
      }
  }
}
