package at.wtioit.intellij.plugins.odoo;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import com.jetbrains.python.psi.PyClass;
import at.wtioit.intellij.plugins.odoo.models.OdooModelUtil;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.intellij.psi.xml.XmlAttribute;

public class OdooDocumentationProvider implements DocumentationProvider {
    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        // Implemented: documentation for Odoo model
        if (element instanceof PyClass) {
            String modelName = OdooModelUtil.detectName((PyClass) element);
            if (modelName != null) {
                return "<b>Odoo Model:</b> " + modelName + "<br>" +
                        "<a href='https://www.odoo.com/documentation/16.0/developer/reference/addons/orm.html'>Odoo ORM Docs</a>";
            }
        }
        // Documentation for Python field (self.<field>)
        if (element instanceof PyReferenceExpression) {
            PyReferenceExpression ref = (PyReferenceExpression) element;
            PsiElement qualifier = ref.getQualifier();
            if (qualifier instanceof PyReferenceExpression && "self".equals(((PyReferenceExpression) qualifier).getReferencedName())) {
                String fieldName = ref.getReferencedName();
                PyClass pyClass = com.intellij.psi.util.PsiTreeUtil.getParentOfType(element, PyClass.class);
                if (pyClass != null && fieldName != null) {
                    OdooModelUtil.OdooFieldInfo field = OdooModelUtil.getFieldInfo(pyClass, fieldName);
                    if (field != null) {
                        StringBuilder doc = new StringBuilder();
                        doc.append("<b>Odoo Field:</b> ").append(field.name).append("<br>");
                        doc.append("<b>Type:</b> ").append(field.type).append("<br>");
                        if (field.comodelName != null) doc.append("<b>comodel_name:</b> ").append(field.comodelName).append("<br>");
                        doc.append("<a href='https://www.odoo.com/documentation/16.0/developer/reference/addons/orm.html'>Odoo ORM Docs</a>");
                        return doc.toString();
                    }
                }
            }
        }
        // Documentation for field in XML <field name="...">
        if (element instanceof XmlAttribute) {
            XmlAttribute attr = (XmlAttribute) element;
            if ("name".equals(attr.getName())) {
                com.intellij.psi.xml.XmlTag fieldTag = attr.getParent();
                if (fieldTag != null && "field".equals(fieldTag.getName())) {
                    com.intellij.psi.xml.XmlTag modelTag = fieldTag.getParentTag();
                    if (modelTag != null) {
                        String modelName = modelTag.getAttributeValue("model");
                        String fieldName = attr.getValue();
                        if (modelName != null && fieldName != null) {
                            java.util.Map<String, com.intellij.psi.PsiElement> models = OdooModelUtil.findAllOdooModels(element.getProject());
                            if (models.containsKey(modelName) && models.get(modelName) instanceof PyClass) {
                                PyClass pyClass = (PyClass) models.get(modelName);
                                OdooModelUtil.OdooFieldInfo field = OdooModelUtil.getFieldInfo(pyClass, fieldName);
                                if (field != null) {
                                    StringBuilder doc = new StringBuilder();
                                    doc.append("<b>Odoo Field:</b> ").append(field.name).append("<br>");
                                    doc.append("<b>Type:</b> ").append(field.type).append("<br>");
                                    if (field.comodelName != null) doc.append("<b>comodel_name:</b> ").append(field.comodelName).append("<br>");
                                    doc.append("<a href='https://www.odoo.com/documentation/16.0/developer/reference/addons/orm.html'>Odoo ORM Docs</a>");
                                    return doc.toString();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        // Implemented: link to Odoo ORM documentation
        if (element instanceof PyClass) {
            return java.util.Collections.singletonList("https://www.odoo.com/documentation/16.0/developer/reference/addons/orm.html");
        }
        return null;
    }
} 