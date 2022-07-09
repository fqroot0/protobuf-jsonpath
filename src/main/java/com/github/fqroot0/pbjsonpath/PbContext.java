package com.github.fqroot0.pbjsonpath;

import static com.jayway.jsonpath.JsonPath.compile;
import static com.jayway.jsonpath.internal.Utils.notEmpty;
import static com.jayway.jsonpath.internal.Utils.notNull;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.EvaluationListener;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.MapFunction;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.internal.JsonContext;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.cache.Cache;
import com.jayway.jsonpath.spi.cache.CacheProvider;

/**
 * @author fqroot0
 * Created on 2022-07-09
 */
public class PbContext implements DocumentContext {

    private static final Logger logger = LoggerFactory.getLogger(JsonContext.class);

    private final Configuration configuration;
    private final Object json;

    PbContext(Object json, Configuration configuration) {
        notNull(json, "json can not be null");
        notNull(configuration, "configuration can not be null");
        this.configuration = configuration;
        this.json = json;
    }


    public Configuration configuration() {
        return configuration;
    }

    //------------------------------------------------
    //
    // ReadContext impl
    //
    //------------------------------------------------
    public Object json() {
        return json;
    }

    public String jsonString() {
        return configuration.jsonProvider().toJson(json);
    }

    public <T> T read(String path, Predicate... filters) {
        notEmpty(path, "path can not be null or empty");
        return read(pathFromCache(path, filters));
    }

    public <T> T read(String path, Class<T> type, Predicate... filters) {
        return convert(read(path, filters), type, configuration);
    }

    public <T> T read(JsonPath path) {
        notNull(path, "path can not be null");
        return path.read(json, configuration);
    }

    public <T> T read(JsonPath path, Class<T> type) {
        return convert(read(path), type, configuration);
    }

    public <T> T read(JsonPath path, TypeRef<T> type) {
        return convert(read(path), type, configuration);
    }

    public <T> T read(String path, TypeRef<T> type) {
        return convert(read(path), type, configuration);
    }

    public ReadContext limit(int maxResults) {
        return withListeners(new PbContext.LimitingEvaluationListener(maxResults));
    }

    public ReadContext withListeners(EvaluationListener... listener) {
        return new PbContext(json, configuration.setEvaluationListeners(listener));
    }

    private <T> T convert(Object obj, Class<T> targetType, Configuration conf) {
        return conf.mappingProvider().map(obj, targetType, conf);
    }

    private <T> T convert(Object obj, TypeRef<T> targetType, Configuration conf) {
        return conf.mappingProvider().map(obj, targetType, conf);
    }

    public DocumentContext set(String path, Object newValue, Predicate... filters) {
        return set(pathFromCache(path, filters), newValue);
    }

    public DocumentContext set(JsonPath path, Object newValue) {
        List<String> modified = path.set(json, newValue, configuration.addOptions(Option.AS_PATH_LIST));
        if (logger.isDebugEnabled()) {
            for (String p : modified) {
                logger.debug("Set path {} new value {}", p, newValue);
            }
        }
        return this;
    }

    public DocumentContext map(String path, MapFunction mapFunction, Predicate... filters) {
        map(pathFromCache(path, filters), mapFunction);
        return this;
    }

    public DocumentContext map(JsonPath path, MapFunction mapFunction) {
        Object obj = path.map(json, mapFunction, configuration);
        return obj == null ? null : this;
    }

    public DocumentContext delete(String path, Predicate... filters) {
        return delete(pathFromCache(path, filters));
    }

    public DocumentContext delete(JsonPath path) {
        List<String> modified = path.delete(json, configuration.addOptions(Option.AS_PATH_LIST));
        if (logger.isDebugEnabled()) {
            for (String p : modified) {
                logger.debug("Delete path {}", p);
            }
        }
        return this;
    }

    public DocumentContext add(String path, Object value, Predicate... filters) {
        return add(pathFromCache(path, filters), value);
    }

    public DocumentContext add(JsonPath path, Object value) {
        List<String> modified = path.add(json, value, configuration.addOptions(Option.AS_PATH_LIST));
        if (logger.isDebugEnabled()) {
            for (String p : modified) {
                logger.debug("Add path {} new value {}", p, value);
            }
        }
        return this;
    }

    public DocumentContext put(String path, String key, Object value, Predicate... filters) {
        return put(pathFromCache(path, filters), key, value);
    }

    public DocumentContext renameKey(String path, String oldKeyName, String newKeyName, Predicate... filters) {
        return renameKey(pathFromCache(path, filters), oldKeyName, newKeyName);
    }

    public DocumentContext renameKey(JsonPath path, String oldKeyName, String newKeyName) {
        List<String> modified = path.renameKey(json, oldKeyName, newKeyName, configuration.addOptions(Option.AS_PATH_LIST));
        if (logger.isDebugEnabled()) {
            for (String p : modified) {
                logger.debug("Rename path {} new value {}", p, newKeyName);
            }
        }
        return this;
    }

    public DocumentContext put(JsonPath path, String key, Object value) {
        List<String> modified = path.put(json, key, value, configuration.addOptions(Option.AS_PATH_LIST));
        if (logger.isDebugEnabled()) {
            for (String p : modified) {
                logger.debug("Put path {} key {} value {}", p, key, value);
            }
        }
        return this;
    }

    private JsonPath pathFromCache(String path, Predicate[] filters) {
        Cache cache = CacheProvider.getCache();
        String cacheKey = filters == null || filters.length == 0
                          ? path : Utils.concat(path, Arrays.toString(filters));
        JsonPath jsonPath = cache.get(cacheKey);
        if (jsonPath == null) {
            jsonPath = compile(path, filters);
            cache.put(cacheKey, jsonPath);
        }
        return jsonPath;
    }

    private static final class LimitingEvaluationListener implements EvaluationListener {
        private final int limit;

        private LimitingEvaluationListener(int limit) {
            this.limit = limit;
        }

        @Override
        public EvaluationContinuation resultFound(FoundResult found) {
            if (found.index() == limit - 1) {
                return EvaluationContinuation.ABORT;
            } else {
                return EvaluationContinuation.CONTINUE;
            }
        }
    }
}
