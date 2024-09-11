package com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update

import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3.xml.Member
import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3.xml.MemberMapper
import com.github.kuchita_el.mybatis_generator_plugins.system_test.utils.getConnection
import com.github.kuchita_el.mybatis_generator_plugins.system_test.utils.openSqlSession
import com.github.kuchita_el.mybatis_generator_plugins.system_test.utils.selectOne
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.time.LocalDateTime

class SelectForUpdatePluginTest {

    @Rule
    @JvmField
    val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
        .withInitScripts("sql/table.sql")

    @Test
    fun トランザクション内でselectForUpdateを呼び出したら行ロックを獲得すること() {
        val memberId = "testMemberId"
        val name = "testName"
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()
        val (driverClassName, jdbcUrl, username, password) = runPostgres(postgres)
        openSqlSession(driverClassName, jdbcUrl, username, password)
            .use { session ->
                val memberMapper = session.getMapper(MemberMapper::class.java)
                val member = Member()
                member.memberId = memberId
                member.name = name
                member.createdAt = createdAt
                member.updatedAt = updatedAt
                memberMapper.insert(member)
                session.commit()
            }

        val connection = getConnection(driverClassName, jdbcUrl, username, password)
        val statement = connection.prepareStatement(
            """
                select
                    count(*)
                from
                    pg_locks
                    left outer join pg_class on pg_locks.relation = pg_class.oid
                where
                    pg_locks.mode = 'RowShareLock'
                    and pg_class.relname = ?
                """.trimIndent()
        )
        statement.setString(1, "member")

        openSqlSession(driverClassName, jdbcUrl, username, password)
            .use { session ->
                val memberMapper = session.getMapper(MemberMapper::class.java)
                val member = memberMapper.selectByPrimaryKeyForUpdate(memberId)

                assertAll(
                    { assertEquals(memberId, member.memberId) },
                    { assertEquals(name, member.name) },
                )

                val numberOfLockedRows = selectOne(statement, { it.getInt(1) })
                assertEquals(1, numberOfLockedRows.getOrThrow())
                session.rollback()
            }

        val numberOfLockedRows = selectOne(statement, {it.getInt(1) })
        assertEquals(0, numberOfLockedRows.getOrThrow())

        statement.close()
        connection.close()
    }

    /**
     * Postgresを起動する
     */
    private fun runPostgres(container: PostgreSQLContainer<out PostgreSQLContainer<*>>): StartedDatabase {
        postgres.start()
        val jdbcUrl = container.jdbcUrl
        val username = container.username
        val password = container.password
        val driverClassName = container.driverClassName
        return StartedDatabase(driverClassName, jdbcUrl, username, password)
    }

    data class StartedDatabase(val driverClassName: String, val jdbcUrl: String, val username: String, val password: String)
}
