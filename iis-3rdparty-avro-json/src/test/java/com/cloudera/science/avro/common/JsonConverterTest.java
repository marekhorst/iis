/**
 * Copyright (c) 2013, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.science.avro.common;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class JsonConverterTest {

  private static Schema.Field sf(String name, Schema schema) {
    return new Schema.Field(name, schema, "", null);
  }
  
  private static Schema.Field sf(String name, Schema.Type type) {
    return sf(name, sc(type));
  }
  
  private static Schema sc(Schema.Type type) {
    return Schema.create(type);
  }
  
  private static Schema sr(Schema.Field... fields) {
    return Schema.createRecord(Arrays.asList(fields));
  }
  
  Schema.Field f1 = sf("field1", Type.LONG);
  Schema.Field f2 = sf("field2", Schema.createArray(sc(Type.BOOLEAN)));
  Schema.Field f3Map = sf("field3", Schema.createMap(sc(Type.STRING)));
  Schema.Field f3Rec = sf("field3", sr(sf("key", Type.STRING)));
  
  @Test
  public void testBasicWithMap() throws Exception {
    JsonConverter jc = new JsonConverter(sr(f1, f2, f3Map));
    String json = "{\"field1\": 1729, \"field2\": [true, true, false], \"field3\": {\"key\": \"value\"}}";
    GenericRecord r = jc.convert(json);
    assertEquals(json, r.toString());
  }
  
  @Test
  public void testBasicWithRecord() throws Exception {
    JsonConverter jc = new JsonConverter(sr(f1, f2, f3Rec));
    String json = "{\"field1\": 1729, \"field2\": [true, true, false], \"field3\": {\"key\": \"value\"}}";
    GenericRecord r = jc.convert(json);
    assertEquals(json, r.toString());
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testMissingRequiredField() throws Exception {
    JsonConverter jc = new JsonConverter(sr(f1, f2, f3Rec));
    String json = "{\"field2\": [true, true, false], \"field3\": {\"key\": \"value\"}}";
    jc.convert(json);
  }
  
  @Test
  public void testMissingNullableField() throws Exception {
    Schema optional = Schema.createUnion(
        ImmutableList.of(Schema.create(Type.NULL), Schema.create(Type.DOUBLE)));
    Schema.Field f4 = sf("field4", optional);
    JsonConverter jc = new JsonConverter(sr(f1, f2, f3Rec, f4));
    String json = "{\"field1\": 1729, \"field2\": [true, true, false], \"field3\": {\"key\": \"value\"}}";
    GenericRecord r = jc.convert(json);
    String expect = "{\"field1\": 1729, \"field2\": [true, true, false], \"field3\": {\"key\": \"value\"}, \"field4\": null}";
    assertEquals(expect, r.toString());
  }
  
  @Test
  public void testMissingNullableArrayField() throws Exception {
	  Schema.Parser parser = new Schema.Parser();
      Schema schemaOptionalArray = parser.parse("[\"null\", {\"type\": \"array\", \"items\": \"int\"}]");
      Schema.Field f4 = sf("field4", schemaOptionalArray);
      JsonConverter jc = new JsonConverter(sr(f1, f2, f3Rec, f4));
      String json = "{\"field1\": 1729, \"field2\": [true, true, false], \"field3\": {\"key\": \"value\"}}";
      GenericRecord r = jc.convert(json);
      String expect = "{\"field1\": 1729, \"field2\": [true, true, false], \"field3\": {\"key\": \"value\"}, \"field4\": null}";
      assertEquals(expect, r.toString());	  
  }
}
