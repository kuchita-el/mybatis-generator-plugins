package com.github.kuchita_el.mybatis_generator_plugins.system_test.utils

import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactoryBuilder

/**
 * SqlSessionを開始する。
 * 使用後にクローズする必要がある。
 */
fun openSqlSession(driver: String, url: String, username: String, password: String): SqlSession {
    Resources.getResourceAsStream("mybatis-config.xml").use {
        val properties =
            mapOf("driver" to driver, "url" to url, "username" to username, "password" to password)
                .toProperties()
        val sqlSessionFactory = SqlSessionFactoryBuilder()
            .build(it, properties)
        return sqlSessionFactory.openSession()
    }
}
