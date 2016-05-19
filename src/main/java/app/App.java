/**
 * 
 */
package app;

import java.util.HashMap;
import java.util.Map;

import datastructures.Attribute;
import datastructures.AttributeJoint;
import datastructures.DFJoint;
import datastructures.Relation;
import dependency.FunctionalDependency;
import spark.ModelAndView;
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
    private static Map<String, String> sessions = new HashMap<String, String>();

    public static void main(String[] args) {
        staticFileLocation("/public");
        
        String helloVtl = "templates/hello.vtl";

        get("/", (req, res) -> {
            System.out.println(req.ip());
            Map<String, Object> model = new HashMap<>();
            
            model.put(AppConstants.TEMPLATE, helloVtl);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/session", (req, res) -> {
            req.session(true);
            req.session().attribute(SessionConstants.ATTRIBUTE_LIST, new HashMap<String, Attribute>());
            req.session().attribute(SessionConstants.FD_LIST, new HashMap<String, FunctionalDependency>());
            req.session().attribute(SessionConstants.FDJOINT_LIST, new HashMap<String, DFJoint>());
            req.session().attribute(SessionConstants.RELATION_LIST, new HashMap<String, Relation>());
            sessions.put(req.session().id(), req.queryParams(SessionConstants.USERNAME));
            
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/session", (req, res) -> {
            req.session().id();
            req.session().invalidate();
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/attribute", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Object> model = new HashMap<>();
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ATTRIBUTE);
            model.put(AppConstants.ATTRIBUTES_LIST, TemplateConstants.ATTRIBUTES_LIST);
            model.put(AppConstants.ATTRIBUTES_ADD_FORM, TemplateConstants.ATTRIBUTES_ADD_FORM);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/attribute", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Attribute attr = new Attribute(req.queryParams(AppConstants.ATTRIBUTE));
            attrList.put(attr.getAttribute(), attr);

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ATTRIBUTE);
            model.put(AppConstants.ATTRIBUTES_LIST, TemplateConstants.ATTRIBUTES_LIST);
            model.put(AppConstants.ATTRIBUTES_ADD_FORM, TemplateConstants.ATTRIBUTES_ADD_FORM);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/fd", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
            
            Map<String, Object> model = new HashMap<>();
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
            checkSession(req.session().id());
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

            Map<String, Object> model = new HashMap<>();
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

        get("/fdjoint", (req, res) -> {
            checkSession(req.session().id());
            Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT);
            model.put(AppConstants.FDJOINTS_LIST, TemplateConstants.FDJOINTS_LIST);
            model.put(AppConstants.FDS_LIST_CHECKBOX, TemplateConstants.FDS_LIST_CHECKBOX);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/fdjoint", (req, res) -> {
            checkSession(req.session().id());
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
            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT);
            model.put(AppConstants.FDJOINTS_LIST, TemplateConstants.FDJOINTS_LIST);
            model.put(AppConstants.FDS_LIST_CHECKBOX, TemplateConstants.FDS_LIST_CHECKBOX);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/relation", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.RELATION);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/relation", (req, res) -> {
            checkSession(req.session().id());
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
            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.RELATION);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/ullman", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ULLMAN);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/ullman", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
            AttributeJoint attrJoint = new AttributeJoint();
            Relation relation = new Relation();
            for (String item : req.queryParams()){
                if (item.contains(FormConstants.ATTRIBUTE)){
                    attrJoint.addAttributes(attrList.get(req.queryParams(item)));
                }
                else {
                    relation = relationList.get(req.queryParams(item));
                }
            }
            attrJoint = normalization.Normalization.simpleUllman(attrJoint, relation.getDFJoint());

            Map<String, Object> model = new HashMap<>();
            model.put(DataConstants.ATTRIBUTEJOINT, attrJoint);
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ULLMAN);
            model.put(AppConstants.ULLMAN_RESULT, TemplateConstants.ULLMAN_RESULT);
            model.put(SessionConstants.USERNAME, req.session().attribute(SessionConstants.USERNAME));
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/find-normal-form", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
    }

    private static void checkSession(String str) {
        if (!sessions.containsKey(str)){
            halt(401, "No known session");
        }
    }
}
