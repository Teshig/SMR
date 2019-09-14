package com.truestory.base.memoryReader;

import com.truestory.base.memoryReader.python.*;

import java.util.ArrayList;
import java.util.List;

public class UITreeNode extends PyObjectWithRefToDictAt8 {
    private static final String CHILDREN_STR = "children";

    private PyDictEntry dictEntryChildren;
    private PyChildrenList childrenList;

    private List<UITreeNode> children;

    public UITreeNode(long baseAddress, IPythonMemoryReader memoryReader) {
        super(baseAddress, memoryReader);
    }

    public List<UITreeNode> enumerateChildrenTransitive(IPythonMemoryReader memoryReader, int depthMax) {
        if (depthMax == 0) {
            return null;
        }

        if (depthMax < 0) {
            depthMax = 1_000_000;
        }

        // TODO: where is value is used?!?!?
        loadDict(memoryReader);
        loadChildren(memoryReader);

        if (null == children) {
            return null;
        }

        List<UITreeNode> childs = new ArrayList<>();

        for (UITreeNode child : children) {
            childs.add(child);
            // TODO: here is a problem
            List<UITreeNode> tempList = child.enumerateChildrenTransitive(memoryReader, depthMax - 1);
            if (null != tempList) {
                childs.addAll(tempList);
            }
        }

        return childs;
    }

    private List<UITreeNode> loadChildren(IPythonMemoryReader memoryReader) {
        PyDict dict = getDict();

        if (null != dict) {
            dictEntryChildren = dict.entryForKeyStr(CHILDREN_STR);
        }

        if (null != dictEntryChildren) {
            childrenList = new PyChildrenList(dictEntryChildren.getMeValue(), memoryReader);
            childrenList.loadDict(memoryReader);
            childrenList.loadChildren(memoryReader);
        }

        if (null != childrenList) {
            children = childrenList.getChildren();
        }

        return children;
    }
}
