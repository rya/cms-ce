package com.enonic.cms.core.config;

import org.springframework.core.convert.converter.Converter;

import java.io.File;

/**
 * User: vfi
 * Date: 4/20/11
 * There is no String to File converter in Spring,
 * so this one was created
 */
public class String2FileConverter implements Converter<String, File> {

    @Override
    public File convert(String s) {
        File newFile = new File(s);
        return newFile;
    }
}
