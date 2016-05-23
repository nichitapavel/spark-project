/**
 * 
 */
package constants;

/**
 * @author Pavel Nichita
 * Variable name of every variable inside a template.
 * Most of them are the names of the templates variable.
 * The other variables are what is used for form differentation.  
 */
public class AppConstants {
    public static final String TEMPLATE = "template";

    public static final String ATTRIBUTE = "attribute";
    public static final String ATTRIBUTES_ADD_FORM = "attributes-add-form";
    public static final String ATTRIBUTES_LIST_CHECKBOX = "attributes-list-checkbox";
    public static final String ATTRIBUTES_LIST_CHECKBOX_ANT = "attributes-list-checkbox-ant";
    public static final String ATTRIBUTES_LIST_CHECKBOX_CON = "attributes-list-checkbox-con";
    public static final String ATTRIBUTES_LIST = "attributes-list";
    
    public static final String FD = "fd";
    public static final String FDS_LIST = "fds-list";
    public static final String FDS_LIST_CHECKBOX = "fds-list-checkbox";
    
    public static final String FDJOINT = "fdjoint";
    public static final String FDJOINTS_LIST = "fdjoints-list";
    public static final String FDJOINTS_LIST_RADIO = "fdjoints-list-radio";
    
    public static final String RELATION = "relation";
    public static final String RELATIONS_LIST = "relations-list";
    public static final String RELATIONS_LIST_RADIO = "relations-list-radio";
    
    public static final String ULLMAN_RESULT = "ullman-result";

    private AppConstants() {
        // Private constructor to prevent instantiation.
    }
}
