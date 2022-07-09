package com.github.fqroot0.pbjsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.tutorial.protos.AddressBook;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.DynamicMessage;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author fqroot0
 * Created on 2022-07-09
 */
@Slf4j
class PbParseContextImplTest {
    /**
     * people {
     *   name: "foo0"
     *   email: "foo0@xxx.com"
     *   phones {
     *     number: "000-000-000"
     *   }
     * }
     * people {
     *   name: "foo1"
     *   id: 1
     *   email: "foo1@xxx.com"
     *   phones {
     *     number: "000-000-001"
     *   }
     * }
     * people {
     *   name: "foo2"
     *   id: 2
     *   email: "foo2@xxx.com"
     *   phones {
     *     number: "000-000-002"
     * }
     * }
     */
    @Test
    void test() throws Exception {
        Descriptor descriptor = AddressBook.getDescriptor();
        AddressBook addrbook = PbTestHelper.genAddressBook();
        byte[] bytes = addrbook.toByteArray();

        DynamicMessage dynamicMessage = DynamicMessage.parseFrom(descriptor, bytes);

        Configuration conf = Configuration.defaultConfiguration()
                .jsonProvider(new PbProvider());
        PbParseContextImpl pbParseContext = new PbParseContextImpl(conf);
        DocumentContext ctx = pbParseContext.parse(dynamicMessage);

        Object obj;
        obj = ctx.read("$.people[1].name");
        assertEquals(obj, "foo1");
        log.info("{}", obj);

        obj = ctx.read("$..number");
        assertEquals(obj, Arrays.asList("000-000-000", "000-000-001", "000-000-002"));
        log.info("{}", obj);


        obj = ctx.read("$.people[0:2]");
        assertEquals(((List) obj).size(), 2);
        log.info("{}", obj);

        obj = ctx.read("$..people[?(@.id==0)].name");
        assertEquals(((List) obj).size(), 1);
        assertEquals(((List) obj).get(0), "foo0");
        log.info("{}", obj);

    }

}