package com.github.fqroot0.pbjsonpath;

import com.google.protobuf.DynamicMessage;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.internal.ParseContextImpl;

/**
 * @author fqroot0
 * Created on 2022-07-09
 */
public class PbParseContextImpl extends ParseContextImpl {

    private final Configuration configuration;

    public PbParseContextImpl() {
        this(Configuration.defaultConfiguration());
    }

    public PbParseContextImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    public DocumentContext parse(DynamicMessage dynamicMessage) {
        return new PbContext(dynamicMessage, configuration);
    }
}
