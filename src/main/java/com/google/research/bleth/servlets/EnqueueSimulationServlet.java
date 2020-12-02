package com.google.research.bleth.servlets;

import com.google.cloud.tasks.v2.AppEngineHttpRequest;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/enqueue-simulation")
public class EnqueueSimulationServlet extends HttpServlet {

    static final String projectId = "bleth-2020";
    static final String locationId = "europe-west1";
    static final String queueId = "simulations-queue";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try (CloudTasksClient client = CloudTasksClient.create()) {
            // Construct the fully qualified queue name.
            String queueName = QueueName.of(projectId, locationId, queueId).toString();

            // Construct the HTTP request (for the task).
            AppEngineHttpRequest httpRequest = AppEngineHttpRequest.newBuilder()
                    .setRelativeUri("/new-simulation?" + toQueryString(request))
                    .setHttpMethod(HttpMethod.POST)
                    .build();

            // Construct the task body.
            Task task = Task.newBuilder()
                    .setAppEngineHttpRequest(httpRequest)
                    .build();

            // Add the task to the queue.
            client.createTask(queueName, task);
        }

        response.setContentType("text/html;");
        response.getWriter().println("Task has been added to queue.");
    }

    private String toQueryString(HttpServletRequest request) {
        List<String> keyValuePairs = new ArrayList<>();
        for (String param : request.getParameterMap().keySet()) {
            keyValuePairs.add(param + "=" + request.getParameter(param));
        }
        return String.join("&", keyValuePairs);
    }
}