/*
 * Created by Ubique Innovation AG
 * https://www.ubique.ch
 * Copyright (c) 2020. All rights reserved.
 */

package org.dpppt.backend.sdk.data;

import java.nio.ByteBuffer;

import org.springframework.util.DigestUtils;

public class EtagGenerator implements EtagGeneratorInterface {

    // TODO make configurable
    private byte[] secret = new byte[]{'s', 'e', 'c', 'r', 'e', 't'};

    @Override
    public String getEtag(int primaryKey) {
        return DigestUtils.md5DigestAsHex(ByteBuffer.allocate(10).putInt(primaryKey).put(secret).array());
    }

}
