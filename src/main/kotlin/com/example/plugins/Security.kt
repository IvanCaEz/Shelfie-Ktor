package com.example.plugins

import com.example.models.UserPrincipal
import com.example.models.myRealm
import com.example.models.userTable
import io.ktor.server.application.*
import io.ktor.server.auth.*


fun Application.configureSecurity(){
    install(Authentication) {
        digest("auth-digest") {
            realm = myRealm
            digestProvider { userName, realm ->
                userTable[userName]
            }
            validate { credentials ->
                if (credentials.userName.isNotEmpty()) {
                    UserPrincipal(credentials.userName, credentials.realm)
                } else {
                    null
                }
            }
        }
    }
}
