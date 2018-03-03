package com.iantorno.xylographer.publisher

import com.iantorno.xylographer.model.ReleaseType
import org.junit.Assert

/**
 * Created by mark on 2018-03-02.
 */
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

    void testGetOrInitializeVersioningFile() {
    }

    void testGetBuildIdentifierString() {
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

    void testInitializeVersionFile() {
    }

    void testIncrementProperty() {
    }

    void testSetProperty() {
    }

    void testGetProperty() {
    }

    void testLoadVersionProperties() {
    }

    void testPrintCurrentProperties() {
    }

    void testBuildVersionName() {
    }

    void testBuildVersionNumber() {
    }
}
