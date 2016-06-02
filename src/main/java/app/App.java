/**
 * 
 */
package app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import datastructures.Attribute;
import datastructures.AttributeSet;
import datastructures.FDSet;
import datastructures.KeyJoint;
import datastructures.Relation;
import dependency.FunctionalDependency;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.*;

import constants.TemplateConstants;
import constants.XMLConstants;
import constants.AppConstants;
import constants.DataConstants;
import constants.SessionConstants;
import constants.FormConstants;
import constants.RoutesConstants;

/**
 * @author Pavel Nichita
 *
 */
public class App {
    private static Map<String, String> session = new HashMap<String, String>();

    public static void main(String[] args) {
        staticFileLocation(RoutesConstants.FILE_LOCATION);
        //port(80);

        before((req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
        });      

        get(RoutesConstants.ROOT, (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.WELCOME);
        }, new VelocityTemplateEngine());

        get(RoutesConstants.GLOBAL_VIEW, (req, res) -> {
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
            model.put(AppConstants.TEMPLATE, TemplateConstants.GLOBAL_VIEW);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post(RoutesConstants.SESSION, (req, res) -> {
            req.session(true);
            req.session().maxInactiveInterval(43400); // 12 hours in seconds
            req.session().attribute(SessionConstants.ATTRIBUTE_LIST, new HashMap<String, Attribute>());
            req.session().attribute(SessionConstants.FD_LIST, new HashMap<String, FunctionalDependency>());
            req.session().attribute(SessionConstants.FDJOINT_LIST, new HashMap<String, FDSet>());
            req.session().attribute(SessionConstants.RELATION_LIST, new HashMap<String, Relation>());
            session.put(req.session().id(), req.queryParams(FormConstants.USERNAME));

            res.redirect(RoutesConstants.ATTRIBUTE);
            return null;
        });

        get(RoutesConstants.SESSION, (req, res) -> {
            req.session().invalidate();
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.GOOD_BYE);
        }, new VelocityTemplateEngine());

        get(RoutesConstants.ATTRIBUTE, (req, res) -> {
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

        post(RoutesConstants.ATTRIBUTE, (req, res) -> {
            checkSession(req, res);

            addAttribute(req);

            res.redirect(RoutesConstants.ATTRIBUTE);
            return null;
        });
        
        get(RoutesConstants.SAVE, (req, res) -> {
            checkSession(req, res);
            
            saveSession(req, res);

            return null;
        });

        get(RoutesConstants.LOAD, (req, res) -> {
            checkSession(req, res);
            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(AppConstants.TEMPLATE, TemplateConstants.LOAD_SESSION);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post(RoutesConstants.LOAD, (req, res) -> {
            checkSession(req, res);
            
            loadSession(req, res);
            res.redirect(RoutesConstants.GLOBAL_VIEW);
            return null;
        });
        
        get(RoutesConstants.FD, (req, res) -> {
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

        post(RoutesConstants.FD, (req, res) -> {
            checkSession(req, res);

            addFD(req);

            res.redirect(RoutesConstants.FD);
            return null;
        });

        get(RoutesConstants.FDJOINT, (req, res) -> {
            checkSession(req, res);
            Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT);
            model.put(AppConstants.FDJOINTS_LIST, TemplateConstants.FDJOINTS_LIST);
            model.put(AppConstants.FDS_LIST_CHECKBOX, TemplateConstants.FDS_LIST_CHECKBOX);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post(RoutesConstants.FDJOINT, (req, res) -> {
            checkSession(req, res);

            addFDJoint(req);

            res.redirect(RoutesConstants.FDJOINT);
            return null;
        });

        get(RoutesConstants.RELATION, (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
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

        post(RoutesConstants.RELATION, (req, res) -> {
            checkSession(req, res);

            addRelation(req);

            res.redirect(RoutesConstants.RELATION);
            return null;
        });

        get(RoutesConstants.ULLMAN, (req, res) -> {
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

        post(RoutesConstants.ULLMAN, (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            AttributeSet ullmanAttrJoint = getAttrJoint(req);
            Relation ullmanRelation = relationList.get(req.queryParams(FormConstants.RELATION));
            AttributeSet ullmanResult = normalization.Normalization.simpleUllman(ullmanAttrJoint, ullmanRelation.getDFJoint());

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

        get(RoutesConstants.CALCULATE_KEYS, (req, res) -> {
            checkSession(req, res);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_KEYS);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post(RoutesConstants.CALCULATE_KEYS, (req, res) -> {
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

        get(RoutesConstants.CALCULATE_MINIMAL_COVER, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.CALCULATE_MINIMAL_COVER);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post(RoutesConstants.CALCULATE_MINIMAL_COVER, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            FDSet fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
            FDSet fdJointMinimal = new FDSet(fdJoint);
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

        get(RoutesConstants.PROJECTION, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
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

        post(RoutesConstants.PROJECTION, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);

            FDSet fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
            AttributeSet attrJoint = getAttrJoint(req);
            FDSet result = fdJoint.projectionOnAttributeJoint(attrJoint);

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

        get(RoutesConstants.FD_PARTOF_FDJOINT, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, FDSet> fdList = req.session().attribute(SessionConstants.FD_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(DataConstants.FDS, fdList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.FDS_LIST_RADIO, TemplateConstants.FDS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FD_PARTOF_FDJOINT);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post(RoutesConstants.FD_PARTOF_FDJOINT, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);

            FunctionalDependency fd = fdList.get(req.queryParams(FormConstants.FD));
            FDSet fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
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

        get(RoutesConstants.TEST_IMPLIES, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_A, TemplateConstants.FDJOINTS_LIST_RADIO_A);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_B, TemplateConstants.FDJOINTS_LIST_RADIO_B);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT_IMPLIES);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post(RoutesConstants.TEST_IMPLIES, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            FDSet fdJointA = fdJointList.get(req.queryParams(FormConstants.FDJOINT_A));
            FDSet fdJointB = fdJointList.get(req.queryParams(FormConstants.FDJOINT_B));
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

        get(RoutesConstants.EQUIVALENCE, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_A, TemplateConstants.FDJOINTS_LIST_RADIO_A);
            model.put(AppConstants.FDJOINTS_LIST_RADIO_B, TemplateConstants.FDJOINTS_LIST_RADIO_B);
            model.put(AppConstants.TEMPLATE, TemplateConstants.FDJOINT_EQUIVALENCE);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post(RoutesConstants.EQUIVALENCE, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            FDSet fdJointA = fdJointList.get(req.queryParams(FormConstants.FDJOINT_A));
            FDSet fdJointB = fdJointList.get(req.queryParams(FormConstants.FDJOINT_B));
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
        
        get(RoutesConstants.TEST_NORMAL_FORM, (req, res) -> {
            checkSession(req, res);
            Map<String, Object> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.TESTS_NORMAL_FORM);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post(RoutesConstants.TEST_NORMAL_FORM, (req, res) -> {
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

        get(RoutesConstants.TEST_KEYS, (req, res) -> {
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

        post(RoutesConstants.TEST_KEYS, (req, res) -> {
            checkSession(req, res);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            AttributeSet attrJoint = getAttrJoint(req);
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

        get(RoutesConstants.TEST_MINIMAL_COVER, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.FDJOINTS, fdJointList);
            model.put(AppConstants.FDJOINTS_LIST_RADIO, TemplateConstants.FDJOINTS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.TESTS_MINIMAL_COVER);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());

        post(RoutesConstants.TEST_MINIMAL_COVER, (req, res) -> {
            checkSession(req, res);
            Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);

            FDSet fdJoint = fdJointList.get(req.queryParams(FormConstants.FDJOINT));
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

        get(RoutesConstants.NORMALIZE, (req, res) -> {
            checkSession(req, res);
            Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

            Map<String, Object> model = new HashMap<>();
            model.put(SessionConstants.USERNAME, session.get(req.session().id()));
            model.put(DataConstants.RELATIONS, relationList);
            model.put(AppConstants.RELATIONS_LIST_RADIO, TemplateConstants.RELATIONS_LIST_RADIO);
            model.put(AppConstants.TEMPLATE, TemplateConstants.NORMALIZE);
            return new ModelAndView(model, TemplateConstants.LAYOUT);
        }, new VelocityTemplateEngine());
        
        post(RoutesConstants.NORMALIZE, (req, res) -> {
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
        
        get(RoutesConstants.E401, (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, TemplateConstants.E401);
        }, new VelocityTemplateEngine());
        
        post(RoutesConstants.DELETE_ATTRIBUTE, (req, res) -> {
            checkSession(req, res);
            String attr = req.queryParams(FormConstants.ATTRIBUTE);
            Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
            attrList.remove(attr);
            res.redirect(RoutesConstants.GLOBAL_VIEW);
            return null;
        });
        
        post(RoutesConstants.DELETE_FD, (req, res) -> {
            checkSession(req, res);
            String fd = req.queryParams(FormConstants.FD);
            Map<String, Attribute> fdList = req.session().attribute(SessionConstants.FD_LIST);
            fdList.remove(fd);
            res.redirect(RoutesConstants.GLOBAL_VIEW);
            return null;
        });
        
        post(RoutesConstants.DELETE_FDJOINT, (req, res) -> {
            checkSession(req, res);
            String fdJoint = req.queryParams(FormConstants.FDJOINT);
            Map<String, Attribute> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
            fdJointList.remove(fdJoint);
            res.redirect(RoutesConstants.GLOBAL_VIEW);
            return null;
        });
        
        post(RoutesConstants.DELETE_RELATION, (req, res) -> {
            checkSession(req, res);
            String relation = req.queryParams(FormConstants.RELATION);
            Map<String, Attribute> fdJointList = req.session().attribute(SessionConstants.RELATION_LIST);
            fdJointList.remove(relation);
            res.redirect(RoutesConstants.GLOBAL_VIEW);
            return null;
        });
    }

    private static void checkSession(Request req, Response res) {
        if (!session.containsKey(req.session().id())){
            res.redirect(RoutesConstants.E401);
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
        AttributeSet antecedent = new AttributeSet();
        AttributeSet consequent = new AttributeSet();
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
        Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
        FDSet fdJoint = new FDSet();
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
        Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
        Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
        Relation relation = new Relation();
        relation.setName(req.queryParams(FormConstants.RELATION));
        AttributeSet attrJoint = new AttributeSet();

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

    private static AttributeSet getAttrJoint(Request req) {
        Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
        AttributeSet attrJoint = new AttributeSet();

        for (String item : req.queryParams()){
            if (item.contains(FormConstants.ATTRIBUTE)){
                attrJoint.addAttributes(attrList.get(req.queryParams(item)));
            }
        }

        return attrJoint;
    }

    private static Response saveSession(Request req, Response res) {
        Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
        Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
        Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
        Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);
        HttpServletResponse raw;
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element root = doc.createElement(XMLConstants.SESSION);
            doc.appendChild(root);

            Element attributes = doc.createElement(XMLConstants.ATTRIBUTES);
            root.appendChild(attributes);
            for (Attribute item : attrList.values()) {
                Node element = doc.importNode(item.toXML(), true);
                attributes.appendChild(element);
            }

            Element fds = doc.createElement(XMLConstants.FDS);
            root.appendChild(fds);
            for (FunctionalDependency item : fdList.values()) {
                Node element = doc.importNode(item.toXML(), true);
                fds.appendChild(element);
            }

            Element fdJoints = doc.createElement(XMLConstants.FDJOINTS);
            root.appendChild(fdJoints);
            for (FDSet item : fdJointList.values()) {
                Node element = doc.importNode(item.toXML(), true);
                fdJoints.appendChild(element);
            }

            Element relations = doc.createElement(XMLConstants.RELATIONS);
            root.appendChild(relations);
            for (Relation item : relationList.values()) {
                Node element = doc.importNode(item.toXML(), true);
                relations.appendChild(element);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, XMLConstants.UTF_8);
            transformer.setOutputProperty(OutputKeys.INDENT, XMLConstants.YES);
            transformer.setOutputProperty(OutputKeys.METHOD, XMLConstants.XML);
            transformer.setOutputProperty(XMLConstants.INDENT_AMOUNT, XMLConstants.FOUR);
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);
            transformer.transform(source, result);
            //StreamResult result = new StreamResult(System.out);            

            byte[] bytes = bos.toByteArray();
            raw = res.raw();
            raw.getOutputStream().write(bytes);
            raw.getOutputStream().flush();
            raw.getOutputStream().close();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        res.type( XMLConstants.XML);
        res.raw();
        return res;
    }

    private static void loadSession(Request req, Response res) {
        Map<String, Attribute> attrList = req.session().attribute(SessionConstants.ATTRIBUTE_LIST);
        Map<String, FunctionalDependency> fdList = req.session().attribute(SessionConstants.FD_LIST);
        Map<String, FDSet> fdJointList = req.session().attribute(SessionConstants.FDJOINT_LIST);
        Map<String, Relation> relationList = req.session().attribute(SessionConstants.RELATION_LIST);

        InputStream input;

        try {
            req.attribute(XMLConstants.ORG_ECLIPSE_JETTY_MULTIPART_CONFIG, new MultipartConfigElement(XMLConstants.TEMP));
            input = req.raw().getPart(FormConstants.UPLOAD).getInputStream();

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(input);

            // Load Attributes from XML
            Node root = doc.getChildNodes().item(0);
            Node attribute = getNode(XMLConstants.ATTRIBUTES, root.getChildNodes());
            NodeList attributes = attribute.getChildNodes();
            List<String> attrStringList = getNodesValue(attributes, XMLConstants.ATTRIBUTE);
            addAttributes(attrList, attrStringList);

            // Load Functional Dependencies from XML
            Node fdNode = getNode(XMLConstants.FDS, root.getChildNodes());
            NodeList fds = fdNode.getChildNodes();
            List<Node> nodeList = getNodes(XMLConstants.FD, fds);

            for (Node item : nodeList) {
                Node antecedent = getNode(XMLConstants.ANTECEDENT, item.getChildNodes());
                attributes = antecedent.getChildNodes();
                List<String> antecedentAttr = getNodesValue(attributes, XMLConstants.ATTRIBUTE);
                AttributeSet attrAntecedent = new AttributeSet();
                for (String item2 : antecedentAttr) {
                    attrAntecedent.addAttributes(attrList.get(item2));
                }
                Node consequent = getNode(XMLConstants.CONSEQUENT, item.getChildNodes());
                attributes = consequent.getChildNodes();
                List<String> consequentAttr = getNodesValue(attributes, XMLConstants.ATTRIBUTE);
                AttributeSet attrConsequent = new AttributeSet();
                for (String item2 : consequentAttr) {
                    attrConsequent.addAttributes(attrList.get(item2));
                }
                FunctionalDependency fd = new FunctionalDependency(attrAntecedent, attrConsequent);
                fdList.put(fd.toString(), fd);
            }

            // Load FD Joints from XML
            Node fdJointNode = getNode(XMLConstants.FDJOINTS, root.getChildNodes());
            NodeList fdJoints = fdJointNode.getChildNodes();
            nodeList = getNodes(XMLConstants.FDJOINT, fdJoints);

            for (Node item : nodeList) {
                FDSet FDSet = new FDSet();
                FDSet.setName(getNodeValue(item.getChildNodes(), XMLConstants.NAME));
                fdNode = getNode(XMLConstants.FDS, item.getChildNodes());
                List<String> nodesValue = getNodesValue(fdNode.getChildNodes(), XMLConstants.FD);
                for (String item2 : nodesValue) {
                    FDSet.addDependency(fdList.get(item2));
                }
                fdJointList.put(FDSet.getName(), FDSet);
            }

            // Load Relations from XML
            Node relationNode = getNode(XMLConstants.RELATIONS, root.getChildNodes());
            NodeList relations = relationNode.getChildNodes();
            nodeList = getNodes(XMLConstants.RELATION, relations);

            for (Node item : nodeList) {
                Relation relation = new Relation();
                relation.setName(getNodeValue(item.getChildNodes(), XMLConstants.NAME));
                relation.setDFJoint(fdJointList.get(getNodeValue(item.getChildNodes(), XMLConstants.FDJOINT)));
                attribute = getNode(XMLConstants.ATTRIBUTES, item.getChildNodes());
                List<String> nodesValue = getNodesValue(attribute.getChildNodes(), XMLConstants.ATTRIBUTE);
                AttributeSet attrJoint = new AttributeSet();
                for (String item2 : nodesValue) {
                    attrJoint.addAttributes(attrList.get(item2));
                }
                relation.settAttrJoint(attrJoint);
                relationList.put(relation.getName(), relation);
            }
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServletException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static Node getNode(String tagName, NodeList nodes) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                return node;
            }
        }
        return null;
    }
    
    private static List<Node> getNodes(String tagName, NodeList nodes) {
        List<Node> nodeList = new ArrayList<>();
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                nodeList.add(node);
            }
        }
        return nodeList;
    }
    
    private static List<String> getNodesValue(NodeList nodes, String nodeName) {
        List<String> values = new ArrayList<>(); 
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(nodeName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    Node data = childNodes.item(y);
                    if ( data.getNodeType() == Node.TEXT_NODE )
                        values.add(data.getNodeValue());
                }
            }
        }
        return values;
    }
    
    private static String getNodeValue(NodeList nodes, String nodeName) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(nodeName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    Node data = childNodes.item(y);
                    if ( data.getNodeType() == Node.TEXT_NODE )
                        return data.getNodeValue();
                }
            }
        }
        return null;
    }

    private static void addAttributes(Map<String, Attribute> attrMap, List<String> attrList) {
        for (String item : attrList) {
            attrMap.put(item, new Attribute(item));
        }
    }
}
