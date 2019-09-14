package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;

/**
 *  One more object with offsets from Offsets
 *  from https://github.com/python/cpython/blob/2.7/Include/listobject.h and https://github.com/python/cpython/blob/2.7/Objects/listobject.c
 */
public class PyList extends PyObjectVar {
    private final int OFFSET_OB_ITEM = 12;

    private int ob_item;

    private int[] items;

    public PyList(long baseAddress, IPythonMemoryReader memoryReader) {
        super(baseAddress, memoryReader);
        ob_item = IMemoryReader.readUInt32(memoryReader, baseAddress + OFFSET_OB_ITEM).orElse(0);
        items = IMemoryReader.readArray(memoryReader, ob_item, super.getObSize() * 4);
    }

    public int[] getItems() {
        return items;
    }
}
