package com.github.fqroot0.pbjsonpath;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.github.fqroot0.pbjsonpath.exceptions.NotImplementedException;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.json.AbstractJsonProvider;


/**
 * @author fqroot0
 * Created on 2022-07-09
 */
public class PbProvider extends AbstractJsonProvider {
    @Override
    public Object parse(String json) throws InvalidJsonException {
        throw new NotImplementedException();
    }

    @Override
    public Object parse(byte[] json) throws InvalidJsonException {
        throw new NotImplementedException();
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        throw new NotImplementedException();
    }

    @Override
    public String toJson(Object obj) {
        throw new NotImplementedException();
    }

    @Override
    public Object createArray() {
        return new LinkedList<>();
    }

    @Override
    public Object createMap() {
        return new LinkedHashMap<>();
    }

    @Override
    public boolean isMap(Object obj) {
        if (obj instanceof DynamicMessage) {
            return true;
        }
        return super.isMap(obj);
    }

    @Override
    public Object getMapValue(Object obj, String key) {
        if (obj instanceof DynamicMessage) {
            DynamicMessage msg = (DynamicMessage) obj;
            Descriptors.FieldDescriptor fieldDescriptor = msg.getDescriptorForType().findFieldByName(key);
            Object value = msg.getField(fieldDescriptor);
            return value instanceof Descriptors.EnumValueDescriptor ? value.toString() : value;
        }
        return super.getMapValue(obj, key);
    }

    @Override
    public Collection<String> getPropertyKeys(Object obj) {
        if (obj instanceof DynamicMessage) {
            DynamicMessage msg = (DynamicMessage) obj;
            return msg.getDescriptorForType().getFields()
                    .stream()
                    .map(FieldDescriptor::getName)
                    .collect(Collectors.toList());
        }
        return super.getPropertyKeys(obj);
    }
}
