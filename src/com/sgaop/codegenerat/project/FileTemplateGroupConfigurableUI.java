package com.sgaop.codegenerat.project;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/28
 */
public class FileTemplateGroupConfigurableUI {

    private TextFieldWithBrowseButton templatePath;

    private JTable tamplateTable;

    private JPanel root;
    private JTextField userName;
    private JTextField userMail;

    public FileTemplateGroupConfigurableUI(Project project, ToolCfigurationData configuration) {
        FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        templatePath.addBrowseFolderListener("请选择模板目录", "请将模板解压至模板目录中", project, fileChooserDescriptor);
        templatePath.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent documentEvent) {
                if (documentEvent.getType().equals(DocumentEvent.EventType.INSERT)) {
                    loadTable();
                }
            }
        });
        userName.setText(configuration.getUserName());
        userMail.setText(configuration.getUserMail());
        templatePath.setText(configuration.getTemplatePath());
        tamplateTable.setRowHeight(25);
    }

    private void loadTable() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"模板名称"}, 0);
        Path path = Paths.get(templatePath.getText());
        File[] list = path.toFile().listFiles();
        for (File file : list) {
            if (file.isFile()) {
                continue;
            }
            model.addRow(new String[]{file.getName()});
        }
        tamplateTable.setModel(model);
    }

    public TextFieldWithBrowseButton getTemplatePath() {
        return templatePath;
    }

    public JTable getTamplateTable() {
        return tamplateTable;
    }

    public JPanel getRoot() {
        return root;
    }

    public JTextField getUserName() {
        return userName;
    }

    public JTextField getUserMail() {
        return userMail;
    }
}
