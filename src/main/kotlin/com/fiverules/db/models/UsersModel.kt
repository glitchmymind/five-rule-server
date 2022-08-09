package com.fiverules.db.models

import com.fiverules.models.UserDTO
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class User(id: EntityID<Long>) :  Entity<Long>(id) {
    companion object : EntityClass<Long, User>(Users)

    var login by Users.login
    var password by Users.password
    var name by Users.name
    var email by Users.email
    var avatar by Users.avatar
    var rating by Users.rating
    var token by Users.token
}

object Users : LongIdTable("users") {
    val login = varchar("login", 25)
    val password = varchar("password", 25)
    val name = varchar("name", 25)
    val email = varchar("email", 50)
    val avatar = varchar("avatar", 300).nullable()
    val rating = integer("rating")
    val token = varchar("token", 50) // JWT?

    fun insert(user: UserDTO) {
        transaction {
            Users.insert {
                it[login] = user.login
                it[password] = user.password
                it[name] = user.name
                it[email] = user.email
                it[avatar] = user.avatar
                it[rating] = user.rating
                it[token] = user.token
            }
        }
    }

    fun getUser(userId: Long): UserDTO? {
        return transaction{
            val userModel = User.findById(userId)
            if(userModel != null){
                UserDTO(
                    id = userModel.id.value,
                    login = userModel.login,
                    password = userModel.password,
                    name = userModel.name,
                    email = userModel.email,
                    avatar = userModel.avatar,
                    rating = userModel.rating,
                    token = userModel.token,
                )
            } else {
                null
            }
        }
    }

    fun updateUserToken(token: String) {
        transaction {
            Users.update {
                it[Users.token] = token
            }
        }
    }

    fun getUserByLogin(userLogin: String): UserDTO? {
        return transaction {
            val userModel = User.find { login eq userLogin }.firstOrNull()
            if (userModel != null) {
                UserDTO(
                    id = userModel.id.value,
                    login = userModel.login,
                    password = userModel.password,
                    name = userModel.name,
                    email = userModel.email,
                    avatar = userModel.avatar,
                    rating = userModel.rating,
                    token = userModel.token,
                )
            } else {
                null
            }
        }
    }

    fun isUserExist(login: String) : Boolean = getUserByLogin(login) != null
}