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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.research.bleth.simulator.StrategiesMapper;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for displaying all string representations of movement and awakeness strategies. */
@WebServlet("/list-strategies")
public class ListStrategiesServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson(); // Used for json serialization.

        // Construct a hash map storing 'movement' or 'awakeness' as key,
        // and list of strings representing available strategies as value.
        HashMap<String, JsonArray> strategies = new HashMap<>();
        JsonArray movementStrategies = (JsonArray) gson.toJsonTree(StrategiesMapper.listMovementStrategies());
        JsonArray awakenessStrategies = (JsonArray) gson.toJsonTree(StrategiesMapper.listAwakenessStrategies());
        strategies.put("movement", movementStrategies);
        strategies.put("awakeness", awakenessStrategies);

        // Write hash map to response.
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(strategies));
    }
}
