package jp.skypencil.errorprone;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import com.google.errorprone.bugpatterns.BugChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FooBarNamingCheckTest {
  @Test
  void renameFooMethod() {
    BugChecker checker = new FooBarNamingCheck();
    BugCheckerRefactoringTestHelper helper =
        BugCheckerRefactoringTestHelper.newInstance(checker, getClass());
    helper
        .addInputLines("Foo.java", "public class Foo { void foo() {} }")
        .addOutputLines("Foo.java", "public class Foo { void bar() {} }")
        .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
  }

  // According to JUnit Jupiter, the nested test class should be non-static
  // https://junit.org/junit5/docs/current/user-guide/#writing-tests-nested
  @SuppressWarnings("ClassCanBeStatic")
  @Nested
  class CompilationTest {
    private CompilationTestHelper helper;

    @BeforeEach
    void setup() {
      helper = CompilationTestHelper.newInstance(FooBarNamingCheck.class, getClass());
    }

    @Test
    void testFooMethod() {
      helper
          .addSourceLines(
              "Foo.java",
              "public class Foo { \n"
                  + "// BUG: Diagnostic contains: foo() should be renamed to bar()\n"
                  + "void foo() {} }")
          .doTest();
    }

    @Test
    void testBarMethod() {
      helper
          .addSourceLines("Foo.java", "public class Foo { void bar() {} }")
          .expectNoDiagnostics()
          .doTest();
    }
  }
}
