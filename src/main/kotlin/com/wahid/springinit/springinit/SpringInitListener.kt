package com.wahid.springinit.springinit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project

class SpringInitListener : AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        val project = p0.getData(CommonDataKeys.PROJECT)

        if (project != null){


        }
    }


    private fun edit(project: Project){
        project

    }
}