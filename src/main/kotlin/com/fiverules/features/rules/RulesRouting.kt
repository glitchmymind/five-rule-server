package com.fiverules.features.rules

import com.fiverules.db.models.Rules
import com.fiverules.db.models.Tasks
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

fun Route.rulesRouting() {

    val rulesInteractor: RulesInteractor by inject()

    route("/create") {
        post("/rule") {
            rulesInteractor.createNewRule(call)
        }

        post("/task") {
            rulesInteractor.createNewTask(call)
        }
    }

    route("/delete") {
        post("/rule") {
            rulesInteractor.deleteRule(call)
        }

        post("/task") {
            rulesInteractor.deleteTask(call)
        }
    }

    post("attach/task") {
        rulesInteractor.addTasksToRule(call)
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