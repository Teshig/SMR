package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * One more object with offsets from https://github.com/python/cpython/blob/2.7/Include/dictobject.h and https://github.com/python/cpython/blob/2.7/Objects/dictobject.c
 *
 */
public class PyDict extends PyObject{
    private static final int Offset_ma_fill = 8;
    private static final int Offset_ma_used = 12;
    private static final int Offset_ma_mask = 16;
    private static final int Offset_ma_table = 20;

    private int ma_fill;
    private int ma_used;
    private int ma_mask;
    private int ma_table;
    private int slotsCount;

    private List<PyDictEntry> slots;

    public PyDict(long baseAddress, IPythonMemoryReader memoryReader, int slotsCountMax) {
        super(baseAddress, memoryReader);
        ma_fill = IMemoryReader.readUInt32(memoryReader,baseAddress + Offset_ma_fill).orElse(0);
        ma_used = IMemoryReader.readUInt32(memoryReader,baseAddress + Offset_ma_used).orElse(0);
        ma_mask = IMemoryReader.readUInt32(memoryReader,baseAddress + Offset_ma_mask).orElse(0);
        ma_table = IMemoryReader.readUInt32(memoryReader,baseAddress + Offset_ma_table).orElse(0);

        slotsCount = ma_mask + 1;

        if (ma_table != -1 && slotsCount != -1) {
            int slotsToReadCount = Math.min(slotsCountMax, slotsCount);
            slots = IntStream.rangeClosed(0, slotsToReadCount).mapToObj(slotIndex -> new PyDictEntry(ma_table + slotIndex * 12, memoryReader)).collect(Collectors.toList());
        }
    }

    private PyDict(long baseAddress, IPythonMemoryReader memoryReader, int ma_fill, int ma_used, int ma_mask, int ma_table, int slotsCount) {
        super(baseAddress, memoryReader);
        this.ma_fill = ma_fill;
        this.ma_used = ma_used;
        this.ma_mask = ma_mask;
        this.ma_table = ma_table;
        this.slotsCount = slotsCount;
    }

    public PyDictEntry entryForKeyStr(String childrenObjectName) {
        if (null == slots || StringUtils.isEmpty(childrenObjectName)) {
            return null;
        }

        for (PyDictEntry slot : slots) {
            if (null == slot) {
                continue;
            }

            if (childrenObjectName.equals(slot.keyStr())) {
                return slot;
            }
        }

        return null;
    }
    // TODO: make object clonable
//    public PyDict clone() {
//        PyObject pyObj = super.clone();
//        return new PyDict(this.baseAddress, this.memoryReader, this.ma_fill, this.ma_used, this.ma_mask, this.ma_table, this.slotsCount);

//    }

    public List<PyDictEntry> getSlots() {
        return new ArrayList<>(slots);
    }
}
