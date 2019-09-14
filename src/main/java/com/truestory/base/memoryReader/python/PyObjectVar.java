package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;

public class PyObjectVar extends PyObject {
    private final int OFFSET_OB_SIZE = 8;

    private final int obSize;

    public PyObjectVar(long baseAddress, IPythonMemoryReader memoryReader) {
        super(baseAddress, memoryReader);
        obSize = IMemoryReader.readUInt32(memoryReader, baseAddress + OFFSET_OB_SIZE).orElse(0);
    }

    public int getObSize() {
        return obSize;
    }
}