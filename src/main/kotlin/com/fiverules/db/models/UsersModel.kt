package com.fiverules.db.models

import com.fiverules.db.models.Users.nullable
import com.fiverules.models.UserDTO
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class User(id: EntityID<Long>) : Entity<Long>(id) {
    companion object : EntityClass<Long, User>(Users)

    var login by Users.login
    var password by Users.password
    var phoneNumber by Users.phoneNumber
    var name by Users.name
    var email by Users.email
    var avatar by Users.avatar
    var rating by Users.rating
    var isBlocked by Users.isBlocked
}

object Users : LongIdTable("users") {
    val login = varchar("login", 25).nullable()
    val password = varchar("password", 25).nullable()
    val phoneNumber = varchar("phoneNumber", 20).nullable()
    val name = varchar("name", 25).nullable()
    val email = varchar("email", 50).nullable()
    val avatar = varchar("avatar", 300).nullable()
    val rating = integer("rating")
    val isBlocked = bool("isBlocked").default(false)

    fun insert(user: UserDTO) {
        transaction {
            Users.insert {
                it[login] = user.login
                it[phoneNumber] = user.phoneNumber
                it[password] = user.password
                it[name] = user.name
                it[email] = user.email
                it[avatar] = user.avatar
                it[rating] = user.rating
                it[isBlocked] = user.isBlocked
            }
        }
    }

    fun getUser(userId: Long): UserDTO? {
        return transaction {
            User.findById(userId)?.mapToDTO()
        }
    }

    fun getUserByLogin(userLogin: String): UserDTO? {
        return transaction {
            User.find { login eq userLogin }.firstOrNull()?.mapToDTO()
        }
    }

    fun getUserByPhone(userPhone: String): UserDTO? {
        return transaction {
            User.find { phoneNumber eq userPhone }.firstOrNull()?.mapToDTO()
        }
    }

    fun isLoginExist(login: String): Boolean = getUserByLogin(login) != null

    fun isPhoneExist(phone: String): Boolean = getUserByPhone(phone) != null

    private fun User.mapToDTO(): UserDTO {
        return UserDTO(
            id = id.value,
            login = login,
            password = password,
            phoneNumber = phoneNumber,
            name = name,
            email = email,
            avatar = avatar,
            rating = rating,
            isBlocked = isBlocked,
        )
    }
}