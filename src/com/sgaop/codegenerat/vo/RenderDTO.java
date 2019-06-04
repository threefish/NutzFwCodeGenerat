package com.sgaop.codegenerat.vo;

import java.nio.file.Path;

public class RenderDTO {
    Path servicePath;
    Path serviceImplPath;
    Path actionPath;
    Path indexPath;
    Path editPath;

    boolean service;

    boolean serviceImpl;

    boolean action;

    boolean htmlPath;

    String servicePackageText;
    String serviceImplPackageText;
    String actionPackageText;
    String basePathText;
    String htmlPaths;

    public String getServicePackageText() {
        return servicePackageText;
    }

    public void setServicePackageText(String servicePackageText) {
        this.servicePackageText = servicePackageText;
    }

    public String getServiceImplPackageText() {
        return serviceImplPackageText;
    }

    public void setServiceImplPackageText(String serviceImplPackageText) {
        this.serviceImplPackageText = serviceImplPackageText;
    }

    public String getActionPackageText() {
        return actionPackageText;
    }

    public void setActionPackageText(String actionPackageText) {
        this.actionPackageText = actionPackageText;
    }

    public String getBasePathText() {
        return basePathText;
    }

    public void setBasePathText(String basePathText) {
        this.basePathText = basePathText;
    }

    public String getHtmlPaths() {
        return htmlPaths;
    }

    public void setHtmlPaths(String htmlPaths) {
        this.htmlPaths = htmlPaths;
    }

    public Path getServicePath() {
        return servicePath;
    }

    public void setServicePath(Path servicePath) {
        this.servicePath = servicePath;
    }

    public Path getServiceImplPath() {
        return serviceImplPath;
    }

    public void setServiceImplPath(Path serviceImplPath) {
        this.serviceImplPath = serviceImplPath;
    }

    public Path getActionPath() {
        return actionPath;
    }

    public void setActionPath(Path actionPath) {
        this.actionPath = actionPath;
    }

    public Path getIndexPath() {
        return indexPath;
    }

    public void setIndexPath(Path indexPath) {
        this.indexPath = indexPath;
    }

    public Path getEditPath() {
        return editPath;
    }

    public void setEditPath(Path editPath) {
        this.editPath = editPath;
    }

    public boolean isService() {
        return service;
    }

    public void setService(boolean service) {
        this.service = service;
    }

    public boolean isServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(boolean serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public boolean isHtmlPath() {
        return htmlPath;
    }

    public void setHtmlPath(boolean htmlPath) {
        this.htmlPath = htmlPath;
    }
}
