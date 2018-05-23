package com.thaid.xylographer.model

enum ReleaseType {
    VERSION_MAJOR("major", 99),
    VERSION_MINOR("minor", 99),
    VERSION_REVISION("revision", 99),
    VERSION_BUILD("build", 999)

    final String mIdLabel
    final int mMaxValue

    ReleaseType(String identifyingLabel, int max) {
        mIdLabel = identifyingLabel
        mMaxValue = max
    }

    String getIdentifyingLabel() {
        return mIdLabel
    }

    int getMaxValue() {
        return mMaxValue
    }
}