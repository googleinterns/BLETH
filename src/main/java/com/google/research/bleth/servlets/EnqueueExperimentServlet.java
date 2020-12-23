// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.research.bleth.servlets;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet used for enqueuing multiple tasks targeted at endpoint '/new-experiment-simulation',
 * in order to create and run a new experiment.
 */
@WebServlet("/enqueue-experiment")
public class EnqueueExperimentServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String experimentTitle = request.getParameter("experimentTitle");
        int beaconsNum = Integer.parseInt(request.getParameter("beaconsNum"));
        Set<PropertyWrapper> roundsNumValues = createIntValues(request, "roundsNum");
        Set<PropertyWrapper> rowsNumValues = createIntValues(request, "rowsNum");
        Set<PropertyWrapper> colsNumValues = createIntValues(request, "colsNum");
        Set<PropertyWrapper> observersNumValues = createIntValues(request, "observersNum");
        Set<PropertyWrapper> awakenessCycleValues = createIntValues(request, "awakenessCycle");
        Set<PropertyWrapper> awakenessDurationValues = createIntValues(request, "awakenessDuration");
        Set<PropertyWrapper> transmissionThresholdRadiusValues = createDoubleValues(request, "transmissionThresholdRadius");

        Set<List<PropertyWrapper>> configurations = Sets.cartesianProduct(
                roundsNumValues,
                rowsNumValues,
                colsNumValues,
                observersNumValues,
                awakenessCycleValues,
                awakenessDurationValues,
                transmissionThresholdRadiusValues
        );

        // todo: for p in product:
            // todo: extract p's parameters to create an http request
            // todo: create a task and enqueue it

        String testOutput = new Gson().toJson(configurations);
        response.setContentType("application/json;");
        response.getWriter().println(testOutput);
    }

    @AutoValue
    public static abstract class PropertyWrapper {
        public static PropertyWrapper create(String property, Number value) {
            return new AutoValue_EnqueueExperimentServlet_PropertyWrapper(property, value);
        }
        public abstract String property();
        public abstract Number value();
    }

    private Set<PropertyWrapper> createIntValues(HttpServletRequest request, String parameter) {
        int lower = Integer.parseInt(request.getParameter("lower" + upperCaseFirstChar(parameter)));
        int upper = Integer.parseInt(request.getParameter("upper" + upperCaseFirstChar(parameter)));
        int step = Integer.parseInt(request.getParameter("step" + upperCaseFirstChar(parameter)));
        Set<PropertyWrapper> values = new HashSet<>();
        for (int value = lower; value <= upper; value += step) {
            values.add(PropertyWrapper.create(parameter, value));
        }
        return ImmutableSet.copyOf(values);
    }

    private Set<PropertyWrapper> createDoubleValues(HttpServletRequest request, String parameter) {
        double lower = Double.parseDouble(request.getParameter("lower" + upperCaseFirstChar(parameter)));
        double upper = Double.parseDouble(request.getParameter("upper" + upperCaseFirstChar(parameter)));
        double step = Double.parseDouble(request.getParameter("step" + upperCaseFirstChar(parameter)));
        Set<PropertyWrapper> values = new HashSet<>();
        for (double value = lower; value <= upper; value += step) {
            values.add(PropertyWrapper.create(parameter, value));
        }
        return ImmutableSet.copyOf(values);
    }

    private String upperCaseFirstChar(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
