package com.google.research.bleth.servlets;

import com.google.cloud.tasks.v2.AppEngineHttpRequest;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("enqueue-simulation")
public class EnqueueSimulationServlet extends HttpServlet {

    static final String projectId = "bleth-2020";
    static final String locationId = "europe-west1";
    static final String queueId = "simulations-queue";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try (CloudTasksClient client = CloudTasksClient.create()) {
            // Construct the fully qualified queue name.
            String queueName = QueueName.of(projectId, locationId, queueId).toString();

            // Construct the task body.
            Task task =
                    Task.newBuilder()
                            .setAppEngineHttpRequest(
                                    AppEngineHttpRequest.newBuilder()
                                            .setRelativeUri("/new-simulation?" + toQueryString(request))
                                            .setHttpMethod(HttpMethod.POST)
                                            .build())
                            .build();

            // Add the task to the queue.
            client.createTask(queueName, task);
        }

        response.sendRedirect("/");
    }

    private String toQueryString(HttpServletRequest request) {
        StringBuilder queryString = new StringBuilder();
        for (String param : request.getParameterMap().keySet()) {
            queryString.append(param).append("=").append(request.getParameter(param));
        }
        return queryString.toString();
    }
}