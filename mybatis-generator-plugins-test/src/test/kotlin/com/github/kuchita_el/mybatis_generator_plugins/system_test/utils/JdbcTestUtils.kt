package com.github.kuchita_el.mybatis_generator_plugins.system_test.utils

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

fun getConnection(driverClass: String, jdbcUrl: String, username: String, password: String): Connection {
    Class.forName(driverClass)
    return DriverManager.getConnection(jdbcUrl, username, password)
}

fun <Row> selectList(preparedStatement: PreparedStatement, rowCreator: (resultSet: ResultSet) -> Row): List<Row> {
    preparedStatement.executeQuery().use {
        val mutableList = mutableListOf<Row>()
        while (it.next()) {
            mutableList.add(rowCreator(it))
        }
        return mutableList.toList()
    }
}

fun <Row> selectOne(preparedStatement: PreparedStatement, rowCreator: (resultSet: ResultSet) -> Row): Result<Row?> {
    preparedStatement.executeQuery().use {
        var result: Row? = null
        while (it.next()) {
            if (result == null) {
                result = rowCreator(it)
                continue
            } else {
                return Result.failure(RuntimeException("結果が1行ではありませんでした。"))
            }

        }
        return Result.success(result)
    }

}
