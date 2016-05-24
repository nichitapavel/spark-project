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
import spark.Session;
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
        
        String helloVtl = "templates/hello.vtl";

        before((req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
        });      
        
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put(AppConstants.TEMPLATE, helloVtl);
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
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
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/attribute", (req, res) -> {
            checkSession(req.session().id());
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
            checkSession(req.session().id());
            
            addAttribute(req.session(), req.queryParams(AppConstants.ATTRIBUTE));

            res.redirect("/attribute");
            return null;
        });

        get("/fd", (req, res) -> {
            checkSession(req.session().id());
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

            res.redirect("/fd");
            return null;
        });

        get("/fdjoint", (req, res) -> {
            checkSession(req.session().id());
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

            res.redirect("/fdjoint");
            return null;
        });

        get("/relation", (req, res) -> {
            checkSession(req.session().id());
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

            res.redirect("/relation");
            return null;
        });

        get("/ullman", (req, res) -> {
            checkSession(req.session().id());
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
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
            AttributeJoint ullmanAttrJoint = new AttributeJoint();
            Relation ullmanRelation = new Relation();
            for (String item : req.queryParams()){
                if (item.contains(FormConstants.ATTRIBUTE)){
                    ullmanAttrJoint.addAttributes(attrList.get(req.queryParams(item)));
                }
                else {
                    ullmanRelation = relationList.get(req.queryParams(item));
                }
            }
            AttributeJoint ullmanResult = normalization.Normalization.simpleUllman(ullmanAttrJoint, ullmanRelation.getDFJoint());
            
            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.ULLMAN_RELATION, ullmanRelation);
            model.put(DataConstants.ULLMAN_ATTRJOINT, ullmanAttrJoint);
            model.put(DataConstants.ULLMAN_ALG, ullmanResult);
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ULLMAN);
            model.put(AppConstants.ULLMAN_RESULT, TemplateConstants.ULLMAN_RESULT);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        get("/find-normal-form", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
    }

    private static void checkSession(String id) {
        // if (!session.containsKey(id)){
           // halt(401, "No known session");
        //}
    }
    
    private static void addAttribute(Session reqSession, String reqAttr) {
        Map<String, Attribute> attrList = reqSession.attribute(SessionConstants.ATTRIBUTE_LIST);
        Attribute attr = new Attribute(reqAttr);
        attrList.put(attr.getAttribute(), attr);
    }
}
