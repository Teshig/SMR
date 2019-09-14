package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;

/**
 * Offsets was taken from https://docs.python.org/2/c-api/structures.html
 */
public class PyObject {
    static final int OFFSET_OB_REFCNT = 0;
    static final int OFFSET_OB_TYPE = 4;

    private long ob_refcnt;
    private long ob_type;

    protected long baseAddress;

    protected PyType typeObject;

    private IMemoryReader memoryReader;

    public PyObject(long baseAddress, IMemoryReader memoryReader) {
        this.baseAddress = baseAddress;

        ob_refcnt = IMemoryReader.readUInt32(memoryReader,baseAddress + OFFSET_OB_REFCNT).orElse(-1);
        ob_type = IMemoryReader.readUInt32(memoryReader,baseAddress + OFFSET_OB_TYPE).orElse(-1);
    }

//    public PyType loadType(PythonMemoryReader MemoryReader) {
//        typeObject = MemoryReader.TypeFromAddress(ob_type);
//
//        return typeObject;
//    }

//    public static String TypeNameForObjectWithAddress(long objectAddress, PythonMemoryReader memoryReader) {
//        PyObject pyObject = new PyObject(objectAddress, memoryReader);
//
//        pyObject.loadType(memoryReader);
//
//        PyType objectType = pyObject.loadType(memoryReader);
//
//        if (null == objectType) {
//            return null;
//        }
//
//        return objectType.getTpNameVal();
//    }

    private PyObject(long ob_refcnt, long ob_type, long baseAddress, PyType typeObject) {
        this.ob_refcnt = ob_refcnt;
        this.ob_type = ob_type;
        this.baseAddress = baseAddress;
        this.typeObject = typeObject;
    }

    protected PyObject clone() {
        return new PyObject(this.ob_refcnt, this.ob_type, this.baseAddress, this.typeObject);
    }

    public long getBaseAddress() {
        return baseAddress;
    }
}