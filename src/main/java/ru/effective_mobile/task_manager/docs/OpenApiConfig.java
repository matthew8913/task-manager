package ru.effective_mobile.task_manager.docs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info =
        @Info(
            title = "Task-manager API",
            description = "API менеджера задач.",
            version = "1.0.0",
            contact = @Contact(name = "Matthew Yurkevich", email = "matvey.yurkevich@gmail.com")))
public class OpenApiConfig {}
