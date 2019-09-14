package com.truestory.base.memoryReader.python;

import com.truestory.base.memoryReader.IMemoryReader;
import com.truestory.base.memoryReader.UITreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PyChildrenList extends PyObjectWithRefToDictAt8 {
    private static final String CHILDREN_OBJECT_NAME = "_childrenObjects";
    private PyDictEntry dictEntryChildren;
    private PyList childrenList;

    private List<UITreeNode> children;

    public PyChildrenList(long baseAddress, IMemoryReader memoryReader) {
        super(baseAddress, memoryReader);
    }

    public List<UITreeNode> loadChildren(IPythonMemoryReader memoryReader) {
        PyDict dict = super.getDict();

        if (null != dict) {
            dictEntryChildren = dict.entryForKeyStr(CHILDREN_OBJECT_NAME);
        }

        if (null != dictEntryChildren) {
            childrenList = new PyList(dictEntryChildren.getMeValue(), memoryReader);
        }

        if (null != childrenList) {
            int[] items = childrenList.getItems();

            if (null != items) {
                children = Arrays.stream(items).mapToObj(childAddress -> new UITreeNode(childAddress, memoryReader)).collect(Collectors.toList());
            }
        }

        return children;
    }

    public List<UITreeNode> getChildren() {
        return null != children ? new ArrayList<>(children) : new ArrayList<>();
    }
}
