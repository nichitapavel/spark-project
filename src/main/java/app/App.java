/**
 * 
 */
package app;

import java.util.HashMap;
import java.util.Map;

import datastructures.Attribute;
import datastructures.AttributeJoint;
import datastructures.DFJoint;
import datastructures.KeyJoint;
import datastructures.Relation;
import dependency.FunctionalDependency;
import spark.ModelAndView;
import spark.Request;
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
            
            addAttribute(req);

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
            
            addFD(req);

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
            
            addFDJoint(req);

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
            
            addRelation(req);

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
            model.put(DataConstants.RELATION, ullmanRelation);
            model.put(DataConstants.ATTRJOINT, ullmanAttrJoint);
            model.put(DataConstants.ULLMAN_ALG, ullmanResult);
            model.put(DataConstants.RELATIONS, relationList);
            model.put(DataConstants.ATTRIBUTES, attrList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.ATTRIBUTES_LIST_CHECKBOX, TemplateConstants.ATTRIBUTES_LIST_CHECKBOX);
            model.put(AppConstants.TEMPLATE, TemplateConstants.ULLMAN);
            model.put(AppConstants.ULLMAN_RESULT, TemplateConstants.ULLMAN_RESULT);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        get("/calculate-keys", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_KEYS);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post("/calculate-keys", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
            
            Relation relation = relationList.get(req.queryParams(FormConstants.RELATION));
            KeyJoint keyJoint = relation.calculateKeyJoint();
            
            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATION, relation);
            model.put(DataConstants.KEY_JOINT, keyJoint);
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.CALCULATE_KEYS_RESULT, TemplateConstants.CALCULATE_KEYS_RESULT);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_KEYS);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        get("/calculate-minimal-cover", (req, res) -> {
            checkSession(req.session().id());
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_MINIMAL_COVER);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post("/calculate-minimal-cover", (req, res) -> {
            checkSession(req.session().id());
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            DFJoint fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
            DFJoint fdJointMinimal = new DFJoint(fdJoint);
            fdJointMinimal.removeRareAttributes(true);
            
            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDJOINT, fdJointMinimal);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.CALCULATE_MINIMAL_COVER_RESULT, TemplateConstants.CALCULATE_MINIMAL_COVER_RESULT);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_MINIMAL_COVER);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        get("/projection", (req, res) -> {
            checkSession(req.session().id());
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, DFJoint> fdList = req.session().attribute(SessionConstants.FD_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.FDS_LIST_RADIO, TemplateConstants.FDS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.PROJECTION);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post("/projection", (req, res) -> {
            checkSession(req.session().id());
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, DFJoint> fdList = req.session().attribute(SessionConstants.FD_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.FDS_LIST_RADIO, TemplateConstants.FDS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.PROJECTION);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        get("/fd-partof-fdjoint", (req, res) -> {
            checkSession(req.session().id());
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
            checkSession(req.session().id());
            Map<String, DFJoint> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
            
            FunctionalDependency fd = fdList.get(req.queryParams(FormConstants.FD));
            DFJoint fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
            
            Boolean result = fd.belongsTo(fdJoint, null);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.BOOLEAN, result);
            model.put(DataConstants.FD, fd);
            model.put(DataConstants.FDJOINT, fdJoint);
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.FDS_LIST_RADIO, TemplateConstants.FDS_LIST_RADIO);
            model.put(AppConstants.FD_PARTOF_FDJOINT_RESULT, TemplateConstants.FD_PARTOF_FDJOINT_RESULT);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FD_PARTOF_FDJOINT);
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
}
