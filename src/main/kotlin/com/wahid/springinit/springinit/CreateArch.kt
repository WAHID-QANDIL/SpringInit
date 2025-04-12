package com.wahid.springinit.springinit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import org.gradle.tooling.GradleConnector
import java.io.File

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

    /**
     * Runs the given Gradle task in a background thread.
     *
     * @param project the current project
     * @param gradleTask the name of the Gradle task to run
     */
    private fun runGradleTask(project: Project, gradleTask: String) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Running Gradle Task: $gradleTask") {
            override fun run(indicator: ProgressIndicator) {
                try {
                    val basePath = project.basePath ?: throw Exception("Project base path not found")
                    val projectDir = File(basePath)
                    // Initialize and configure the GradleConnector.
                    val connector = GradleConnector.newConnector().forProjectDirectory(projectDir)

                    // Connect, execute the task, and properly manage the connection.
                    connector.connect().use { connection ->
                        connection.newBuild().forTasks(gradleTask).run()
                    }
                } catch (ex: Exception) {
                    ApplicationManager.getApplication().invokeLater {
                        Messages.showErrorDialog(project, "Error running Gradle task: ${ex.message}", "Error")
                    }
                }
            }
        })
    }
}
