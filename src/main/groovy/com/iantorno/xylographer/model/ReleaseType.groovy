package com.iantorno.xylographer.model

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