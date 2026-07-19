package com.jummania;

import java.lang.reflect.Field;
import java.util.HashMap;

record FieldMap(Field[] fields, HashMap<String, Field> map) {

}