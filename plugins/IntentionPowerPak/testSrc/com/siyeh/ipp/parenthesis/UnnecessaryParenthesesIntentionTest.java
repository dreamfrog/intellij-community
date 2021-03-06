package com.siyeh.ipp.parenthesis;

import com.siyeh.IntentionPowerPackBundle;
import com.siyeh.ipp.IPPTestCase;

/**
 * @see RemoveUnnecessaryParenthesesIntention
 */
public class UnnecessaryParenthesesIntentionTest extends IPPTestCase {

  public void testPolyadic() { doTest(); }
  public void testCommutative() { doTest(); }
  public void testWrapping() { doTest(); }
  public void testNotCommutative() { assertIntentionNotAvailable(); }
  public void testStringParentheses() { assertIntentionNotAvailable(); }
  public void testComparisonParentheses() { assertIntentionNotAvailable(); }
  public void testNotCommutative2() { doTest(); }

  @Override
  protected String getRelativePath() {
    return "parentheses";
  }

  @Override
  protected String getIntentionName() {
    return IntentionPowerPackBundle.message("remove.unnecessary.parentheses.intention.name");
  }
}