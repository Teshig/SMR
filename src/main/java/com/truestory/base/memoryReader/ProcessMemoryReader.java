package com.truestory.base.memoryReader;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

/**
 * Read the process by @processId
 */
public class ProcessMemoryReader implements IMemoryReader, AutoCloseable {
    private final static long UInt32_MIN_VALUE = 0L;
    private final static long UInt32_MAX_VALUE = 4_294_967_295L;

    private final int processId;

    private final WinNT.HANDLE process;


    public ProcessMemoryReader(int processId) {
        this.processId = processId;
        this.process = openProcess(processId);
    }

    private WinNT.HANDLE openProcess(int pid) {
        WinNT.HANDLE handle = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_VM_READ, false, pid);
        return handle;
    }

    public WinNT.HANDLE getProcess() {
        return process;
    }

    static public Pointer castToIntPtrAvoidOverflow(long address) {

        if (address < UInt32_MIN_VALUE || UInt32_MAX_VALUE < address) {
            return null;
        }

        return new Pointer(address);
    }

    @Override
    public Memory readBytes(long address, int bytesCount) {
        if (bytesCount < 1) {
            return null;
        }

        Memory buffer = new Memory(bytesCount);

        IntByReference lpNumberOfBytesRead = new IntByReference();

        Pointer addressAsIntPtr = castToIntPtrAvoidOverflow(address);

        if (addressAsIntPtr == null) {
            return null;
        }

        boolean isReaded = Kernel32.INSTANCE.ReadProcessMemory(process, addressAsIntPtr, buffer, bytesCount, lpNumberOfBytesRead);

        int numberOfBytesRead = lpNumberOfBytesRead.getValue();

        if (numberOfBytesRead < 1) {
            return null;
        }

        if (buffer.size() == numberOfBytesRead) {
            return buffer;
        }

        return buffer;
    }

    @Override
    public void close() throws Exception {
        Kernel32.INSTANCE.CloseHandle(process);
    }
}
