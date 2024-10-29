/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.vaticle.typedb.common.yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.BiConsumer;

import static com.vaticle.typedb.common.util.Objects.className;

public abstract class YAML {

    public static YAML load(java.lang.String yaml) {
        return wrap(new org.yaml.snakeyaml.Yaml().load(yaml));
    }

    public static YAML load(java.lang.String yaml, org.yaml.snakeyaml.LoaderOptions options) {
        return wrap(new org.yaml.snakeyaml.Yaml(options).load(yaml));
    }

    public static YAML load(Path filePath) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(filePath.toFile());
        return wrap(new org.yaml.snakeyaml.Yaml().load(inputStream));
    }

    public static YAML load(Path filePath, org.yaml.snakeyaml.LoaderOptions options) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(filePath.toFile());
        return wrap(new org.yaml.snakeyaml.Yaml(options).load(inputStream));
    }

    private static YAML wrap(Object yaml) {
        if (yaml == null) return null;
        else if (yaml instanceof java.util.Map) {
            assert ((java.util.Map<Object, Object>) yaml).keySet().stream().allMatch(key -> key instanceof java.lang.String);
            return Map.wrap((java.util.Map<java.lang.String, Object>) yaml);
        } else if (yaml instanceof java.util.List) return List.wrap((java.util.List<Object>) yaml);
        else if (yaml instanceof java.lang.String) return new String((java.lang.String) yaml);
        else if (yaml instanceof java.lang.Integer) return new Int((int) yaml);
        else if (yaml instanceof java.lang.Double) return new Double((double) yaml);
        else if (yaml instanceof java.lang.Boolean) return new Boolean((boolean) yaml);
        else throw new IllegalStateException();
    }

    public boolean isMap() {
        return false;
    }

    public Map asMap() {
        throw classCastException(getClass(), Map.class);
    }

    public boolean isList() {
        return false;
    }

    public List asList() {
        throw classCastException(getClass(), List.class);
    }

    public boolean isString() {
        return false;
    }

    public String asString() {
        throw classCastException(getClass(), String.class);
    }

    public boolean isInt() {
        return false;
    }

    public Int asInt() {
        throw classCastException(getClass(), Int.class);
    }

    public boolean isDouble() {
        return false;
    }

    public Double asDouble() {
        throw classCastException(getClass(), Double.class);
    }

    public boolean isBoolean() {
        return false;
    }

    public Boolean asBoolean() {
        throw classCastException(getClass(), Boolean.class);
    }

    private ClassCastException classCastException(Class<?> from, Class<?> to) {
        return new ClassCastException(java.lang.String.format("Illegal cast from '%s' to '%s'.", className(from),
                className(to)));
    }

    public static class Map extends YAML {

        private final java.util.Map<java.lang.String, YAML> map;

        public Map(java.util.Map<java.lang.String, YAML> map) {
            this.map = map;
        }

        private static Map wrap(java.util.Map<java.lang.String, Object> source) {
            java.util.Map<java.lang.String, YAML> map = new LinkedHashMap<>();
            for (java.lang.String key : source.keySet()) {
                map.put(key, YAML.wrap(source.get(key)));
            }
            return new Map(map);
        }

        public java.util.Map<java.lang.String, YAML> content() {
            return map;
        }

        public boolean containsKey(java.lang.String key) {
            return map.containsKey(key);
        }

        public Set<java.lang.String> keys() {
            return map.keySet();
        }

        public YAML get(java.lang.String key) {
            return map.get(key);
        }

        public void put(java.lang.String key, YAML value) {
            map.put(key, value);
        }

        public void forEach(BiConsumer<java.lang.String, YAML> consumer) {
            map.forEach(consumer);
        }

        @Override
        public boolean isMap() {
            return true;
        }

        @Override
        public Map asMap() {
            return this;
        }
    }

    public static class List extends YAML {

        private final java.util.List<YAML> list;

        private List(java.util.List<YAML> list) {
            this.list = list;
        }

        static List wrap(java.util.List<Object> source) {
            java.util.List<YAML> yamlList = new ArrayList<>();
            for (Object e : source) {
                yamlList.add(YAML.wrap(e));
            }
            return new List(yamlList);
        }

        public java.util.List<YAML> content() {
            return list;
        }

        public Iterator<YAML> iterator() {
            return list.iterator();
        }

        @Override
        public boolean isList() {
            return true;
        }

        @Override
        public List asList() {
            return this;
        }
    }

    public static class String extends YAML {

        private final java.lang.String value;

        private String(java.lang.String string) {
            this.value = string;
        }

        public java.lang.String value() {
            return value;
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public String asString() {
            return this;
        }

        @Override
        public java.lang.String toString() {
            return value + "[string]";
        }
    }

    public static class Int extends YAML {

        private final int value;

        private Int(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public Int asInt() {
            return this;
        }

        @Override
        public java.lang.String toString() {
            return value + "[int]";
        }
    }

    public static class Double extends YAML {

        private final double value;

        private Double(double value) {
            this.value = value;
        }

        public double value() {
            return value;
        }

        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public Double asDouble() {
            return this;
        }

        @Override
        public java.lang.String toString() {
            return value + "[double]";
        }
    }

    public static class Boolean extends YAML {

        private final boolean value;

        private Boolean(boolean value) {
            this.value = value;
        }

        public boolean value() {
            return value;
        }

        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public Boolean asBoolean() {
            return this;
        }

        @Override
        public java.lang.String toString() {
            return value + "[boolean]";
        }
    }
}
