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

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * A servlet used for creating and running a new simulation associated to an experiment,
 * and updating experimentsToSimulations database entity.
 */
@WebServlet("/new-experiment-simulation")
public class NewExperimentSimulationServlet extends HttpServlet {
    // todo: implement worker servlet (doPost).
}
