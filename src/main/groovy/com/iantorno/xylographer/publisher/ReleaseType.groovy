package com.iantorno.xylographer.publisher

enum ReleaseType {
    VERSION_MAJOR("major"),
    VERSION_MINOR("minor"),
    VERSION_REVISION("revision"),
    VERSION_BUILD("build")

    final String mIdLabel

    ReleaseType(String identifyingLabel) {
        mIdLabel = identifyingLabel
    }

    String getIdentifyingLabel() {
        return mIdLabel
    }
}