package com.sgaop.codegenerat.util;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.lang.jvm.annotation.JvmAnnotationAttributeValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationClassValue;
import com.intellij.lang.jvm.annotation.JvmAnnotationConstantValue;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.source.tree.java.PsiNameValuePairImpl;
import com.sgaop.codegenerat.vo.JavaFieldVO;

import javax.swing.*;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/12/24
 */
public class JavaFieldUtil {

    public static final String[] IS_FIELD = {"org.nutz.dao.entity.annotation.Name", "org.nutz.dao.entity.annotation.Id", "org.nutz.dao.entity.annotation.Column"};

    private static final String COMMENT = "org.nutz.dao.entity.annotation.Comment";

    private static final String DICT = "com.nutzfw.annotation.NutzFw";

    public static boolean isDate(PsiField field) {
        String type = field.getType().getPresentableText();
        return "Date".equals(type) || "Timestamp".equals(type);
    }

    public static boolean isPrimaryKey(PsiField field) {
        PsiAnnotation annotation0 = field.getAnnotation(IS_FIELD[0]);
        PsiAnnotation annotation1 = field.getAnnotation(IS_FIELD[1]);
        return annotation0 != null || annotation1 != null;
    }


    public static String getDbNameAndIsColumn(PsiField field) {
        PsiAnnotation annotation0 = field.getAnnotation(IS_FIELD[0]);
        PsiAnnotation annotation1 = field.getAnnotation(IS_FIELD[1]);
        PsiAnnotation annotation2 = field.getAnnotation(IS_FIELD[2]);
        boolean isField = annotation0 != null || annotation1 != null || annotation2 != null;
        if (isField) {
            List<JvmAnnotationAttribute> attributes = annotation2.getAttributes();
            if (attributes.size() == 0) {
                return field.getName();
            } else {
                return ((PsiNameValuePairImpl) attributes.get(0)).getLiteralValue();
            }
        }
        return null;
    }

    public static String getComment(PsiField field) {
        PsiAnnotation annotation = field.getAnnotation(COMMENT);
        if (annotation != null) {
            List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
            if (attributes.size() > 0) {
                return ((PsiNameValuePairImpl) attributes.get(0)).getLiteralValue();
            }
        }
        return "";
    }

    public static JavaFieldVO getNutzFw(PsiField field) {
        JavaFieldVO javaField = new JavaFieldVO();
        javaField.setName(field.getName());
        javaField.setPrimaryKey(JavaFieldUtil.isPrimaryKey(field));
        javaField.setDate(JavaFieldUtil.isDate(field));
        javaField.setComment(JavaFieldUtil.getComment(field));
        javaField.setType(field.getType().getPresentableText());
        javaField.setFullType(field.getType().getCanonicalText());
        javaField.setShow(true);
        javaField.setMaxLength(50);
        PsiAnnotation annotation = field.getAnnotation(DICT);
        if (annotation != null) {
            List<JvmAnnotationAttribute> attributes = annotation.getAttributes();
            if (attributes.size() > 0) {
                for (JvmAnnotationAttribute attribute : attributes) {
                    String name = attribute.getAttributeName();
                    try {
                        String value = ((PsiNameValuePairImpl) attribute).getLiteralValue();
                        /**
                         * 表关联类
                         */
                        String oneOneClassName = "";
                        /**
                         * 表关联类全路径
                         */
                        String oneOneClassQualifiedName = "";
                        JvmAnnotationAttributeValue attributeValue = attribute.getAttributeValue();
                        if (value == null && attributeValue instanceof JvmAnnotationConstantValue) {
                            value = ((JvmAnnotationConstantValue) attributeValue).getConstantValue().toString();
                        } else if (value == null && attributeValue instanceof JvmAnnotationClassValue) {
                            oneOneClassQualifiedName = ((JvmAnnotationClassValue) attributeValue).getQualifiedName();
                            oneOneClassName = ((JvmAnnotationClassValue) attributeValue).getClazz().getName();
                        }
                        if (value != null) {
                            switch (name) {
                                case "required":
                                    javaField.setRequired(Boolean.parseBoolean(value));
                                    break;
                                case "text":
                                    javaField.setText(Integer.parseInt(value));
                                    break;
                                case "attachment":
                                    javaField.setAttachment(Boolean.parseBoolean(value));
                                    break;
                                case "attachmentMultiple":
                                    javaField.setAttachmentMultiple(Boolean.parseBoolean(value));
                                    break;
                                case "attachmentAllIsImg":
                                    javaField.setAttachmentAllIsImg(Boolean.parseBoolean(value));
                                    break;
                                case "attachSuffix":
                                    javaField.setAttachSuffix(value);
                                    break;
                                case "dictCode":
                                    javaField.setDict(true);
                                    javaField.setDictCode(value);
                                    break;
                                case "multiDict":
                                    javaField.setMultiDict(Boolean.parseBoolean(value));
                                    break;
                                case "placeholder":
                                    javaField.setPlaceholder(value);
                                    break;
                                case "maxLength":
                                    javaField.setMaxLength(Integer.parseInt(value));
                                    break;
                                case "show":
                                    javaField.setShow(Boolean.parseBoolean(value));
                                    break;
                                case "oneOneField":
                                    javaField.setOneOneField(value);
                                    break;
                                default:
                                    break;
                            }
                        } else if ("oneOne".equals(name) && !"".equals(oneOneClassName)) {
                            javaField.setOneOne(true);
                            javaField.setOneOneClassName(oneOneClassName);
                            javaField.setOneOneClassQualifiedName(oneOneClassQualifiedName);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, javaField.getName() + ":" + e.getMessage(), "错误提示", JOptionPane.ERROR_MESSAGE, null);
                    }
                }
            }
        }
        if (javaField.getMaxLength() <= 0) {
            javaField.setMaxLength(10);
        }
        if (Strings.isNullOrEmpty(javaField.getPlaceholder())) {
            javaField.setPlaceholder("请输入...");
        }
        if (javaField.isAttachment() && Strings.isNullOrEmpty(javaField.getAttachSuffix())) {
            javaField.setAttachSuffix("xlsx,xls,png,jpg,doc,docx");
        }
        return javaField;
    }


}
