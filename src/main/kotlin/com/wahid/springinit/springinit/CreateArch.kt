package com.wahid.springinit.springinit

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.jetbrains.plugins.gradle.util.GradleConstants

class CreateArch : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return


        // Ask the user to choose a generation pattern.
        val options = arrayOf("MVC", "Clean Architecture")
        val selected = Messages.showDialog(
            project,
            "Please choose the pattern",
            "Pattern Generation",
            options,
            0,
            Messages.getQuestionIcon()
        )

        when (selected) {
            0 -> runGradleTask(project, "generateMVC")
            1 -> runGradleTask(project, "generateCleanArchitecture")
            else -> Messages.showInfoMessage(project, "No valid pattern selected.", "Cancelled")
        }
    }

}
fun runGradleTask(project: Project?, gradleTask: String) {
    if (project == null) return

    // Create task execution settings
    val settings = ExternalSystemTaskExecutionSettings().apply {
        externalProjectPath = project.basePath // your project's base directory
        taskNames = listOf(gradleTask) // e.g., "applyFormatting"
        vmOptions = "" // additional JVM options if needed
        externalSystemIdString = GradleConstants.SYSTEM_ID.id
    }

    // Run the task without showing the Gradle tool window
    ExternalSystemUtil.runTask(
        settings,
        DefaultRunExecutor.EXECUTOR_ID,
        project,
        GradleConstants.SYSTEM_ID,
        null, // optional callback can be provided here
        ProgressExecutionMode.IN_BACKGROUND_ASYNC,
        false // do not activate the tool window before running
    )
}