package com.truestory.base.memoryReader;

import com.sun.jna.Memory;

public class MemoryReader implements IMemoryReader {

    @Override
    public Memory readBytes(long address, int bytesCount) {
        return new Memory(0);
    }
}
