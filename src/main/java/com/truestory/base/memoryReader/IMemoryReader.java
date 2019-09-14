package com.truestory.base.memoryReader;

import com.sun.jna.Memory;
import com.truestory.base.memoryReader.python.PyType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.IntStream;

public interface IMemoryReader {

    public static final int ELEMENT_SIZE = 4;
    public static final int NUMBER_OF_BYTES_INTEGER = 4;

//    byte[] readBytes(long address, int bytesCount);
    Memory readBytes(long address, int bytesCount);

//    MemoryReaderModuleInfo[] Modules();

    public static List<Integer> addressesHoldingValue32Aligned32(IMemoryReader memoryReader, long searchedValue, long searchedRegionBegin, long searchedRegionEnd) {
        if (null == memoryReader) {
            return Collections.emptyList();
        }

        List<Integer> addresses = new ArrayList<>();

        // hz for what purposes this needed
        long firstBlockAddress = (searchedRegionBegin / PyType.SPECIALISATION_RUNTIME_COST_BLOCK_SIZE) * PyType.SPECIALISATION_RUNTIME_COST_BLOCK_SIZE;

        long lastBlockAddress = (searchedRegionEnd / PyType.SPECIALISATION_RUNTIME_COST_BLOCK_SIZE) * PyType.SPECIALISATION_RUNTIME_COST_BLOCK_SIZE;

        for (long blockAddress = firstBlockAddress; blockAddress <= lastBlockAddress; blockAddress += PyType.SPECIALISATION_RUNTIME_COST_BLOCK_SIZE) {
            int[] blockValues = IMemoryReader.readArray(memoryReader, blockAddress, PyType.SPECIALISATION_RUNTIME_COST_BLOCK_SIZE);

            if (null == blockValues) {
                continue;
            }

            for (int inBlockIndex = 0; inBlockIndex < blockValues.length; inBlockIndex++) {
                long address = blockAddress + (inBlockIndex * 4);

                if (address < searchedRegionBegin || searchedRegionEnd < address) {
                    continue;
                }

                if (searchedValue == blockValues[inBlockIndex]) {
                    addresses.add((int)address);
                }
            }
        }

        return addresses;
    }

    public static String readStringAsciiNullTerminated( IMemoryReader memoryReader, long address, int lengthMax) {
        if (null == memoryReader) {
            return null;
        }

        Memory bytes = memoryReader.readBytes(address, lengthMax);

        if (null == bytes) {
            return null;
        }

//        var BytesNullTerminated = bytes.TakeWhile((Byte) => 0 != Byte).ToArray();

        return bytes.getString(0, "ASCII");
    }

    public static OptionalInt readUInt32(IMemoryReader memoryReader, long address) {

        if (null == memoryReader) {
            return OptionalInt.empty();
        }

        Memory memory = memoryReader.readBytes(address, NUMBER_OF_BYTES_INTEGER);


        if (null == memory || memory.size() < NUMBER_OF_BYTES_INTEGER) {
            return OptionalInt.empty();
        }

        return OptionalInt.of(memory.getInt(0));
    }

    public static int[] readArray(IMemoryReader memoryReader, long address, int numberOfBytes) {
        if (null == memoryReader) {
            return null;
        }

        Memory bytesRead = memoryReader.readBytes(address, numberOfBytes);

        if (null == bytesRead) {
            return null;
        }

        // for x32 client = 4
        // int elementSize = System.Runtime.InteropServices.Marshal.SizeOf(typeof(T));
        int ELEMENT_SIZE = 4;

        long NumberOfElements = (bytesRead.size() - 1) / ELEMENT_SIZE + 1;

        int[] array = new int[(int)NumberOfElements];

        byte[] bytes = bytesRead.getByteArray(0, numberOfBytes);

        ByteBuffer wrapper = ByteBuffer.wrap(bytes);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);
        int i = 0;
        while (wrapper.remaining() > 0) {
            array[i] = wrapper.getInt();
            i++;
        }

        return array;
    }

    public static void invertByteArray(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            byte temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }

    public static void invertLongArray(long[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            long temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }
}
