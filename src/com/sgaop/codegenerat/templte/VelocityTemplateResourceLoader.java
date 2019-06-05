package com.sgaop.codegenerat.templte;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/5
 */
public class VelocityTemplateResourceLoader extends ResourceLoader {

    List<String> VM_GLOBALS = Arrays.asList("VM_global_library.vm");

    @Override
    public void init(ExtendedProperties extendedProperties) {

    }

    @Override
    public InputStream getResourceStream(String s) throws ResourceNotFoundException {
        if (VM_GLOBALS.contains(s)) {
            return null;
        }
        return new ByteArrayInputStream(s.getBytes());
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return false;
    }

    @Override
    public long getLastModified(Resource resource) {
        return 0;
    }
}
