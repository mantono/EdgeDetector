package diacheck.java.libs.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import diacheck.java.libs.imageTools.test.ControlFieldReaderTest;
import diacheck.java.libs.imageTools.test.FieldFinderTest;
import diacheck.java.libs.imageTools.test.FieldTest;
import diacheck.java.libs.imageTools.test.ImageReaderTest;
import diacheck.java.libs.imageTools.test.ImageTransformerTest;
import diacheck.java.libs.imageTools.test.ImageValidatorTest;
import diacheck.java.libs.imageTools.test.IntergrationTest;

@RunWith(Suite.class)
@SuiteClasses({ TriangleTest.class, ImageReaderTest.class, ImageValidatorTest.class, FieldTest.class, ImageTransformerTest.class, FieldFinderTest.class, ControlFieldReaderTest.class, IntergrationTest.class })
public class AllTests
{

}
