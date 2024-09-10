package com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update

import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3.xml.Member
import com.github.kuchita_el.mybatis_generator_plugins.system_test.select_for_update.mybatis3.xml.MemberMapper
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactoryBuilder
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
        val memberId = "memberId"
        val name = "name"
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

        openSqlSession(driverClassName, jdbcUrl, username, password)
            .use { session ->
                val memberMapper = session.getMapper(MemberMapper::class.java)
                val pgLocksMapper = session.getMapper(PgLocksMapper::class.java)
                val member = memberMapper.selectByPrimaryKeyForUpdate(memberId)

                assertAll(
                    { assertEquals(memberId, member.memberId) },
                    { assertEquals(name, member.name) },
                )

                val numberOfLockedRows = pgLocksMapper.countRowShareLocksByTableName("member")
                assertEquals(1, numberOfLockedRows)
                session.rollback()
            }

        openSqlSession(driverClassName, jdbcUrl, username, password)
            .use { session ->
                val pgLocksMapper = session.getMapper(PgLocksMapper::class.java)
                val numberOfLockedRows = pgLocksMapper.countRowShareLocksByTableName("member")
                assertEquals(0, numberOfLockedRows)
            }
    }


    /**
     * SqlSessionを開始する。
     * 使用後にクローズする必要がある。
     */
    private fun openSqlSession(driver: String, url: String, username: String, password: String): SqlSession {
        val configFile = this::class.java.getResourceAsStream("/mybatis-config.xml")
        val properties =
            mapOf("driver" to driver, "url" to url, "username" to username, "password" to password)
                .toProperties()
        val sqlSessionFactory = SqlSessionFactoryBuilder()
            .build(configFile, properties)
        return sqlSessionFactory.openSession()
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
