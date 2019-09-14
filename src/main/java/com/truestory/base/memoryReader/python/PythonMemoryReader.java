package com.truestory.base.memoryReader.python;

import com.sun.jna.Memory;
import com.truestory.base.memoryReader.ProcessMemoryReader;

import java.util.HashMap;
import java.util.Map;

public class PythonMemoryReader implements IPythonMemoryReader{
    private ProcessMemoryReader memoryReader;

    private Map<Long, PyType> cacheTypeObjects = new HashMap<>();

    public PythonMemoryReader(ProcessMemoryReader memoryReader) {
        this.memoryReader = memoryReader;
    }


    @Override
    public PyType typeFromAddress(long typeObjectAddress) {
        PyType typeObject = cacheTypeObjects.get(typeObjectAddress);

        if(null != typeObject) {
            return typeObject;
        }

        typeObject = new PyType(typeObjectAddress, memoryReader);

        cacheTypeObjects.put(typeObjectAddress, typeObject);

        return typeObject;
    }

    @Override
    public Memory readBytes(long address, int bytesCount) {
        return memoryReader.readBytes(address, bytesCount);
    }
}
