package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;

public class PyDictEntry {
    private final long baseAddress;
    private int me_hash;
    private int me_key;
    private int meValue;

    private PyStr keyAsStr;

    public PyDictEntry(long baseAddress, IMemoryReader memoryReader) {
        this.baseAddress = baseAddress;

        int[] array = IMemoryReader.readArray(memoryReader, baseAddress, 12);

        if (null == array) {
            return;
        }

        if (0 < array.length) {
            me_hash = array[0];
        }
        if (1 < array.length) {
            me_key = array[1];

            keyAsStr = new PyStr(me_key, memoryReader);
        }

        if (2 < array.length) {
            meValue = array[2];
        }
    }

    private PyDictEntry(long baseAddress, int me_hash, int me_key, int me_value, PyStr keyAsStr) {
        this.baseAddress = baseAddress;
        this.me_hash = me_hash;
        this.me_key = me_key;
        this.meValue = me_value;
        this.keyAsStr = keyAsStr;
    }

    public PyDictEntry clone() {
        return new PyDictEntry(baseAddress, me_hash, me_key, meValue, keyAsStr);
    }

    public String keyStr() {
        return keyAsStr != null ? keyAsStr.getValue() : null;
    }

    public int getMeValue() {
        return meValue;
    }
}
