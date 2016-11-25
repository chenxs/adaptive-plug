package com.hillchen.plug.adaptive.compiler;

import com.alibaba.dubbo.common.compiler.support.ClassUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by hillchen on 2016/11/25.
 */
public class CompilerJavaFileObject extends SimpleJavaFileObject {

    private ByteArrayOutputStream bytecode;

    private final CharSequence    source;

    public CompilerJavaFileObject(final String baseName, final CharSequence source){
        super(ClassUtils.toURI(baseName + ClassUtils.JAVA_EXTENSION), Kind.SOURCE);
        this.source = source;
    }

    public CompilerJavaFileObject(final String name, final Kind kind){
        super(ClassUtils.toURI(name), kind);
        source = null;
    }

    public CompilerJavaFileObject(URI uri, Kind kind){
        super(uri, kind);
        source = null;
    }

    @Override
    public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws UnsupportedOperationException {
        if (source == null) {
            throw new UnsupportedOperationException("source == null");
        }
        return source;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(getByteCode());
    }

    @Override
    public OutputStream openOutputStream() {
        return bytecode = new ByteArrayOutputStream();
    }

    public byte[] getByteCode() {
        return bytecode.toByteArray();
    }
}