package com.wahid.springinit.springinit

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class SpringInitManager : ProjectManagerListener {
    override fun projectClosed(project: Project) {
        super.projectClosed(project)
    }

    override fun projectClosing(project: Project) {
        super.projectClosing(project)
    }

    override fun projectClosingBeforeSave(project: Project) {
        super.projectClosingBeforeSave(project)
    }
}