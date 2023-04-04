/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bf2.cos.connector.camel.serdes.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.bf2.cos.connector.camel.serdes.MimeType;
import org.bf2.cos.connector.camel.serdes.format.spi.DataTypeConverter;
import org.bf2.cos.connector.camel.serdes.format.spi.annotations.DataType;

/**
 * Data type uses Jackson data format to unmarshal Exchange body to generic JsonNode representation.
 */
@DataType(name = "application-x-struct", mediaType = "application/x-struct")
public class JsonStructDataType implements DataTypeConverter {

    @Override
    public void convert(Exchange exchange) {
        try {
            Object unmarshalled = Json.MAPPER.reader().forType(JsonNode.class).readValue(getBodyAsStream(exchange));
            exchange.getMessage().setBody(unmarshalled);

            exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, MimeType.STRUCT);
        } catch (InvalidPayloadException | IOException e) {
            throw new CamelExecutionException("Failed to apply Json input data type on exchange", exchange, e);
        }
    }

    private InputStream getBodyAsStream(Exchange exchange) throws InvalidPayloadException {
        InputStream bodyStream = exchange.getMessage().getBody(InputStream.class);

        if (bodyStream == null) {
            bodyStream = new ByteArrayInputStream(exchange.getMessage().getMandatoryBody(byte[].class));
        }

        return bodyStream;
    }
}