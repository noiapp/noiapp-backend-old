package org.dpppt.backend.sdk.data.util;

import org.junit.jupiter.api.extension.Extension;

public class DBContainerJUnitExtension implements Extension {

    public DBContainerJUnitExtension() {
        SingletonPostgresContainer.getInstance().start();
    }
}
