package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;

import java.util.Optional;

public class PyObjectWithRefToDictAt8 extends PyObject{
    private final static int Offset_dict = 8;

    private final int ref_dict;

    private PyDict dict;

    public PyObjectWithRefToDictAt8(long baseAddress, IMemoryReader memoryReader) {
        super(baseAddress, memoryReader);
        ref_dict = IMemoryReader.readUInt32(memoryReader,baseAddress + Offset_dict).orElse(-1);
    }

    public Optional<PyDict> loadDict(IPythonMemoryReader memoryReader) {
        if (ref_dict != -1) {
            dict = new PyDict(ref_dict, memoryReader,	0x1000);
        }

        return Optional.ofNullable(dict);
    }

    // TODO: make clonable
    public PyDict getDict() {
//        return dict.clone();
        return dict;
    }
}
