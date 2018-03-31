package org.approvaltests.tests;

import org.approvaltests.Approvals;

import junit.framework.TestCase;

public class EnumerationsTest extends TestCase
{
  public void testNumbers() throws Exception
  {
    Approvals.verifyAll("i", new Integer[]{5, 4, 3, 2, 1});
  }
  public void testNumbersWithHeader() throws Exception
  {
    Approvals.verifyAll("The Numbers", "i", new Integer[]{5, 4, 3, 2, 1});
  }
  public void testNumbersWithLambdas() throws Exception
  {
    String[] numbers = new String[]{"one", "two", "three", "four"};
    Approvals.verifyAll(numbers, a -> a + " => " + a.length());
  }
  public void testNumbersWithLambdasAndHeader() throws Exception
  {
    String[] numbers = new String[]{"one", "two", "three", "four"};
    Approvals.verifyAll("Lengths of Strings", numbers, a -> a + " => " + a.length());
  }
}
