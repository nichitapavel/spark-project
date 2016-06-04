/**
 * 
 */
package manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import datastructures.Attribute;
import datastructures.AttributeSet;
import datastructures.FDSet;
import datastructures.Relation;
import dependency.ADependency;
import dependency.FunctionalDependency;

/**
 * @author Pavel Nichita
 *
 */
public class DeleteManager {
    private DeleteManager() {
        // Private constructor to avoid initialization
    }
    
    public static  Map<String, Relation> updateRelationMap(Map<String, Relation> relationList, 
            Map<String, FDSet> fdSetList, 
            Map<String, Attribute> attrList) {
        
        relationList.forEach((k, v)-> {
            String relFDSetName = v.getDFJoint().getName();
            FDSet fdSet = fdSetList.get(relFDSetName);
            
            if (fdSet != null) {
                v.setDFJoint(fdSet);
            } else {
                v.setDFJoint(new FDSet());
            }
        });
        
        relationList.forEach((k, v)-> {
            AttributeSet attrSet = v.getAttrJoint();
            Iterator<Attribute> it = attrSet.iterator();
            
            while (it.hasNext()) {
                Attribute item = it.next();
                Attribute attr = attrList.get(item.toString());
                
                if (attr == null) {
                    it.remove();
                }
            }
        });
        
        Iterator<Entry<String, Relation>> it = relationList.entrySet().iterator();
        
        while(it.hasNext()) {
            Entry<String, Relation> item = it.next();
            Relation relation = item.getValue();
            
            if(relation.getAttrJoint().getSize() == 0) {
                it.remove();
            }
        }
        
        return relationList;
    }
    
    public static Map<String, FDSet> updateFDSetMap(Map<String, FunctionalDependency> fdList, 
            Map<String, FDSet> fdSetList) {
        
        Iterator<Entry<String, FDSet>> it = fdSetList.entrySet().iterator();
        
        while (it.hasNext()) {
            Entry<String, FDSet> item = it.next();
            FDSet fdSet = item.getValue();
            Iterator<ADependency> it2 = fdSet.iterator();

            while (it2.hasNext()) {
                ADependency item2 = it2.next();
                FunctionalDependency fd = fdList.get(item2.toString());

                if (fd == null) {
                    it2.remove();
                }
            }
            
            if (fdSet.getSize() == 0) {
                it.remove();
            }
        }
        
        return fdSetList;
    }
    
    public static Map<String, FunctionalDependency> updateFDMap(Attribute attr, 
            Map<String, FunctionalDependency> fdList) {
        
        Iterator<Map.Entry<String, FunctionalDependency>> it = fdList.entrySet().iterator();
        
        while (it.hasNext()) {
            Entry<String, FunctionalDependency> item = it.next();
            FunctionalDependency fd = item.getValue();
            fd.removeAttributeFromAntecedent(new Attribute(attr));
            fd.removeAttributeFromConsequent(new Attribute(attr));
            
            if (fd.getAntecedent().getSize() == 0 ||
                    fd.getConsequent().getSize() == 0) {
                it.remove();
            } 
        }

        return updateFDMapKey(fdList);
    }
    
    private static Map<String, FunctionalDependency> updateFDMapKey(Map<String, FunctionalDependency> fdList) {
        Map<String, FunctionalDependency> newFDList = new HashMap<>();
        
        fdList.forEach((k, v) -> {
            newFDList.put(v.toString(), v);
        });
        
        return newFDList;
    }

}
