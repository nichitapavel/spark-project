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

/**
 * @author Pavel Nichita
 *
 */
public class App {
    private static Map<String, String> sessions = new HashMap<String, String>();

    public static void main(String[] args) {
        staticFileLocation("/public");
        String layoutVtl = "templates/layout.vtl";
        String helloVtl = "templates/hello.vtl";
        String attributeVtl = "templates/attribute/attribute.vtl";

        get("/", (req, res) -> {
            System.out.println(req.ip());
            Map<String, Object> model = new HashMap<>();
            model.put("template", helloVtl);
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        post("/session", (req, res) -> {
            req.session(true);
            req.session().attribute("attrList", new HashMap<String, Attribute>());
            req.session().attribute("fdList", new HashMap<String, FunctionalDependency>());
            req.session().attribute("fdJointList", new HashMap<String, DFJoint>());
            req.session().attribute("relationList", new HashMap<String, Relation>());
            sessions.put(req.session().id(), req.queryParams("username"));
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        get("/session", (req, res) -> {
            req.session().id();
            req.session().invalidate();
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        get("/attribute", (req, res) -> {
            checkSession(req.session().id());
            System.out.println("Cookie: " + req.cookie("JSESSIONID"));
            System.out.println("Session ID: " + req.session().id());
            Map<String, Object> model = new HashMap<>();
            Map<String, Attribute> attrList = req.session().attribute("attrList");
            model.put("attributes", attrList.values());
            model.put("template", attributeVtl);
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        post("/attribute", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute("attrList");
            Attribute attr = new Attribute(req.queryParams("attribute"));
            attrList.put(attr.getAttribute(), attr);

            Map<String, Object> model = new HashMap<>();
            model.put("attributes", attrList);
            model.put("template", "templates/attribute/attribute.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        get("/fd", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute("attrList");
            Map<String, FunctionalDependency> fdList = req.session().attribute("fdList");
            Map<String, Object> model = new HashMap<>();
            model.put("attributes", attrList.values());
            model.put("fds", fdList);
            model.put("template", "templates/fd/fd.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        post("/fd", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute("attrList");
            Map<String, FunctionalDependency> fdList = req.session().attribute("fdList");
            AttributeJoint antecedent = new AttributeJoint();
            AttributeJoint consequent = new AttributeJoint();
            for (String attrr : req.queryParams()){
                if (attrr.contains("ant-")){
                    antecedent.addAttributes(attrList.get(req.queryParams(attrr)));
                }
                else {
                    consequent.addAttributes(attrList.get(req.queryParams(attrr)));
                }
            }
            FunctionalDependency fd = new FunctionalDependency(antecedent, consequent);
            fdList.put(fd.toString(), fd);

            Map<String, Object> model = new HashMap<>();
            model.put("attributes", attrList);
            model.put("fds", fdList);
            model.put("template", "templates/fd/fd.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        get("/fdjoint", (req, res) -> {
            checkSession(req.session().id());
            Map<String, FunctionalDependency> fdList = req.session().attribute("fdList");
            Map<String, DFJoint> fdJointList = req.session().attribute("fdJointList");
            Map<String, Object> model = new HashMap<>();
            model.put("fdjoints", fdJointList);
            model.put("fds", fdList);
            model.put("template", "templates/fdjoint/fdjoint.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        post("/fdjoint", (req, res) -> {
            checkSession(req.session().id());
            Map<String, FunctionalDependency> fdList = req.session().attribute("fdList");
            Map<String, DFJoint> fdJointList = req.session().attribute("fdJointList");
            DFJoint fdJoint = new DFJoint();
            fdJoint.setName(req.queryParams("fdjoint-name"));

            for (String fd : req.queryParams()){
                if (!fd.equals("fdjoint-name")){
                    fdJoint.addDependency(fdList.get(fd));;
                }
            }
            fdJointList.put(req.queryParams("fdjoint-name"), fdJoint);
            Map<String, Object> model = new HashMap<>();
            model.put("fdjoints", fdJointList);
            model.put("fds", fdList);
            model.put("template", "templates/fdjoint/fdjoint.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        get("/relation", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute("attrList");
            Map<String, DFJoint> fdJointList = req.session().attribute("fdJointList");
            Map<String, Relation> relationList = req.session().attribute("relationList");
            Map<String, Object> model = new HashMap<>();
            model.put("relations", relationList);
            model.put("attributes", attrList);
            model.put("fdjoints", fdJointList);
            model.put("template", "templates/relation/relation.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        post("/relation", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute("attrList");
            Map<String, DFJoint> fdJointList = req.session().attribute("fdJointList");
            Map<String, Relation> relationList = req.session().attribute("relationList");
            Relation relation = new Relation();
            relation.setName(req.queryParams("relation-name"));
            AttributeJoint attrJoint = new AttributeJoint();

            for (String item : req.queryParams()){
                if (item.contains("fdjoint-name")){
                    relation.setDFJoint(fdJointList.get(req.queryParams(item)));
                }
                else if (item.contains("relation-name")) {}
                else {
                    attrJoint.addAttributes(attrList.get(req.queryParams(item)));
                }
            }
            if (!attrJoint.isNull()) {
                relation.settAttrJoint(attrJoint);
            }

            relationList.put(req.queryParams("relation-name"), relation);
            Map<String, Object> model = new HashMap<>();
            model.put("relations", relationList);
            model.put("fdjoints", fdJointList);
            model.put("attributes", attrList);
            model.put("template", "templates/relation/relation.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        get("/ullman", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute("attrList");
            Map<String, Relation> relationList = req.session().attribute("relationList");
            Map<String, Object> model = new HashMap<>();
            model.put("relations", relationList);
            model.put("attributes", attrList);
            model.put("template", "templates/analyze/ullman.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        post("/ullman", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Attribute> attrList = req.session().attribute("attrList");
            Map<String, Relation> relationList = req.session().attribute("relationList");
            AttributeJoint attrJoint = new AttributeJoint();
            Relation relation = new Relation();
            for (String item : req.queryParams()){
                if (item.contains("attr-")){
                    attrJoint.addAttributes(attrList.get(req.queryParams(item)));
                }
                else {
                    relation = relationList.get(req.queryParams(item));
                }
            }
            attrJoint = normalization.Normalization.simpleUllman(attrJoint, relation.getDFJoint());

            Map<String, Object> model = new HashMap<>();
            model.put("ullman", attrJoint);
            model.put("relations", relationList);
            model.put("attributes", attrList);
            model.put("template", "templates/analyze/ullman.vtl");
            model.put("result", "templates/analyze/ullman-result.vtl");
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());

        get("/find-normal-form", (req, res) -> {
            checkSession(req.session().id());
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, layoutVtl);
        }, new VelocityTemplateEngine());
    }

    private static void checkSession(String str) {
        if (!sessions.containsKey(str)){
            halt(401, "No known session");
        }
    }
}
