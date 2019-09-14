package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;

public class PyStr extends PyObject {
    private final String value;

    public PyStr(long baseAddress, IMemoryReader memoryReader) {
        super(baseAddress, memoryReader);

        value = IMemoryReader.readStringAsciiNullTerminated(memoryReader, baseAddress + 20, 0x1000);
    }

    public String getValue() {
        return value;
    }
}
