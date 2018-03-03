package com.iantorno.xylographer.publisher

import com.iantorno.xylographer.model.ReleaseType
import org.junit.Assert

class PublishTaskTest extends GroovyTestCase {

    final static String BAD_FORMAT_BRANCH_NAME = "Feature444-I_do_not_listen_to_Mark"

    final static String GOOD_FORMAT_BRANCH_NAME_ONE = "BANTA-123-Adding_Banners"
    final static String PARSED_BRANCH_ID_ONE = "BANTA-123"
    final static String GOOD_FORMAT_BRANCH_NAME_TWO = "ICC-9"
    final static String PARSED_BRANCH_ID_TWO = "ICC-9"
    final static String GOOD_FORMAT_BRANCH_NAME_THREE = "MDLY-65432GARBAGE"
    final static String PARSED_BRANCH_ID_THREE = "MDLY-65432"

    void testGetCurrentBranchSuffix() {
        String testBad = PublishTask.getCurrentBranchSuffix(BAD_FORMAT_BRANCH_NAME)
        Assert.assertEquals(PublishTask.BAD_BRANCH_FORMAT, testBad)
        String testOne = PublishTask.getCurrentBranchSuffix(GOOD_FORMAT_BRANCH_NAME_ONE)
        Assert.assertEquals(PARSED_BRANCH_ID_ONE, testOne)
        String testTwo = PublishTask.getCurrentBranchSuffix(GOOD_FORMAT_BRANCH_NAME_TWO)
        Assert.assertEquals(PARSED_BRANCH_ID_TWO, testTwo)
        String testThree = PublishTask.getCurrentBranchSuffix(GOOD_FORMAT_BRANCH_NAME_THREE)
        Assert.assertEquals(PARSED_BRANCH_ID_THREE, testThree)
    }

    final static String NULL_ID_STRING = null
    final static String NO_REC_ID_STRING = "Where is my mind"
    final static String MAJOR_ID_STRING = "buildMajorRelease"
    final static String MINOR_ID_STRING = "minor"
    final static String REVISION_ID_STRING = "generateRevision"

    void testDetermineReleaseTypeFromIdString() {
        ReleaseType nullInput = PublishTask.determineReleaseTypeFromIdString(NULL_ID_STRING)
        Assert.assertEquals(ReleaseType.VERSION_BUILD, nullInput)
        ReleaseType otherInput = PublishTask.determineReleaseTypeFromIdString(NO_REC_ID_STRING)
        Assert.assertEquals(ReleaseType.VERSION_BUILD, otherInput)
        ReleaseType majorInput = PublishTask.determineReleaseTypeFromIdString(MAJOR_ID_STRING)
        Assert.assertEquals(ReleaseType.VERSION_MAJOR, majorInput)
        ReleaseType minorInput = PublishTask.determineReleaseTypeFromIdString(MINOR_ID_STRING)
        Assert.assertEquals(ReleaseType.VERSION_MINOR, minorInput)
        ReleaseType revisionInput = PublishTask.determineReleaseTypeFromIdString(REVISION_ID_STRING)
        Assert.assertEquals(ReleaseType.VERSION_REVISION, revisionInput)
    }

    final static String DEFAULT_GET_PROP_VALUE = "-1"

