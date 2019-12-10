package cdst.testocr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BestTextOCRTest.class, FastTextOCRTest.class, OldTextOCRTest.class, StdTextOCRTest.class })
public class AllTests {

}
