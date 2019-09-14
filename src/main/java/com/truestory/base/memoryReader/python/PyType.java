package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PyType extends PyObject{
    public static final int SPECIALISATION_RUNTIME_COST_BLOCK_SIZE = 0x1000; // 4096
    private static final int OFFSET_TP_NAME = 12;

    private static final String TAG_TP_NAME = "type";

    private long tpNameAddress;
    private String tpNameVal;

    public PyType(long baseAddress, IMemoryReader memoryReader) {
        super(baseAddress, memoryReader);

        tpNameAddress = IMemoryReader.readUInt32(memoryReader, baseAddress + OFFSET_TP_NAME).orElse(-1);

        if (tpNameAddress < Long.MAX_VALUE) {
            tpNameVal = IMemoryReader.readStringAsciiNullTerminated(memoryReader, tpNameAddress, 0x100);
        }
    }

    /**
     * the enumerated set contains all addresses of Python Objects of the type with the given @tpNameAddress.
     * the addresses are only filtered for appropriate ob_type.
     */
    static public List<Integer> enumeratePossibleAddressesOfInstancesOfPythonTypeFilteredByObType(IMemoryReader memoryReader, String tp_name) {
        List<Integer> candidatesTypeObjectType = PyType.findCandidatesTypeObjectTypeAddress(memoryReader);

        List<PyType> typeObjectTypeList = PyType.typeObjectAddressesFilterByTpName(candidatesTypeObjectType, memoryReader, TAG_TP_NAME);

        if (null == typeObjectTypeList) {
            return Collections.emptyList();
        }
        List<Integer> candidateForObjectOfTypeAddressPlus4 = new ArrayList<>();

        for (PyType typeObjectType : typeObjectTypeList) {
            //	finds candidate Addresses for Objects of type "type" with only requiring them to have a appropriate value for ob_type.
            List<Integer> typeCandidateAddressesPlus4 = IMemoryReader.addressesHoldingValue32Aligned32(
                                                memoryReader, typeObjectType.getBaseAddress(), 0, Integer.MAX_VALUE);

            List<Integer> typeCandidateAddresses = typeCandidateAddressesPlus4.stream().map(address -> address - 4).collect(Collectors.toList());

            List<PyType> typeObjectsWithProperName = PyType.typeObjectAddressesFilterByTpName(typeCandidateAddresses, memoryReader, tp_name);

            for (PyType typeObject : typeObjectsWithProperName) {
                //	finds candidate Addresses for Objects of type tpNameAddress with only requiring them to have a appropriate value for ob_type.
                candidateForObjectOfTypeAddressPlus4.addAll(IMemoryReader.addressesHoldingValue32Aligned32(memoryReader, typeObject.getBaseAddress(), 0, Integer.MAX_VALUE));
            }
        }

        return candidateForObjectOfTypeAddressPlus4.stream().map(address -> address - 4).collect(Collectors.toList());
    }

    /**
     * enumerates all Addresses that satisfy this condition:
     * +interpreted as the Address of a Python Type Object, the Member tp_type points to the Object itself.
     * instead of reusing the PyObject class, this method uses a more specialized implementation to achieve lower runtime cost.
     * the method assumes the objects to be 32Bit aligned.
     */
    static public List<Integer> findCandidatesTypeObjectTypeAddress(IMemoryReader memoryReader) {
        List<Integer> candidatesAddress = new ArrayList<>();

        for (long blockAddress = 0; blockAddress < Integer.MAX_VALUE; blockAddress += SPECIALISATION_RUNTIME_COST_BLOCK_SIZE) {
            int[] blockValues32 = IMemoryReader.readArray(memoryReader, blockAddress, SPECIALISATION_RUNTIME_COST_BLOCK_SIZE);

            if (null == blockValues32) {
                continue;
            }

            for (int inBlockIndex = 0; inBlockIndex < blockValues32.length; inBlockIndex++) {
                int candidatePointerInBlockAddress = inBlockIndex * 4;

                long candidatePointerAddress = blockAddress + candidatePointerInBlockAddress;

                long candidatePointer = blockValues32[inBlockIndex];

                if (candidatePointerAddress == PyObject.OFFSET_OB_TYPE + candidatePointer) {
                    candidatesAddress.add((int)candidatePointer);
                }
            }
        }
        return candidatesAddress;
    }

    public static List<PyType> typeObjectAddressesFilterByTpName( List<Integer> typeObjectAddresses, IMemoryReader memoryReader, String tpName) {
        if (null == typeObjectAddresses || null == memoryReader || null == tpName) {
            return Collections.emptyList();
        }

        List<PyType> tpNameObjects = new ArrayList<>();

        for (Integer candidateTypeAddress : typeObjectAddresses) {
            PyType pyType = new PyType(candidateTypeAddress, memoryReader);

            if (tpName.equals(pyType.getTpNameVal())) {
                tpNameObjects.add(pyType);
            }
        }

        return tpNameObjects;
    }

    public String getTpNameVal() {
        return tpNameVal;
    }
}