    void testInitializeVersionFile() {
        def versionFile = makeTestVersionInfoFile()
        PublishTask.initializeVersionFile(versionFile)
        def versionProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())
    }

    static File makeTestVersionInfoFile() {
        File versionFile = File.createTempFile( "version", ".properties");
        versionFile.deleteOnExit()
        return versionFile
    }

    void testIncrementProperty() {
        def versionFile = makeTestVersionInfoFile()
        PublishTask.initializeVersionFile(versionFile)

        def versionProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())

        //Normal build
        PublishTask.incrementProperty(versionFile, ReleaseType.VERSION_BUILD)
        versionProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(1, versionProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())

        //Normal build
        PublishTask.incrementProperty(versionFile, ReleaseType.VERSION_BUILD)
        versionProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(2, versionProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())

        //Revision build
        PublishTask.incrementProperty(versionFile, ReleaseType.VERSION_REVISION)
        versionProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(1, versionProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())

        //Minor build
        PublishTask.incrementProperty(versionFile, ReleaseType.VERSION_MINOR)
        versionProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(1, versionProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())

        //Major build
        PublishTask.incrementProperty(versionFile, ReleaseType.VERSION_MAJOR)
        versionProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(1, versionProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(0, versionProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())
    }

    static final int TEST_SET_VALUE_ONE = 11
    static final int TEST_SET_VALUE_TWO = 22
    static final int TEST_SET_VALUE_THREE = 33
    static final int TEST_SET_VALUE_FOUR = 44

    void testSetProperty() {
        def versionFile = makeTestVersionInfoFile()
        PublishTask.initializeVersionFile(versionFile)

        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MAJOR, TEST_SET_VALUE_FOUR)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MINOR, TEST_SET_VALUE_THREE)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_REVISION, TEST_SET_VALUE_TWO)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_BUILD, TEST_SET_VALUE_ONE)

        def versionProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(TEST_SET_VALUE_FOUR, versionProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(TEST_SET_VALUE_THREE, versionProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(TEST_SET_VALUE_TWO, versionProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(TEST_SET_VALUE_ONE, versionProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())
    }

    void testValidPropertyValue() {
        for (ReleaseType type: ReleaseType.values()) {
            //Test Max
            Assert.assertFalse(PublishTask.validPropertyValue(type, type.getMaxValue() + 1))
            Assert.assertTrue(PublishTask.validPropertyValue(type, type.getMaxValue()))
            Assert.assertTrue(PublishTask.validPropertyValue(type, type.getMaxValue() - 1))

            //Test Min
            Assert.assertTrue(PublishTask.validPropertyValue(type, 1))
            Assert.assertTrue(PublishTask.validPropertyValue(type, 0))
            Assert.assertFalse(PublishTask.validPropertyValue(type, -1))
        }
    }

    void testGetProperty() {
        def versionFile = makeTestVersionInfoFile()
        PublishTask.initializeVersionFile(versionFile)

        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MAJOR, TEST_SET_VALUE_FOUR)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MINOR, TEST_SET_VALUE_THREE)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_REVISION, TEST_SET_VALUE_TWO)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_BUILD, TEST_SET_VALUE_ONE)

        Assert.assertEquals(TEST_SET_VALUE_FOUR, PublishTask.getProperty(versionFile, ReleaseType.VERSION_MAJOR))
        Assert.assertEquals(TEST_SET_VALUE_THREE, PublishTask.getProperty(versionFile, ReleaseType.VERSION_MINOR))
        Assert.assertEquals(TEST_SET_VALUE_TWO, PublishTask.getProperty(versionFile, ReleaseType.VERSION_REVISION))
        Assert.assertEquals(TEST_SET_VALUE_ONE, PublishTask.getProperty(versionFile, ReleaseType.VERSION_BUILD))
    }

    void testLoadVersionProperties() {
        def versionFile = makeTestVersionInfoFile()
        PublishTask.initializeVersionFile(versionFile)

        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MAJOR, TEST_SET_VALUE_FOUR)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MINOR, TEST_SET_VALUE_THREE)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_REVISION, TEST_SET_VALUE_TWO)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_BUILD, TEST_SET_VALUE_ONE)

        Properties loadedProperties = PublishTask.loadVersionProperties(versionFile)
        Assert.assertEquals(TEST_SET_VALUE_FOUR, loadedProperties.getProperty(ReleaseType.VERSION_MAJOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(TEST_SET_VALUE_THREE, loadedProperties.getProperty(ReleaseType.VERSION_MINOR as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(TEST_SET_VALUE_TWO, loadedProperties.getProperty(ReleaseType.VERSION_REVISION as String, DEFAULT_GET_PROP_VALUE).toInteger())
        Assert.assertEquals(TEST_SET_VALUE_ONE, loadedProperties.getProperty(ReleaseType.VERSION_BUILD as String, DEFAULT_GET_PROP_VALUE).toInteger())
    }

    void testBuildVersionName() {
        def versionFile = makeTestVersionInfoFile()
        PublishTask.initializeVersionFile(versionFile)

        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MAJOR, TEST_SET_VALUE_FOUR)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MINOR, TEST_SET_VALUE_THREE)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_REVISION, TEST_SET_VALUE_TWO)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_BUILD, TEST_SET_VALUE_ONE)

        String versionNameBuild = PublishTask.buildVersionName(versionFile, ReleaseType.VERSION_BUILD)
        String versionNameMajor = PublishTask.buildVersionName(versionFile, ReleaseType.VERSION_MAJOR)
        String versionNameMinor = PublishTask.buildVersionName(versionFile, ReleaseType.VERSION_MINOR)
        String versionNameRevision = PublishTask.buildVersionName(versionFile, ReleaseType.VERSION_REVISION)

        Assert.assertEquals("${TEST_SET_VALUE_FOUR}.${TEST_SET_VALUE_THREE}.${TEST_SET_VALUE_TWO}.${TEST_SET_VALUE_ONE}".toString(), versionNameBuild)
        Assert.assertEquals("${TEST_SET_VALUE_FOUR}.${TEST_SET_VALUE_THREE}.${TEST_SET_VALUE_TWO}".toString(), versionNameMajor)
        Assert.assertEquals("${TEST_SET_VALUE_FOUR}.${TEST_SET_VALUE_THREE}.${TEST_SET_VALUE_TWO}".toString(), versionNameMinor)
        Assert.assertEquals("${TEST_SET_VALUE_FOUR}.${TEST_SET_VALUE_THREE}.${TEST_SET_VALUE_TWO}".toString(), versionNameRevision)
    }

    void testBuildVersionNumber() {
        def versionFile = makeTestVersionInfoFile()
        PublishTask.initializeVersionFile(versionFile)

        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MAJOR, TEST_SET_VALUE_FOUR)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_MINOR, TEST_SET_VALUE_THREE)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_REVISION, TEST_SET_VALUE_TWO)
        PublishTask.setProperty(versionFile, ReleaseType.VERSION_BUILD, TEST_SET_VALUE_ONE)

        int version = PublishTask.buildVersionNumber(versionFile)
        Assert.assertEquals(443322011, version)
    }
}
