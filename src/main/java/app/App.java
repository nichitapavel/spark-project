/**
 * 
 */
package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastructures.Attribute;
import datastructures.AttributeJoint;
import datastructures.DFJoint;
import datastructures.KeyJoint;
import datastructures.Relation;
import dependency.FunctionalDependency;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.*;

import constants.TemplateConstants;
import constants.AppConstants;
import constants.DataConstants;
import constants.SessionConstants;
import constants.FormConstants;

/**
 * @author Pavel Nichita
 *
 */
public class App {
    private static Map<String, String> session = new HashMap<String, String>();

    public static void main(String[] args) {
        staticFileLocation("/public");
        port(80);

        before((req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
        });      

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.WELCOME);
        }, new VelocityTemplateEngine());

        get("/home", (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Attribute> fdList = req.session().attribute(SessionConstants.FD_LIST);
            Map<String, Attribute> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, Attribute> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
            
            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.ATTRIBUTES_LIST_DELETE, TemplateConstants.ATTRIBUTES_LIST_DELETE);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.FDS_LIST_DELETE, TemplateConstants.FDS_LIST_DELETE);
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_DELETE, TemplateConstants.FDJOINTS_LIST_DELETE);
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_DELETE, TemplateConstants.RELATIONS_LIST_DELETE);
            model.put(AppConstants.TEMPLATE, TemplateConstants.HOME);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post("/session", (req, res) -> {
            req.session(true);
            req.session().maxInactiveInterval(43400); // 12 hours in seconds
            req.session().attribute(SessionConstants.ATTRIBUTE_LIST, new HashMap<String, Attribute>());
            req.session().attribute(SessionConstants.FD_LIST, new HashMap<String, FunctionalDependency>());
            req.session().attribute(SessionConstants.FDJOINT_LIST, new HashMap<String, DFJoint>());
            req.session().attribute(SessionConstants.RELATION_LIST, new HashMap<String, Relation>());
            session.put(req.session().id(), req.queryParams(FormConstants.USERNAME));

            res.redirect("/attribute");
            return null;
        });

        get("/session", (req, res) -> {
            req.session().invalidate();
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.GOOD_BYE);
        }, new VelocityTemplateEngine());

        get("/attribute", (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ATTRIBUTE);
            model.put(AppConstants.ATTRIBUTES_LIST, TemplateConstants.ATTRIBUTES_LIST);
            model.put(AppConstants.ATTRIBUTES_ADD_FORM, TemplateConstants.ATTRIBUTES_ADD_FORM);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/attribute", (req, res) -> {
            checkSession(req, res);

            addAttribute(req);

            res.redirect("/attribute");
            return null;
        });

        get("/fd", (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX_ANT, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX_ANT);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX_CON, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX_CON);
            model.put(DataConstants.ANTECEDENT, FormConstants.ANTECEDENT);
            model.put(DataConstants.CONSEQUENT, FormConstants.CONSEQUENT);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FD);
            model.put(AppConstants.FDS_LIST, TemplateConstants.FDS_LIST);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/fd", (req, res) -> {
            checkSession(req, res);

            addFD(req);

            res.redirect("/fd");
            return null;
        });

        get("/fdjoint", (req, res) -> {
            checkSession(req, res);
            Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT);
            model.put(AppConstants.FDJOINTS_LIST, TemplateConstants.FDJOINTS_LIST);
            model.put(AppConstants.FDS_LIST_CHECKBOX, TemplateConstants.FDS_LIST_CHECKBOX);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/fdjoint", (req, res) -> {
            checkSession(req, res);

            addFDJoint(req);

            res.redirect("/fdjoint");
            return null;
        });

        get("/relation", (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.RELATION);
            model.put(AppConstants.RELATIONS_LIST, TemplateConstants.RELATIONS_LIST);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/relation", (req, res) -> {
            checkSession(req, res);

            addRelation(req);

            res.redirect("/relation");
            return null;
        });

        get("/ullman", (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ULLMAN);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/ullman", (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            AttributeJoint ullmanAttrJoint = getAttrJoint(req);
            Relation ullmanRelation = relationList.get(req.queryParams(FormConstants.RELATION));
            AttributeJoint ullmanResult = normalization.Normalization.simpleUllman(ullmanAttrJoint, ullmanRelation.getDFJoint());

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.RELATION, ullmanRelation);
            model.put(DataConstants.ATTRJOINT, ullmanAttrJoint);
            model.put(DataConstants.ULLMAN_ALG, ullmanResult);
            model.put(AppConstants.ULLMAN_RESULT, TemplateConstants.ULLMAN_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ULLMAN);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/calculate-keys", (req, res) -> {
            checkSession(req, res);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_KEYS);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/calculate-keys", (req, res) -> {
            checkSession(req, res);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Relation relation = relationList.get(req.queryParams(FormConstants.RELATION));
            KeyJoint keyJoint = relation.calculateKeyJoint();

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.RELATION, relation);
            model.put(DataConstants.KEY_JOINT, keyJoint);
            model.put(AppConstants.CALCULATE_KEYS_RESULT, TemplateConstants.CALCULATE_KEYS_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_KEYS);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/calculate-minimal-cover", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_MINIMAL_COVER);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/calculate-minimal-cover", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            DFJoint fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
            DFJoint fdJointMinimal = new DFJoint(fdJoint);
            fdJointMinimal.removeRareAttributes(true);

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.FDJOINT, fdJointMinimal);
            model.put(AppConstants.CALCULATE_MINIMAL_COVER_RESULT, TemplateConstants.CALCULATE_MINIMAL_COVER_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_MINIMAL_COVER);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/projection", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.PROJECTION);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/projection", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);

            DFJoint fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
            AttributeJoint attrJoint = getAttrJoint(req);
            DFJoint result = fdJoint.projectionOnAttributeJoint(attrJoint);

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.FDJOINT, fdJoint);
            model.put(DataConstants.ATTRJOINT, attrJoint);
            model.put(DataConstants.RESULT, result);
            model.put(AppConstants.PROJECTION_RESULT, TemplateConstants.PROJECTION_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.PROJECTION);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/fd-partof-fdjoint", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, DFJoint> fdList = req.session().attribute(SessionConstants.FD_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.FDS_LIST_RADIO, TemplateConstants.FDS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FD_PARTOF_FDJOINT);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/fd-partof-fdjoint", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);

            FunctionalDependency fd = fdList.get(req.queryParams(FormConstants.FD));
            DFJoint fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
            Boolean result = fd.belongsTo(fdJoint, null);

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.BOOLEAN, result);
            model.put(DataConstants.FD, fd);
            model.put(DataConstants.FDJOINT, fdJoint);
            model.put(AppConstants.FD_PARTOF_FDJOINT_RESULT, TemplateConstants.FD_PARTOF_FDJOINT_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.FDS_LIST_RADIO, TemplateConstants.FDS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FD_PARTOF_FDJOINT);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/implies", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_A, TemplateConstants.FDJOINTS_LIST_RADIO_A);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_B, TemplateConstants.FDJOINTS_LIST_RADIO_B);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT_IMPLIES);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/implies", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            DFJoint fdJointA = fdJointList.get(req.queryParams(FormConstants.FDJOINT_A));
            DFJoint fdJointB = fdJointList.get(req.queryParams(FormConstants.FDJOINT_B));
            Boolean result = fdJointB.isImplied(fdJointA);

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.BOOLEAN, result);
            model.put(DataConstants.FDJOINT_A, fdJointA);
            model.put(DataConstants.FDJOINT_B, fdJointB);
            model.put(AppConstants.FDJOINT_IMPLIES_RESULT, TemplateConstants.FDJOINT_IMPLIES_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_A, TemplateConstants.FDJOINTS_LIST_RADIO_A);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_B, TemplateConstants.FDJOINTS_LIST_RADIO_B);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT_IMPLIES);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/equivalence", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_A, TemplateConstants.FDJOINTS_LIST_RADIO_A);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_B, TemplateConstants.FDJOINTS_LIST_RADIO_B);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT_EQUIVALENCE);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/equivalence", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            DFJoint fdJointA = fdJointList.get(req.queryParams(FormConstants.FDJOINT_A));
            DFJoint fdJointB = fdJointList.get(req.queryParams(FormConstants.FDJOINT_B));
            Boolean result = fdJointA.isEquivalent(fdJointB);

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.BOOLEAN, result);
            model.put(DataConstants.FDJOINT_A, fdJointA);
            model.put(DataConstants.FDJOINT_B, fdJointB);
            model.put(AppConstants.FDJOINT_EQUIVALENCE_RESULT, TemplateConstants.FDJOINT_EQUIVALENCE_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_A, TemplateConstants.FDJOINTS_LIST_RADIO_A);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_B, TemplateConstants.FDJOINTS_LIST_RADIO_B);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT_EQUIVALENCE);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        get("/test-normal-form", (req, res) -> {
            checkSession(req, res);
            Map<String, Object> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.TESTS_NORMAL_FORM);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post("/test-normal-form", (req, res) -> {
            checkSession(req, res);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Relation relation = relationList.get(req.queryParams(FormConstants.RELATION));
            String normalForm = req.queryParams(FormConstants.NORMAL_FORM);
            String resultAll = "";
            Boolean result = false;
            
            if (normalForm.equals(FormConstants.NORMAL_FORM_VALUE_2ND)){
                result = relation.is2NF();
            } else if (normalForm.equals(FormConstants.NORMAL_FORM_VALUE_3RD)) {
                result = relation.is3NF();
            } else if (normalForm.equals(FormConstants.NORMAL_FORM_VALUE_BC)) {
                result = relation.isBCNF();
            } else {
                resultAll = relation.getNormalForm();
            }
            
            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.BOOLEAN, result);
            model.put(DataConstants.OPTION, normalForm);
            model.put(DataConstants.RESULT, resultAll);
            model.put(DataConstants.RELATION, relation);
            model.put(AppConstants.TESTS_NORMAL_FORM_RESULT, TemplateConstants.TESTS_NORMAL_FORM_RESULT);
            
            
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.TESTS_NORMAL_FORM);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/test-keys", (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.TESTS_KEYS);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/test-keys", (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            AttributeJoint attrJoint = getAttrJoint(req);
            Relation relation = relationList.get(req.queryParams(FormConstants.RELATION));
            int result = attrJoint.isKey(relation);

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.ATTRJOINT, attrJoint);
            model.put(DataConstants.RELATION, relation);
            model.put(DataConstants.RESULT, result);
            model.put(AppConstants.TESTS_KEYS_RESULT, TemplateConstants.TESTS_KEYS_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.TESTS_KEYS);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/test-minimal-cover", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.TESTS_MINIMAL_COVER);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/test-minimal-cover", (req, res) -> {
            checkSession(req, res);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            DFJoint fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
            Boolean result = fdJoint.isMinimal();

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.FDJOINT, fdJoint);
            model.put(DataConstants.RESULT, result);
            model.put(AppConstants.TESTS_MINIMAL_COVER_RESULT, TemplateConstants.TESTS_MINIMAL_COVER_RESULT);

            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.TESTS_MINIMAL_COVER);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/normalize", (req, res) -> {
            checkSession(req, res);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.NORMALIZE);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post("/normalize", (req, res) -> {
            checkSession(req, res);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Relation relation = relationList.get(req.queryParams(FormConstants.RELATION));
            String normalForm = req.queryParams(FormConstants.NORMAL_FORM);
            List<Relation> result = new ArrayList<>();

            if (normalForm.equals(FormConstants.NORMAL_FORM_VALUE_3RD)) {
                result = normalization.Normalization.normalize3NF(relation, true);
            } else {
                result = normalization.Normalization.normalizeBCNF(relation, true);
            }

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.RELATION, relation);
            model.put(DataConstants.RESULT, result);
            model.put(DataConstants.OPTION, normalForm);
            model.put(AppConstants.NORMALIZE_RESULT, TemplateConstants.NORMALIZE_RESULT);
            model.put(AppConstants.RELATIONS_LIST_NF, TemplateConstants.RELATIONS_LIST_NF);
            
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.NORMALIZE);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        get("/401", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.E401);
        }, new VelocityTemplateEngine());
        
        post("/delete-attribute", (req, res) -> {
            checkSession(req, res);
            String attr = req.queryParams(FormConstants.ATTRIBUTE);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            attrList.remove(attr);
            res.redirect("/home");
            return null;
        });
        
        post("/delete-fd", (req, res) -> {
            checkSession(req, res);
            String fd = req.queryParams(FormConstants.FD);
            Map<String, Attribute> fdList = req.session().attribute(SessionConstants.FD_LIST);
            fdList.remove(fd);
            res.redirect("/home");
            return null;
        });
        
        post("/delete-fdjoint", (req, res) -> {
            checkSession(req, res);
            String fdJoint = req.queryParams(FormConstants.FDJOINT);
            Map<String, Attribute> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            fdJointList.remove(fdJoint);
            res.redirect("/home");
            return null;
        });
        
        post("/delete-relation", (req, res) -> {
            checkSession(req, res);
            String relation = req.queryParams(FormConstants.RELATION);
            Map<String, Attribute> fdJointList = req.session().attribute(SessionConstants.RELATION_LIST);
            fdJointList.remove(relation);
            res.redirect("/home");
            return null;
        });
    }

    private static void checkSession(Request req, Response res) {
        if (!session.containsKey(req.session().id())){
            res.redirect("/401");
        }
    }

    /**
     * @param req
     */
    private static void addAttribute(Request req) {
        Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
        Attribute attr = new Attribute(req.queryParams(AppConstants.ATTRIBUTE));
        attrList.put(attr.getAttribute(), attr);
    }

    /**
     * @param req
     */
    private static void addFD(Request req) {
        Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
        Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
        AttributeJoint antecedent = new AttributeJoint();
        AttributeJoint consequent = new AttributeJoint();
        for (String attrr : req.queryParams()){
            if (attrr.contains(FormConstants.ANTECEDENT)){
                antecedent.addAttributes(attrList.get(req.queryParams(attrr)));
            }
            else {
                consequent.addAttributes(attrList.get(req.queryParams(attrr)));
            }
        }
        FunctionalDependency fd = new FunctionalDependency(antecedent, consequent);
        fdList.put(fd.toString(), fd);
    }

    /**
     * @param req
     */
    private static void addFDJoint(Request req) {
        Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
        Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
        DFJoint fdJoint = new DFJoint();
        fdJoint.setName(req.queryParams(FormConstants.FDJOINT));

        for (String fd : req.queryParams()){
            if (!fd.equals(FormConstants.FDJOINT)){
                fdJoint.addDependency(fdList.get(fd));;
            }
        }
        fdJointList.put(req.queryParams(FormConstants.FDJOINT), fdJoint);
    }

    /**
     * @param req
     */
    private static void addRelation(Request req) {
        Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
        Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
        Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
        Relation relation = new Relation();
        relation.setName(req.queryParams(FormConstants.RELATION));
        AttributeJoint attrJoint = new AttributeJoint();

        for (String item : req.queryParams()){
            if (item.contains(FormConstants.FDJOINT)){
                relation.setDFJoint(fdJointList.get(req.queryParams(item)));
            }
            else if (item.contains(FormConstants.RELATION)) {}
            else {
                attrJoint.addAttributes(attrList.get(req.queryParams(item)));
            }
        }
        if (!attrJoint.isNull()) {
            relation.settAttrJoint(attrJoint);
        }

        relationList.put(req.queryParams(FormConstants.RELATION), relation);
    }

    private static AttributeJoint getAttrJoint(Request req) {
        Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
        AttributeJoint attrJoint = new AttributeJoint();

        for (String item : req.queryParams()){
            if (item.contains(FormConstants.ATTRIBUTE)){
                attrJoint.addAttributes(attrList.get(req.queryParams(item)));
            }
        }

        return attrJoint;
    }
}
