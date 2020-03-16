package jp.skypencil.errorprone;

import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.ProvidesFix;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodTreeMatcher;
import com.google.errorprone.fixes.SuggestedFixes;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.MethodTree;

@AutoService(BugChecker.class)
@BugPattern(
    name = "FooBarNamingCheck",
    summary = "Foo is dead, long live Bar!",
    tags = {"SAMPLE"},
    severity = WARNING,
    providesFix = ProvidesFix.REQUIRES_HUMAN_ATTENTION)
public class FooBarNamingCheck extends BugChecker implements MethodTreeMatcher {
  @Override
  public Description matchMethod(MethodTree tree, VisitorState state) {
    if (tree.getName().contentEquals("foo")) {
      return Description.builder(
              tree, "FooBarNamingCheck", null, WARNING, "foo() should be renamed to bar()")
          .addFix(SuggestedFixes.renameMethod(tree, "bar", state))
          .build();
    }
    return Description.NO_MATCH;
  }
}
