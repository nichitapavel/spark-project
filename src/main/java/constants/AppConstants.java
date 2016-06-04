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
    public static final String ATTRIBUTES_LIST_DELETE = "attributes-list-delete";

    public static final String FD = "fd";
    public static final String FDS_LIST = "fds-list";
    public static final String FDS_LIST_CHECKBOX = "fds-list-checkbox";
    public static final String FDS_LIST_RADIO = "fds-list-radio";
    public static final String FDS_LIST_DELETE = "fds-list-delete";

    public static final String FDJOINT = "fdjoint";
    public static final String FDJOINTS_LIST = "fdjoints-list";
    public static final String FDJOINTS_LIST_RADIO_A = "fdjoints-list-radio-a";
    public static final String FDJOINTS_LIST_RADIO_B = "fdjoints-list-radio-b";
    public static final String FDJOINTS_LIST_RADIO = "fdjoints-list-radio";
    public static final String FDJOINTS_LIST_DELETE = "fdjoints-list-delete";
    public static final String FDJOINTS_LIST_RADIO_NONE = "fdjoints-list-radio-none";

    public static final String RELATION = "relation";
    public static final String RELATIONS_LIST = "relations-list";
    public static final String RELATIONS_LIST_NF = "relations-list-nf";
    public static final String RELATIONS_LIST_RADIO = "relations-list-radio";
    public static final String RELATIONS_LIST_DELETE = "relations-list-delete";

    public static final String ULLMAN_RESULT = "ullman-result";

    public static final String CALCULATE_KEYS_RESULT = "calculate-keys-result";

    public static final String CALCULATE_MINIMAL_COVER_RESULT = "calculate-minimal-cover-result";

    public static final String PROJECTION_RESULT = "projection-result";

    public static final String FD_PARTOF_FDJOINT_RESULT = "fd-partof-fdjoint-result";

    public static final String FDJOINT_IMPLIES_RESULT = "fdjoint-implies-fdjoint-result";

    public static final String FDJOINT_EQUIVALENCE_RESULT = "fdjoint-equivalence-fdjoint-result";

    public static final String TESTS_KEYS_RESULT = "test-keys-result";
    
    public static final String TESTS_MINIMAL_COVER_RESULT = "test-minimal-cover-result";
    
    public static final String TESTS_NORMAL_FORM_RESULT = "test-normal-form-result";
    
    public static final String NORMALIZE_RESULT = "normalize-result";

    private AppConstants() {
        // Private constructor to prevent instantiation.
    }
}
