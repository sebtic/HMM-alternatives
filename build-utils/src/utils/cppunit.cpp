#include <stdio.h>
#include <unistd.h>
#include <cppunit/extensions/TestFactoryRegistry.h>
#include <cppunit/ui/text/TestRunner.h>
#include <cppunit/TextTestProgressListener.h>
#include <cppunit/BriefTestProgressListener.h>
#include <cppunit/CompilerOutputter.h>
#include <cppunit/TestResult.h>
#include <cppunit/TestResultCollector.h>

int main(int, char **)
{
  /* Create the event manager and test controller */
  CPPUNIT_NS::TestResult controller;

  /* Add a listener that collects test results */
  CPPUNIT_NS::TestResultCollector result;
  controller.addListener( &result);

  /* Add a listener that print dots as test run. */
  CPPUNIT_NS::BriefTestProgressListener progress;
  /* CPPUNIT_NS::TextTestProgressListener progress; */
  controller.addListener(&progress);

  /* Add the top suite to the test runner */
  CPPUNIT_NS::TestRunner runner;
  runner.addTest(CPPUNIT_NS::TestFactoryRegistry::getRegistry().makeTest() );
  runner.run(controller);

  /* Print test results in a compiler compatible format. */
  CPPUNIT_NS::CompilerOutputter outputter( &result, CPPUNIT_NS::stdCErr() );
  outputter.write();

  return result.wasSuccessful() ? 0 : 1;
}
