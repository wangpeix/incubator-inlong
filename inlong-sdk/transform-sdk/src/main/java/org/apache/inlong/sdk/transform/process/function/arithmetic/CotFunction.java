/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.inlong.sdk.transform.process.function.arithmetic;

import org.apache.inlong.sdk.transform.decode.SourceData;
import org.apache.inlong.sdk.transform.process.Context;
import org.apache.inlong.sdk.transform.process.function.FunctionConstant;
import org.apache.inlong.sdk.transform.process.function.TransformFunction;
import org.apache.inlong.sdk.transform.process.operator.OperatorTools;
import org.apache.inlong.sdk.transform.process.parser.ValueParser;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Function;

import java.math.BigDecimal;

/**
 * CotFunction  ->  cot(numeric)
 * description:
 * - Return NULL if 'numeric' is NULL;
 * - Return the cotangent of the 'numeric' (in radians).
 */
@Slf4j
@TransformFunction(type = FunctionConstant.ARITHMETIC_TYPE, names = {
        "cot"}, parameter = "(Numeric numeric)", descriptions = {
                "- Return \"\" if 'numeric' is NULL;",
                "- Return the cotangent of the 'numeric' (in radians)."}, examples = {
                        "cot(1) = 0.6420926159343306",
                        "cot(0.5) = 1.830487721712452",
                        "cot(-1) = -0.6420926159343306"
                })
public class CotFunction implements ValueParser {

    private final ValueParser valueParser;

    public CotFunction(Function expr) {
        this.valueParser = OperatorTools.buildParser(expr.getParameters().getExpressions().get(0));
    }

    @Override
    public Object parse(SourceData sourceData, int rowIndex, Context context) {
        Object valueObj = valueParser.parse(sourceData, rowIndex, context);
        if (valueObj == null) {
            return null;
        }
        BigDecimal value = OperatorTools.parseBigDecimal(valueObj);

        // Calculate tan(x) and take the inverse to find cot(x)
        double tanValue = Math.tan(value.doubleValue());
        if (tanValue == 0) {
            throw new ArithmeticException("Cotangent undefined for this input, tan(x) is zero.");
        }
        return 1.0 / tanValue;
    }
}
