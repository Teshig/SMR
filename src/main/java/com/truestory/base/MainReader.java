package com.truestory.base;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Kernel32;
import com.truestory.base.memoryReader.EveOnline;
import com.truestory.base.memoryReader.IMemoryReader;
import com.truestory.base.memoryReader.ProcessMemoryReader;
import com.truestory.base.memoryReader.UITreeNode;
import com.truestory.base.memoryReader.python.IPythonMemoryReader;
import com.truestory.base.memoryReader.python.PythonMemoryReader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class MainReader {

    public static int PROCESS_VM_READ= 0x0010;
    public static int PROCESS_VM_WRITE = 0x0020;
    public static int PROCESS_VM_OPERATION = 0x0008;

    public static String processId = "00 00 24 4C";

    public static String EVE_PROCESS = "exefile";

    public static void main(String[] args) {
//        System.out.println("PID = " + Integer.parseInt(processId, 16));
        long start = System.currentTimeMillis();
        System.out.println("Bot is started : " + start);
        List<ProcessHandle> processHandles = ProcessHandle.allProcesses()
                .filter(MainReader::isContainsEveTrace).collect(Collectors.toList());

        //        System.out.println(processHandles.get(0).info().command());

        if (processHandles.size() < 1) {
            throw new IllegalArgumentException("No EVE process were found :(");
        }

        long pid = processHandles.get(0).pid();

        ProcessMemoryReader processReader = new ProcessMemoryReader((int)pid);

        IPythonMemoryReader pythonMemoryReader = new PythonMemoryReader(processReader);

        UITreeNode UIRoot = EveOnline.buildUIRoot(pythonMemoryReader);

        System.out.println("DONE!");

//        WinNT.HANDLE process = processReader.getProcess();
//        Pointer pointer = new Pointer(Integer.parseInt("01592840",16));
//        Pointer pointer = new Pointer(Integer.parseInt("0",16));

//        int size = 4096;
//        Memory pTemp = new Memory(size);

//        Kernel32.INSTANCE.ReadProcessMemory(process, pointer, pTemp, size, null);



//        System.out.println(pTemp);
//        System.out.println(pTemp.getInt(0));


        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Bot is finished. Total time = " + timeElapsed);
    }

    private static WinNT.HANDLE openProcess(int pid) {
        WinNT.HANDLE handle = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_VM_READ, false, pid);
        return handle;
    }

    private static boolean isContainsEveTrace(ProcessHandle process) {
        return process.info().command().orElse("").contains(EVE_PROCESS);
    }

    private static String processDetails(ProcessHandle process) {
        return String.format("%8s %8s %10s %26s %-40s %26s %10s %10s",
                Long.toHexString(process.pid()),
                text(process.parent().map(ProcessHandle::pid)),
                text(process.info().user()),
                text(process.info().startInstant()),
                text(process.info().commandLine()),
                text(process.info().command()),
                text(process.info().user()),
                text(process.info().arguments())
        );
    }

    private static String text(Optional<?> optional) {
        return optional.map(Object::toString).orElse("-");
    }

//    public static void main(String[] args) {
//        int pid = Integer.parseInt(processId, 16);
//        WinNT.HANDLE process = openProcess(pid);
////        Pointer pointer = new Pointer(Integer.parseInt("01592840",16));
//        Pointer pointer = new Pointer(Integer.parseInt("01A92840",16));
//
//        int size = 4;
//        Memory pTemp = new Memory(size);
//
//        Kernel32.INSTANCE.ReadProcessMemory(process, pointer, pTemp, size, null);
//        Kernel32.INSTANCE.
//
//
//        System.out.println(pTemp);
//        System.out.println(pTemp.getInt(0));
//    }



    // kernel32.dll uses the __stdcall calling convention (check the function
    // declaration for "WINAPI" or "PASCAL"), so extend StdCallLibrary
    // Most C libraries will just extend com.sun.jna.Library,
//    public interface Kernel32 extends StdCallLibrary {
//        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);
//
//        // Optional: wraps every call to the native library in a
//        // synchronized block, limiting native calls to one at a time
//        Kernel32 SYNC_INSTANCE = (Kernel32)
//                Native.synchronizedLibrary(INSTANCE);
//
//        @Structure.FieldOrder({ "wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds" })
//        public static class SYSTEMTIME extends Structure {
//            public short wYear;
//            public short wMonth;
//            public short wDayOfWeek;
//            public short wDay;
//            public short wHour;
//            public short wMinute;
//            public short wSecond;
//            public short wMilliseconds;
//        }
//
//        void GetSystemTime(SYSTEMTIME result);
//    }
}
