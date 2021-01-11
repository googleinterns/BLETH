// Copyright 2021 Google LLC
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

import com.google.research.bleth.utils.Queries;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for deleting simulations. */
@WebServlet("/delete-simulation")
public class DeleteSimulationServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String simulationId = request.getParameter("simulationId");
        String responseText = "Simulation has been deleted successfully.";
        try {
            Queries.delete(simulationId);
        } catch (Exception e) {
            responseText = "Something went wrong: " + e.getMessage();
        }
        response.setContentType("text/plain;");
        response.getWriter().println(responseText);
    }
}
