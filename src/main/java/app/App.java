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
    public static void main(String[] args) {
        staticFileLocation("/public");
        String layout = "templates/layout.vtl";
        Map<String, Attribute> attrList = new HashMap<>();
        Map<String, FunctionalDependency> fdList = new HashMap<>();
        Map<String, DFJoint> fdJointList = new HashMap<>();
        Map<String, Relation> relationList = new HashMap<>();
        /*
        get("*", (req, res) -> {
            throw new Exception("Exceptions everywhere!");
        });
        enableDebugScreen();
        */
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("template", "templates/hello.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
               
        get("/attribute", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("attributes", attrList.values());
            model.put("template", "templates/attribute/attribute.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        
        post("/attribute", (req, res) -> {
            Attribute attr = new Attribute(req.queryParams("attribute"));
            attrList.put(attr.getAttribute(), attr);
            
            Map<String, Object> model = new HashMap<>();
            model.put("attributes", attrList);
            model.put("template", "templates/attribute/attribute.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        
        get("/fd", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("attributes", attrList.values());
            model.put("fds", fdList);
            model.put("template", "templates/fd/fd.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        
        post("/fd", (req, res) -> {
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
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        
        get("/fdjoint", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("fdjoints", fdJointList);
            model.put("fds", fdList);
            model.put("template", "templates/fdjoint/fdjoint.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        
        post("/fdjoint", (req, res) -> {
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
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        
        get("/relation", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("relations", relationList);
            model.put("attributes", attrList);
            model.put("fdjoints", fdJointList);
            model.put("template", "templates/relation/relation.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
        
        post("/relation", (req, res) -> {
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
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());
    }
}