/// <summary>
/// the enumerated set contains all addresses of Python Objects of the type with the given <paramref name="tpNameAddress"/>.
///
/// the addresses are only filtered for appropriate ob_type.
/// </summary>
/// <param name="MemoryReader"></param>
/// <param name="tpNameAddress"></param>
/// <returns></returns>
/*
static public IEnumerable<UInt32> EnumeratePossibleAddressesOfInstancesOfPythonTypeFilteredByObType(
        IMemoryReader MemoryReader,
        string tpNameAddress)
        {
        var CandidatesTypeObjectType = PyType.findCandidatesTypeObjectTypeAddress(MemoryReader);

        var TypeObjectType = PyType.typeObjectAddressesFilterByTpName(CandidatesTypeObjectType, MemoryReader, "type").FirstOrDefault();

        if (null == TypeObjectType)
        {
        yield break;
        }

        //	finds candidate Addresses for Objects of type "type" with only requiring them to have a appropriate value for ob_type.
        var TypeCandidateAddressesPlus4 =
        MemoryReader.addressesHoldingValue32Aligned32((UInt32)TypeObjectType.BaseAddress, 0, Int32.MaxValue)
        .ToArray();

        var TypeCandidateAddresses =
        TypeCandidateAddressesPlus4
        .Select((Address) => (UInt32)(Address - 4))
        .ToArray();

        var TypeObjectsWithProperName =
        PyType.typeObjectAddressesFilterByTpName(TypeCandidateAddresses, MemoryReader, tpNameAddress)
        .ToArray();

        foreach (var TypeObject in TypeObjectsWithProperName)
        {
        //	finds candidate Addresses for Objects of type tpNameAddress with only requiring them to have a appropriate value for ob_type.

        foreach (var CandidateForObjectOfTypeAddressPlus4 in MemoryReader.addressesHoldingValue32Aligned32((UInt32)TypeObject.BaseAddress, 0, Int32.MaxValue))
        {
        yield return (UInt32)(CandidateForObjectOfTypeAddressPlus4 - 4);
        }
        }
        }

/// <summary>
/// enumerates the subset of Addresses that satisfy this condition:
/// +interpreted as the Address of a Python Type Object, the tpNameAddress of this Object Equals <paramref name="tpNameAddress"/>
/// </summary>
/// <param name="TypeObjectAddresses"></param>
/// <param name="MemoryReader"></param>
/// <param name="tpNameAddress"></param>
/// <returns></returns>
static public IEnumerable<PyType> typeObjectAddressesFilterByTpName(
        IEnumerable<UInt32> TypeObjectAddresses,
        IMemoryReader MemoryReader,
        string tpNameAddress)
        {
        if (null == TypeObjectAddresses || null == MemoryReader)
        {
        yield break;
        }

        foreach (var CandidateTypeAddress in TypeObjectAddresses)
        {
        var PyType = new PyType(CandidateTypeAddress, MemoryReader);

        if (!string.Equals(PyType.tpNameVal, tpNameAddress))
        {
        continue;
        }

        yield return PyType;
        }
        }

/// <summary>
/// enumerates all Addresses that satisfy this condition:
/// +interpreted as the Address of a Python Type Object, the Member tp_type points to the Object itself.
///
/// instead of reusing the PyObject class, this method uses a more specialized implementation to achieve lower runtime cost.
///
/// the method assumes the objects to be 32Bit aligned.
/// </summary>
/// <param name="MemoryReader"></param>
/// <returns></returns>
static public IEnumerable<UInt32> findCandidatesTypeObjectTypeAddress(
        IMemoryReader MemoryReader)
        {
        var CandidatesAddress = new List<UInt32>();

        for (Int64 BlockAddress = 0; BlockAddress < int.MaxValue; BlockAddress += Specialisation_RuntimeCost_BlockSize)
        {
        var BlockValues32 = MemoryReader.ReadArray<UInt32>(BlockAddress, Specialisation_RuntimeCost_BlockSize);

        if (null == BlockValues32)
        {
        continue;
        }

        for (int InBlockIndex = 0; InBlockIndex < BlockValues32.Length; InBlockIndex++)
        {
        var CandidatePointerInBlockAddress = InBlockIndex * 4;

        var CandidatePointerAddress = BlockAddress + CandidatePointerInBlockAddress;

        var CandidatePointer = BlockValues32[InBlockIndex];

        if (CandidatePointerAddress == Offset_ob_type + CandidatePointer)
        {
        yield return CandidatePointer;
        }
        }
        }
        }
        }*/
