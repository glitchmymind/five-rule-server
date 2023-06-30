package com.fiverules.features.rules

import com.fiverules.db.models.Rules
import com.fiverules.db.models.Tasks
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.rulesRouting() {

    route("/create") {
        post("/rule") {
            RulesInteractor.createNewRule(call)
        }

        post("/task") {
            RulesInteractor.createNewTask(call)
        }
    }

    route("/delete") {
        post("/rule") {
            RulesInteractor.deleteRule(call)
        }

        post("/task") {
            RulesInteractor.deleteTask(call)
        }
    }

    post("attach/task") {
        RulesInteractor.addTasksToRule(call)
    }

    get("/rules") {

        val data = transaction { Rules.fetchRules() }
        call.respond(RuleResponseData(rules = data))
    }

    get("/tasks") {
        val data = transaction { Tasks.fetchTasks() }
        call.respond(data)
    }
}