/**
 * 
 */
package manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import datastructures.Attribute;
import datastructures.FDSet;
import datastructures.Relation;
import dependency.FunctionalDependency;

/**
 * @author Pavel Nichita
 *
 */
public class DeleteManager {
    private DeleteManager() {
        // Private constructor to avoid initialization
    }
    
    public static Map<String, FunctionalDependency> deleteAttributeFromFD(String attr, Map<String, FunctionalDependency> fdList) {
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

        return updateFDMap(fdList);
    }

    public static Map<String, Relation> deleteFDSetFromRelation(FDSet fdSet, Map<String, Relation> relationList) {
        Iterator<Map.Entry<String, Relation>> it = relationList.entrySet().iterator();
 
        while (it.hasNext()) {
            Entry<String, Relation> item = it.next();
            Relation relation = item.getValue();

            if (relation.getDFJoint().equals(fdSet)){
                relation.setDFJoint(new FDSet());
            }
        }
        
        return relationList;
    }
    
    
    private static Map<String, FunctionalDependency> updateFDMap(Map<String, FunctionalDependency> fdList) {
        Map<String, FunctionalDependency> newFDList = new HashMap<>();
        fdList.forEach((k, v) -> {
            newFDList.put(v.toString(), v);
        });
        
        return newFDList;
    }

}
