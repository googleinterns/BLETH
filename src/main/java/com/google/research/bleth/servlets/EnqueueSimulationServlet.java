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

import com.google.cloud.tasks.v2.AppEngineHttpRequest;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import com.google.protobuf.ByteString;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet used for enqueuing a task targeted at endpoint '/new-simulation',
 * in order to create and run a new simulation.
 */
@WebServlet("/enqueue-simulation")
public class EnqueueSimulationServlet extends HttpServlet {

    static final String PROJECT_ID = "bleth-2020";
    static final String LOCATION_ID = "europe-west1";
    static final String QUEUE_ID = "simulations-queue";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // If servlet runs on localhost, forward request without creating and enqueuing a task.
        if (request.getServerName().equals("localhost") && request.getServerPort() == 8080) {
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/new-simulation");
            dispatcher.forward(request, response);
            return;
        }

        try (CloudTasksClient client = CloudTasksClient.create()) {
            // Construct the HTTP request (for the task).
            AppEngineHttpRequest httpRequest = AppEngineHttpRequest.newBuilder()
                    .setRelativeUri("/new-simulation")
                    .setHttpMethod(HttpMethod.POST)
                    .putHeaders("Content-Type", request.getContentType())
                    .setBody(ByteString.readFrom(request.getInputStream()))
                    .build();

            // Construct the task body.
            Task task = Task.newBuilder()
                    .setAppEngineHttpRequest(httpRequest)
                    .build();

            // Add the task to the queue.
            String queueName = QueueName.of(PROJECT_ID, LOCATION_ID, QUEUE_ID).toString();
            client.createTask(queueName, task);
        }

        response.setContentType("text/plain;");
        response.getWriter().println("Task has been added to queue.");
    }
}