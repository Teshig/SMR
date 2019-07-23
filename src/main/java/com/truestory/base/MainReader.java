package com.truestory.base;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;


public class MainReader {

    // This is the standard, stable way of mapping, which supports extensive
    // customization and mapping of Java to native types.

    public interface CLibrary extends Library {
        CLibrary INSTANCE = Native.load((Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);

        void printf(String format, Object... args);
    }

    // kernel32.dll uses the __stdcall calling convention (check the function
    // declaration for "WINAPI" or "PASCAL"), so extend StdCallLibrary
    // Most C libraries will just extend com.sun.jna.Library,
    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        // Optional: wraps every call to the native library in a
        // synchronized block, limiting native calls to one at a time
        Kernel32 SYNC_INSTANCE = (Kernel32)
                Native.synchronizedLibrary(INSTANCE);

        @Structure.FieldOrder({ "wYear", "wMonth", "wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond", "wMilliseconds" })
        public static class SYSTEMTIME extends Structure {
            public short wYear;
            public short wMonth;
            public short wDayOfWeek;
            public short wDay;
            public short wHour;
            public short wMinute;
            public short wSecond;
            public short wMilliseconds;
        }

        void GetSystemTime(SYSTEMTIME result);
    }

    public static void main(String[] args) {
//    CLibrary.INSTANCE.printf("Hello, World\n");
//    for (int i=0;i < args.length;i++) {
//      CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
//    }
        Kernel32.SYSTEMTIME time = new Kernel32.SYSTEMTIME();

        Kernel32.INSTANCE.GetSystemTime(time);
        System.out.println(time.wYear);
    }
}
