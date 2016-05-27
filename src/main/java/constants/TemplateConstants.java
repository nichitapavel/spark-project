/**
 * 
 */
package constants;

/**
 * @author Pavel Nichita
 * Routes of the templates, where to find them.
 */
public class TemplateConstants {
    public static final String LAYOUT = "templates/layout.vtl";
    
    public static final String ATTRIBUTE = "templates/attribute/attribute.vtl";
    public static final String ATTRIBUTES_ADD_FORM = "templates/attribute/attributes-add-form.vtl";
    public static final String ATTRIBUTES_LIST_CHECKBOX_ANT = "templates/attribute/attributes-list-checkbox-ant.vtl";
    public static final String ATTRIBUTES_LIST_CHECKBOX_CON = "templates/attribute/attributes-list-checkbox-con.vtl";
    public static final String ATTRIBUTES_LIST_CHECKBOX = "templates/attribute/attributes-list-checkbox.vtl";
    public static final String ATTRIBUTES_LIST = "templates/attribute/attributes-list.vtl";
    
    public static final String FD = "templates/fd/fd.vtl";
    public static final String FDS_LIST = "templates/fd/fds-list.vtl";
    public static final String FDS_LIST_CHECKBOX = "templates/fd/fds-list-checkbox.vtl";
    
    public static final String FDJOINT = "templates/fdjoint/fdjoint.vtl";
    public static final String FDJOINTS_LIST = "templates/fdjoint/fdjoints-list.vtl";
    public static final String FDJOINTS_LIST_RADIO = "templates/fdjoint/fdjoints-list-radio.vtl";
    
    public static final String RELATION = "templates/relation/relation.vtl";
    public static final String RELATIONS_LIST = "templates/relation/relations-list.vtl";
    public static final String RELATIONS_LIST_RADIO = "templates/relation/relations-list-radio.vtl";
    
    public static final String ULLMAN = "templates/analyze/ullman.vtl";
    public static final String ULLMAN_RESULT = "templates/analyze/ullman-result.vtl";
    
    public static final String CALCULATE_KEYS = "templates/analyze/calculate-keys.vtl";
    public static final String CALCULATE_KEYS_RESULT = "templates/analyze/calculate-keys-result.vtl";

    private TemplateConstants() {
        // Private constructor to prevent instantiation.
    }
}
