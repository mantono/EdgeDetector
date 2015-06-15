package diacheck.java.libs.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import diacheck.java.libs.imageTools.test.FieldTest;
import diacheck.java.libs.imageTools.test.ImageReaderTest;
import diacheck.java.libs.imageTools.test.ImageTransformerTest;

@RunWith(Suite.class)
@SuiteClasses({ TriangleTest.class, ImageReaderTest.class, FieldTest.class, ImageTransformerTest.class })
public class AllTests
{

}
