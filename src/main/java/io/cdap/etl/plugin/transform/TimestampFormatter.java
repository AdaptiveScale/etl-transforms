/*
 * Copyright © 2015 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package io.cdap.etl.plugin.transform;

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.cdap.api.data.format.StructuredRecord;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.cdap.api.plugin.PluginConfig;
import io.cdap.cdap.etl.api.Emitter;
import io.cdap.cdap.etl.api.Transform;
import io.cdap.cdap.etl.api.TransformContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.Nullable;

/**
 * ETL Transform that formats a timestamp into a human readable string.
 */
// TODO: annotate the plugin
public class TimestampFormatter extends Transform<StructuredRecord, StructuredRecord> {
  private final Map<Schema, Schema> schemaCache = Maps.newHashMap();
  private SimpleDateFormat dateFormat;
  // TODO: add a config variable

  public static class Config extends PluginConfig {
    @Description("name of the field that contains the timestamp. Defaults to 'ts'.")
    @Nullable
    private final String timestampField;

    @Description("date pattern to use when formatting the timestamp." +
            " See http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html for more detail." +
            " Defaults to 'yyyy-MM-dd'T'HH:mm:ss.SSS'")
    @Nullable
    private final String datePattern;

    @Description("name of the field to add containing the formatted timestamp. Defaults to 'date'.")
    @Nullable
    private final String dateField;

    @Description("whether the original timestamp field should be dropped. Defaults to true.")
    @Nullable
    private final Boolean dropTimestamp;

    // TODO: add a constructor to set plugin defaults
  }

  @Override
  public void initialize(TransformContext context) throws Exception {
    super.initialize(context);
    dateFormat = new SimpleDateFormat(config.datePattern);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  @Override
  public void transform(StructuredRecord input, Emitter<StructuredRecord> emitter) throws Exception {
    // TODO: get the timestamp from the input record
    Date date = new Date(ts);
    String dateString = dateFormat.format(date);

    Schema outputSchema = getOutputSchema(input.getSchema());

    // copy rest of input fields to the output
    StructuredRecord.Builder builder = StructuredRecord.builder(outputSchema);

    // TODO: set output record fields
    for (Schema.Field field : outputSchema.getFields()) {
      if (!field.getName().equals(config.dateField)) {
        builder.set(field.getName(), input.get(field.getName()));
      }
    }
    emitter.emit(builder.build());
  }

  private Schema getOutputSchema(Schema inputSchema) {
    Schema outputSchema = schemaCache.get(inputSchema);
    if (outputSchema != null) {
      return outputSchema;
    }

    List<Schema.Field> outputFields = Lists.newArrayList();
    outputFields.add(Schema.Field.of(config.dateField, Schema.of(Schema.Type.STRING)));
    if (config.dropTimestamp) {
      for (Schema.Field inputField : inputSchema.getFields()) {
        if (!inputField.getName().equals(config.timestampField)) {
          outputFields.add(inputField);
        }
      }
    } else {
      outputFields.addAll(inputSchema.getFields());
    }
    outputSchema = Schema.recordOf(inputSchema.getRecordName() + ".formatted", outputFields);
    schemaCache.put(inputSchema, outputSchema);
    return outputSchema;
  }
}

