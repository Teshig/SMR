package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;

public interface IPythonMemoryReader extends IMemoryReader {
    PyType typeFromAddress(long typeObjectAddress);
}
