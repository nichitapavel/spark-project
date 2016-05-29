/**
 * 
 */
package constants;

/**
 * @author Pavel Nichita
 * Routes of the templates, where to find them.
 */
public class TemplateConstants {
    public static final String LAYOUT = "templates/other/layout.vtl";
    public static final String E401 = "templates/other/401.vtl";
    public static final String HOME = "templates/other/home.vtl";

    public static final String ATTRIBUTE = "templates/attribute/attribute.vtl";
    public static final String ATTRIBUTES_ADD_FORM = "templates/attribute/attributes-add-form.vtl";
    public static final String ATTRIBUTES_LIST_CHECKBOX_ANT = "templates/attribute/attributes-list-checkbox-ant.vtl";
    public static final String ATTRIBUTES_LIST_CHECKBOX_CON = "templates/attribute/attributes-list-checkbox-con.vtl";
    public static final String ATTRIBUTES_LIST_CHECKBOX = "templates/attribute/attributes-list-checkbox.vtl";
    public static final String ATTRIBUTES_LIST = "templates/attribute/attributes-list.vtl";
    public static final String ATTRIBUTES_LIST_DELETE = "templates/attribute/attributes-list-delete.vtl";

    public static final String FD = "templates/fd/fd.vtl";
    public static final String FDS_LIST = "templates/fd/fds-list.vtl";
    public static final String FDS_LIST_CHECKBOX = "templates/fd/fds-list-checkbox.vtl";
    public static final String FDS_LIST_RADIO = "templates/fd/fds-list-radio.vtl";
    public static final String FDS_LIST_DELETE = "templates/fd/fds-list-delete.vtl";

    public static final String FDJOINT = "templates/fdjoint/fdjoint.vtl";
    public static final String FDJOINTS_LIST = "templates/fdjoint/fdjoints-list.vtl";
    public static final String FDJOINTS_LIST_RADIO = "templates/fdjoint/fdjoints-list-radio.vtl";
    public static final String FDJOINTS_LIST_RADIO_A = "templates/fdjoint/fdjoints-list-radio-a.vtl";
    public static final String FDJOINTS_LIST_RADIO_B = "templates/fdjoint/fdjoints-list-radio-b.vtl";
    public static final String FDJOINTS_LIST_DELETE = "templates/fdjoint/fdjoints-list-delete.vtl";

    public static final String RELATION = "templates/relation/relation.vtl";
    public static final String RELATIONS_LIST = "templates/relation/relations-list.vtl";
    public static final String RELATIONS_LIST_NF = "templates/relation/relations-list-nf.vtl";
    public static final String RELATIONS_LIST_RADIO = "templates/relation/relations-list-radio.vtl";
    public static final String RELATIONS_LIST_DELETE = "templates/relation/relations-list-delete.vtl";

    public static final String ULLMAN = "templates/analyze/ullman.vtl";
    public static final String ULLMAN_RESULT = "templates/analyze/ullman-result.vtl";

    public static final String CALCULATE_KEYS = "templates/analyze/calculate-keys.vtl";
    public static final String CALCULATE_KEYS_RESULT = "templates/analyze/calculate-keys-result.vtl";

    public static final String CALCULATE_MINIMAL_COVER = "templates/analyze/calculate-minimal-cover.vtl";
    public static final String CALCULATE_MINIMAL_COVER_RESULT = "templates/analyze/calculate-minimal-cover-result.vtl";

    public static final String PROJECTION = "templates/analyze/projection.vtl";
    public static final String PROJECTION_RESULT = "templates/analyze/projection-result.vtl";

    public static final String FD_PARTOF_FDJOINT = "templates/analyze/fd-partof-fdjoint.vtl";
    public static final String FD_PARTOF_FDJOINT_RESULT = "templates/analyze/fd-partof-fdjoint-result.vtl";

    public static final String FDJOINT_IMPLIES = "templates/analyze/fdjoint-implies-fdjoint.vtl";
    public static final String FDJOINT_IMPLIES_RESULT = "templates/analyze/fdjoint-implies-fdjoint-result.vtl";

    public static final String FDJOINT_EQUIVALENCE = "templates/analyze/fdjoint-equivalence-fdjoint.vtl";
    public static final String FDJOINT_EQUIVALENCE_RESULT = "templates/analyze/fdjoint-equivalence-fdjoint-result.vtl";

    public static final String TESTS_KEYS = "templates/tests/test-keys.vtl";
    public static final String TESTS_KEYS_RESULT = "templates/tests/test-keys-result.vtl";

    public static final String TESTS_MINIMAL_COVER = "templates/tests/test-minimal-cover.vtl";
    public static final String TESTS_MINIMAL_COVER_RESULT = "templates/tests/test-minimal-cover-result.vtl";
    
    public static final String TESTS_NORMAL_FORM = "templates/tests/test-normal-form.vtl";
    public static final String TESTS_NORMAL_FORM_RESULT = "templates/tests/test-normal-form-result.vtl";
    
    public static final String NORMALIZE = "templates/tests/normalize.vtl";
    public static final String NORMALIZE_RESULT = "templates/tests/normalize-result.vtl";

    private TemplateConstants() {
        // Private constructor to prevent instantiation.
    }
}
