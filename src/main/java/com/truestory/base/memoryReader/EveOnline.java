package com.truestory.base.memoryReader;

import com.truestory.base.memoryReader.python.IPythonMemoryReader;
import com.truestory.base.memoryReader.python.PyType;

import java.util.*;

public class EveOnline {
    private static final String TAG_TP_NAME = "UIRoot";

    static public UITreeNode buildUIRoot(IPythonMemoryReader memoryReader) {
        List<Integer> candidateAddresses = PyType.enumeratePossibleAddressesOfInstancesOfPythonTypeFilteredByObType(memoryReader, TAG_TP_NAME);

        List<Map.Entry<UITreeNode, Integer>> candidatesWithChildrenCount = new ArrayList<>();

        for (Integer candidateAddress : candidateAddresses) {

            UITreeNode candidate = new UITreeNode(candidateAddress, memoryReader);

            int candidateChildrenCount = 0;

            {
                List<UITreeNode> candidateChildren = candidate.enumerateChildrenTransitive(memoryReader, 10000);

                if (null != candidateChildren) {
                    candidateChildrenCount = candidateChildren.size();
                }
            }

            candidatesWithChildrenCount.add(new AbstractMap.SimpleEntry<>(candidate, candidateChildrenCount));
        }

        System.out.println(candidateAddresses.size());
        System.out.println(candidateAddresses);

        //	return the candidate the most children nodes where found in.
        return candidatesWithChildrenCount.stream().max(Comparator.comparing(Map.Entry::getValue)).orElseThrow(() -> new RuntimeException("all is bad")).getKey();
    }
}
