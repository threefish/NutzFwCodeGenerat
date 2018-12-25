package com.sgaop.codegenerat.idea.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiUtilBase;
import com.sgaop.codegenerat.idea.ProjectPluginConfig;
import com.sgaop.codegenerat.ui.CreateServiceImplFram;
import com.sgaop.codegenerat.util.JavaFieldUtil;
import com.sgaop.codegenerat.vo.JavaField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 306955302@qq.com
 * @date: 2018/5/30
 * 描述此类：
 */
public class CodeGeneratAction extends DumbAwareAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = this.getEditor(e.getDataContext());
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (psiFile instanceof PsiJavaFileImpl) {
            this.actionPerformedImpl(project, editor);
        }
    }

    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = this.getEditor(event.getDataContext());
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (psiFile instanceof PsiJavaFileImpl) {
            setEnabledInModalContext(true);
            getTemplatePresentation().setEnabledAndVisible(true);
        } else {
            setEnabledInModalContext(false);
            getTemplatePresentation().setEnabledAndVisible(false);
        }
    }


    @Nullable
    protected Editor getEditor(@NotNull DataContext dataContext) {
        return CommonDataKeys.EDITOR.getData(dataContext);
    }

    public void actionPerformedImpl(@NotNull Project project, Editor editor) {
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (psiFile instanceof PsiJavaFileImpl) {
            PsiJavaFileImpl psiJavaFile = (PsiJavaFileImpl) psiFile;
            String packageName = psiJavaFile.getPackageStatement().getPackageName();
            String filename = psiJavaFile.getName().replace(".java", "");
            Application applicationManager = ApplicationManager.getApplication();
            CommandProcessor processor = CommandProcessor.getInstance();
            //当前所在文档
            Document document = editor.getDocument();
            List<JavaField> javaFields = new ArrayList<>();
            PsiField[] psiFields = psiJavaFile.getClasses()[0].getFields();
            for (PsiField field : psiFields) {
                String dbName = JavaFieldUtil.getDbNameAndIsColumn(field);
                if (dbName != null) {
                    JavaField javaField = new JavaField();
                    javaField.setName(field.getName());
                    javaField.setPrimaryKey(JavaFieldUtil.isPrimaryKey(field));
                    javaField.setDate(JavaFieldUtil.isDate(field));
                    javaField.setDbName(dbName);
                    javaField.setComment(JavaFieldUtil.getComment(field));
                    javaField.setType(field.getType().getPresentableText());
                    javaField.setFullType(field.getType().getCanonicalText());
                    String dictCode = JavaFieldUtil.getDictCode(field);
                    if (dictCode != null) {
                        javaField.setDict(true);
                        javaField.setDictCode(dictCode);
                    }
                    javaFields.add(javaField);
                }
            }
            CreateServiceImplFram dialog = new CreateServiceImplFram(
                    new ProjectPluginConfig(applicationManager, processor, document, project, editor, psiFile, javaFields),
                    packageName, filename);
            dialog.pack();
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "不是java类！", "错误提示", JOptionPane.ERROR_MESSAGE, null);
        }
    }
}
