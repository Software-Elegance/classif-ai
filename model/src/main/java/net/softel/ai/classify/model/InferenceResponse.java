/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package net.softel.ai.classify.model;

import javax.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class InferenceResponse {

    private String outputReference;

    private List<InferredObject> inferredObjects;

    /**
     * Deserialization constructor
     */
    public InferenceResponse(){}

    public InferenceResponse(@NotNull  List<InferredObject> inferredObjects,
                             String outputReference) {
        this.inferredObjects = inferredObjects;
        this.outputReference = outputReference;
    }

}
